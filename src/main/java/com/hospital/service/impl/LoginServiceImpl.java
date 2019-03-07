package com.hospital.service.impl;

import com.hospital.dao.LoginMapper;
import com.hospital.entity.Login;
import com.hospital.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    LoginMapper loginMapper;
    @Override
    public List<Login> findAllAdmin() {
        return loginMapper.findAllAdmin();
    }
}
