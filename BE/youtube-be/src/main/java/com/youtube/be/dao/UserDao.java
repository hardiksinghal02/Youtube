package com.youtube.be.dao;

import com.youtube.be.converter.UserConverter;
import com.youtube.be.entity.UserEntity;
import com.youtube.be.models.User;
import com.youtube.be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDao {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> findUserByEmail(String emailId) {
        Optional<UserEntity> userEntityOptional = userRepository.findByEmail(emailId);

        return userEntityOptional.map(UserConverter::toUser);
    }

    public User saveUser(User user) {
        UserEntity userEntity = UserConverter.toUserEntity(user);
        return UserConverter.toUser(userRepository.save(userEntity));
    }

    public Optional<User> findUserByUserId(String userId) {
        Optional<UserEntity> userEntityOptional = userRepository.findByEmail(userId);
        return userEntityOptional.map(UserConverter::toUser);
    }
}
