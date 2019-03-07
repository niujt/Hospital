package com.hospital.service;

import com.hospital.entity.Login;

import java.util.List;

public interface LoginService {
    List<Login> findAllAdmin();
}
