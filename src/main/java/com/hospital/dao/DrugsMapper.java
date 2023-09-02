package com.hospital.dao;

import com.hospital.entity.Drugs;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DrugsMapper {
    int deleteByPrimaryKey(Integer id);
    int updateNumber(Drugs drugs);
    int insert(Drugs record);

    int insertSelective(Drugs record);

    Drugs selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Drugs record);

    int updateByPrimaryKey(Drugs record);
    List<Drugs> findAll(Drugs drugs);
    Drugs findByName(@Param("name")String name);
    List<Drugs> getDrugsByName(@Param("name")String name);
}