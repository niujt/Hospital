package com.hospital.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Hospitalization {
    private Integer id;

    private String name;

    private String floor;

    private String bed;

    private String door;

    private Integer patientid;

}