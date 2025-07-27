package com.youtube.be.converter;

import com.youtube.be.entity.UserEntity;
import com.youtube.be.models.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserConverter {

    public static User toUser(UserEntity userEntity) {
        return User.builder()
                .id(userEntity.getId())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .profilePicture(userEntity.getProfilePicture())
                .email(userEntity.getEmail())
                .build();
    }

    public static UserEntity toUserEntity(User user) {
        return UserEntity.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .profilePicture(user.getProfilePicture())
                .email(user.getEmail())
                .build();
    }
}
