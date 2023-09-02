package com.hospital.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class Drugs {
    private Integer id;

    private String name;

    private Integer type;
    private BigDecimal price;
    private Integer number;
    private String text;

}