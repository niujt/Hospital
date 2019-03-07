package com.hospital.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Medicalhistory {
    private Integer id;

    private Integer patientid;

    private String name;
}