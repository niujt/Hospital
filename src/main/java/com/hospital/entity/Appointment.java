package com.hospital.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
@Getter
@Setter
@ToString
public class Appointment {
    private Integer id;

    private Integer patientid;

    private Integer doctorid;

    private Date time;

}