package com.scrumandcoke.movietheaterclub.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scrumandcoke.movietheaterclub.enums.MemberType;
import com.scrumandcoke.movietheaterclub.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionDetail {

    String userId;

    private String firstName;

    private String lastName;

    private String email;

    private MemberType memberType;

    private Date createdAt;

    private Date lastUpdatedAt;

    private String sessionId;

    private Date sessionExpireAt;

    private UserType userType;
}
