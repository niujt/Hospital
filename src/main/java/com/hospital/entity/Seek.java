package com.hospital.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class Seek {
  public Seek() {
  }

  public Seek(String describes, String illname,String options, long days, BigDecimal price, String patientname) {
    this.describes = describes;
    this.illname = illname;
    this.options = options;
    this.days = days;
    this.price = price;
    this.patientname = patientname;
  }

  private long id;
  private String describes;
  private String illname;
  private String drugs;
  private String options;
  private long days;
  private BigDecimal price;
  private Integer patientid;
  private String patientname;
  private String doctorname;


}
