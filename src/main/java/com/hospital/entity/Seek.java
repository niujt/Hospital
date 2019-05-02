package com.hospital.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class Seek {

  private long id;
  private String describes;
  private String illname;
  private String drugs;
  private String options;
  private long days;
  private BigDecimal price;
  private Integer patientid;


}
