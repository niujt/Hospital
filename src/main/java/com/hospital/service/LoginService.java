package com.hospital.service;

import com.hospital.entity.Login;

import java.util.List;

public interface LoginService {
    List<Login> findAllAdmin(String username);
    String addAmin(Login login);
    String updateAdmin(Login login);
    String delAdmin(Integer id);
    Login getAdmin(Integer id);
    String login(Login  login);
    String regist(Login login);
}
