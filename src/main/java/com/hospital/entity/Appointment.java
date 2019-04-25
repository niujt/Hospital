package com.hospital.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;
@Getter
@Setter
@ToString
public class Appointment {
    private Integer id;

    private Integer patientid;
    private String patientname;

    private Integer doctorid;
    private String doctorname;
    private String department;
    private Date time;
    private BigDecimal expenses;

}