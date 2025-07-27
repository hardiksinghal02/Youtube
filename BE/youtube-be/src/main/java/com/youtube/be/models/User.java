package com.youtube.be.models;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class User {

    private String id;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private String email;

}
