package com.hospital.dao;

import com.hospital.entity.Doctor;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface DoctorMapper {
    List<Doctor> getAll(@Param("name")String name,@Param("certId")String certId);
    int deleteByPrimaryKey(Integer id);

    int insert(Doctor record);

    int insertSelective(Doctor record);

    Doctor selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Doctor record);

    int updateByPrimaryKey(Doctor record);
    Doctor getDoctorByCertId(@Param("certId")String certId);
    List<Doctor> getDoctorByDepartment(@Param("department")String department);
    Doctor getDoctorByLoginId(@Param("loginid")Integer loginid);
    List<Doctor> getDoctorByName(@Param("name")String name);
}