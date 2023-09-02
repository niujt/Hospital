package com.hospital.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Login {
    private Integer id;

    private String username;

    private String password;
    private Integer role;
    private String certId;

}