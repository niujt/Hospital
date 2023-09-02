package com.hospital.service;

import com.hospital.entity.Option;

import java.util.List;

public interface OptionService {
    List<Option> getAll();
    Option getOption(Integer id);
    String insert(Option option);
    String deleteOption(Integer id);
    String updateBOption(Option option);
}
