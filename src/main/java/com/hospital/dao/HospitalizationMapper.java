package com.hospital.dao;

import com.hospital.entity.Hospitalization;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HospitalizationMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Hospitalization record);

    int insertSelective(Hospitalization record);

    Hospitalization selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Hospitalization record);

    int updateByPrimaryKey(Hospitalization record);
    List<Hospitalization> findAll();
    List<Hospitalization> selectByPatientId(@Param("patientid")Integer patientId);
}