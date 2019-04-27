package com.hospital.dao;

import com.hospital.entity.Option;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OptionMapper {
    Integer insert(Option option);
    Integer deleteById(@Param("id")Integer id);
    Integer updateById(Option option);
    List<Option> selectAll();
    Option getOption(@Param("id")Integer id);
}
