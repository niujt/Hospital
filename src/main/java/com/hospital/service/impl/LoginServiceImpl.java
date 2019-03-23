package com.hospital.service.impl;

import com.hospital.common.CommonService;
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

    @Override
    public String addAmin(Login login) {
        String message;
        if(loginMapper.findByUsername(login.getUsername())!=null){
            message=CommonService.add_message_error2;
        }
        else{
            message=loginMapper.insertAdmin(login)>0? CommonService.add_message_success:CommonService.add_message_error;
        }
        return message;
    }

    @Override
    public String updateAdmin(Login login) {
        String message;
        if(loginMapper.findByUsername(login.getUsername())!=null){
            message=CommonService.add_message_error2;
        }
        else{
            message=loginMapper.updateByPrimaryKeySelective(login)>0? CommonService.upd_message_success:CommonService.upd_message_error;
        }
        return message;
    }

    @Override
    public String delAdmin(Integer id) {
        return loginMapper.deleteByPrimaryKey(id)>0?CommonService.del_message_success:CommonService.del_message_error;
    }

    @Override
    public Login getAdmin(Integer id) {
        return loginMapper.selectByPrimaryKey(id);
    }

    @Override
    public String login(Login  login) {
        String message="";
        Login login2=loginMapper.findByUsername(login.getUsername());
        if(login2!=null){
            if(!login2.getPassword().equals(login.getPassword())){
                message="，密码错误";
            }
            message="登录成功";
        }
        else{
            message="用户名不存在";
        }
        return message;
    }
}
