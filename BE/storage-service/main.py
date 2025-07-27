from flask import Flask, request, send_from_directory, abort
import os

UPLOAD_FOLDER = "/data"
os.makedirs(UPLOAD_FOLDER, exist_ok=True)

app = Flask(__name__)

@app.route('/upload', methods=['POST'])
def upload_file():
    file = request.files.get('file')
    relative_path = request.form.get('path')  # e.g., 'input/input2.mkv'

    if not file or not relative_path:
        return {"status": "fail", "reason": "Missing file or path"}, 400

    # Ensure target directory exists
    full_path = os.path.join(UPLOAD_FOLDER, relative_path)
    os.makedirs(os.path.dirname(full_path), exist_ok=True)

    # Save the file
    file.save(full_path)

    return {"status": "success", "path": full_path}, 200


@app.route('/download/<path:filename>', methods=['GET'])  # Accept slashes in path
def download_file(filename):
    filepath = os.path.join(UPLOAD_FOLDER, filename)
    if not os.path.isfile(filepath):
        abort(404, description="File not found")
    return send_from_directory(UPLOAD_FOLDER, filename, as_attachment=True)

@app.route('/health', methods=['GET'])
def health():
    return {"status": "success"}, 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
