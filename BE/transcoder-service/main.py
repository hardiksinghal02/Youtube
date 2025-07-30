from confluent_kafka import Consumer, Producer
import requests
import subprocess
import os
from concurrent.futures import ThreadPoolExecutor
import json
import shutil
import logging

# ===================== Logging Setup =====================
logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")

# ===================== Constants =====================
MAX_WORKERS = 1
TMP_DIR = "/tmp"
TRANSCODED_DIR = os.path.join(TMP_DIR, "transcoded")
STORAGE_SERVICE_URL = "http://storage-service.youtube.svc.cluster.local:5000"

# ===================== Kafka Consumer Setup =====================
conf = {
    'bootstrap.servers': '192.168.64.50:9092',
    'group.id': 'transcoding-consumer-group',
    'auto.offset.reset': 'earliest'
}

producer = Producer(conf)

consumer = Consumer(conf)
topic = 'transcoding-topic'
consumer.subscribe([topic])
executor = ThreadPoolExecutor(max_workers=MAX_WORKERS)


# ===================== Core Functions =====================
def get_message_from_kafka():
    msg = consumer.poll(timeout=1.0)
    if msg is None:
        return None
    if msg.error():
        logging.error(f"Kafka error: {msg.error()}")
        return None

    raw_value = msg.value()
    if raw_value is None:
        logging.warning("⚠️ Received empty message")
        return None

    try:
        data = json.loads(raw_value.decode('utf-8'))
        raw_path = data.get("rawFilePath")
        dest_path = data.get("destinationPath")
        content_id = data.get("contentId")

        if not raw_path or not dest_path or not content_id:
            logging.warning("⚠️ Missing required keys in message")
            return None

        consumer.commit(msg)
        return {"rawFilePath": raw_path, "destinationPath": dest_path, "contentId" : content_id}

    except json.JSONDecodeError as e:
        logging.error(f"❌ JSON decode failed: {e}")
        return None


def download_file(file_path, input_path):
    url = f"{STORAGE_SERVICE_URL}/download/{file_path}"
    logging.info(f"📡 Downloading from: {url}")
    response = requests.get(url)

    if response.status_code == 200:
        with open(input_path, 'wb') as f:
            f.write(response.content)
        logging.info(f"✅ File downloaded: {input_path}")
        return True
    else:
        logging.error(f"❌ Failed to download {file_path}. Status: {response.status_code} | {response.text}")
        return False


def transcode_to_hls(input_path, output_dir):
    os.makedirs(output_dir, exist_ok=True)
    output_path = os.path.join(output_dir, 'output.m3u8')
    segment_pattern = os.path.join(output_dir, 'segment_%03d.ts')

    command = [
        'ffmpeg',
        '-i', input_path,
        '-c:v', 'libx264',
        '-c:a', 'aac',
        '-b:v', '2000k',
        '-b:a', '128k',
        '-ac', '2',
        '-hls_time', '10',
        '-hls_list_size', '0',
        '-hls_segment_filename', segment_pattern,
        '-f', 'hls',
        output_path
    ]

    try:
        logging.info(f"🎞️ Transcoding started: {input_path}")
        subprocess.run(command, check=True)
        logging.info(f"✅ Transcoding complete. Output at {output_path}")
        return True
    except subprocess.CalledProcessError as e:
        logging.error(f"❌ Transcoding failed: {e}")
        return False


def upload_to_storage(output_dir, upload_path):
    for root, _, files in os.walk(output_dir):
        for f_name in files:
            file_to_upload = os.path.join(root, f_name)
            with open(file_to_upload, 'rb') as file_data:
                files_payload = {'file': file_data}
                data_payload = {'path': f"{upload_path}/{f_name}"}
                upload_url = f"{STORAGE_SERVICE_URL}/upload"

                response = requests.post(upload_url, files=files_payload, data=data_payload)
                if response.status_code == 200:
                    logging.info(f"📤 Uploaded: {f_name}")
                else:
                    logging.error(f"❌ Upload failed for {f_name}. Status: {response.status_code} | {response.text}")
                    return False

    return True


def clear_tmp_directory(tmp_path=TMP_DIR):
    try:
        for filename in os.listdir(tmp_path):
            file_path = os.path.join(tmp_path, filename)
            try:
                if os.path.isfile(file_path) or os.path.islink(file_path):
                    os.unlink(file_path)
                elif os.path.isdir(file_path):
                    shutil.rmtree(file_path)
            except Exception as e:
                logging.error(f"❌ Error deleting {file_path}: {e}")
        logging.info(f"✅ Cleared: {tmp_path}")
    except Exception as e:
        logging.error(f"❌ Could not clear {tmp_path}: {e}")


def process_message(raw_file_path, destination_file_path, content_id):
    try:
        file_name = os.path.basename(raw_file_path)
        input_path = os.path.join(TMP_DIR, file_name)

        if not download_file(raw_file_path, input_path):
            send_transcoding_status(content_id, False)
            return

        transcoding_success = transcode_to_hls(input_path, TRANSCODED_DIR)

        if not transcoding_success:
            send_transcoding_status(content_id, False)
            return

        upload_success = upload_to_storage(TRANSCODED_DIR, destination_file_path)

        if not upload_success:
            send_transcoding_status(content_id, False)
            return

        send_transcoding_status(content_id, True, destination_file_path + "/output.m3u8")
        clear_tmp_directory()

    except Exception as e:
        logging.error(f"❌ Error processing file {raw_file_path}: {e}")
        send_transcoding_status(content_id, False)


def send_transcoding_status(content_id, status, transcoded_path = None):

    message = {
        "contentId" : content_id,
        "success" : status,
        "transcodedPath" : transcoded_path
    }

    producer.produce(
        topic="transcoding-update-topic",
        key=content_id,
        value=json.dumps(message)
    )

    logging.info(f"📥 Status Message sent: {message}")


# ===================== Main Loop =====================
if __name__ == "__main__":
    try:
        logging.info(f"🎧 Listening to Kafka topic: {topic}")
        while True:
            file_paths = get_message_from_kafka()
            if not file_paths:
                continue

            logging.info(f"📥 Message received: {file_paths}")
            executor.submit(
                process_message,
                file_paths["rawFilePath"],
                file_paths["destinationPath"],
                file_paths["contentId"])

    except Exception as e:
        logging.error(f"❌ Fatal error: {e}")
    finally:
        consumer.close()
        executor.shutdown(wait=True)
        logging.info("🚪 Gracefully shutting down.")
