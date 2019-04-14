package com.hospital.dao;

import com.hospital.entity.Patient;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PatientMapper {
    List<Patient> findAll(@Param("name")String name,@Param("certId")String certId);
    int deleteByPrimaryKey(Integer id);

    int insert(Patient record);

    int insertSelective(Patient record);

    Patient selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Patient record);

    int updateByPrimaryKey(Patient record);
    Patient findPatientByCertId(@Param("certId")String certId);
    Patient findPatientByLoginId(@Param("loginid")Integer loginid);
    List<Patient> getPatientByName(@Param("name")String name);
}