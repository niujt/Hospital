package com.hospital.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class Medicalhistory {
    private Integer id;

    private Integer patientid;
    private String patientname;
    private String name;
    private Date time;
    private Integer hospitalizationid;
    private Integer doctorid;
    private String doctorname;
}