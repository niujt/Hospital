package com.hospital.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Patient {
    private Integer id;

    private String name;

    private Integer age;

    private Integer sex;

    private String address;

    private Integer hospitalizationid;

    private String drugsids;

    private Integer appointmentid;

    private Integer loginid;
    private Integer isout;
}