package com.hospital.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class Hospitalization {
    private Integer id;


    private String floor;

    private String bed;

    private String door;

    private Integer patientid;
    private String patientname;
    private Date intime;
    private Date outtime;
    private String medicalname;
}