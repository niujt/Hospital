package com.hospital.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class Option {

  private long id;
  private String name;
  private String type;
  private BigDecimal price;
}
