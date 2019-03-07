package com.hospital.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Doctor {
    private Integer id;

    private String name;

    private Integer age;

    private Integer sex;

    private String patientids;

    private String address;

    private Integer loginid;
    private String department;
}