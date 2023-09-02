package com.hospital.dao;

import com.hospital.entity.Medicalhistory;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface MedicalhistoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Medicalhistory record);

    int insertSelective(Medicalhistory record);

    Medicalhistory selectByPrimaryKey(Integer id);
    List<Medicalhistory> selectByPatientId(@Param("patientid")Integer patientid);

    int updateByPrimaryKeySelective(Medicalhistory record);

    int updateByPrimaryKey(Medicalhistory record);
    List<Medicalhistory> findAll(List<Integer> doctorids,List<Integer> patientids);
}