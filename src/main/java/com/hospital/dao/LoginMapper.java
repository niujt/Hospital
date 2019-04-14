package com.hospital.dao;

import com.hospital.entity.Login;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LoginMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Login record);

    int insertSelective(Login record);

    Login selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Login record);

    int updateByPrimaryKey(Login record);
    List<Login> findAllAdmin(@Param("username")String username);
    int insertAdmin(Login login);
    Login findByUsername(@Param("username")String username);
}