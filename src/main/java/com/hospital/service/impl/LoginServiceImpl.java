package com.hospital.service.impl;

import com.hospital.common.CommonService;
import com.hospital.dao.DoctorMapper;
import com.hospital.dao.LoginMapper;
import com.hospital.dao.PatientMapper;
import com.hospital.entity.Doctor;
import com.hospital.entity.Login;
import com.hospital.entity.Patient;
import com.hospital.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    LoginMapper loginMapper;
    @Autowired
    PatientMapper patientMapper;
    @Autowired
    DoctorMapper doctorMapper;
    @Override
    public List<Login> findAllAdmin(String username) {
        java.util.List<Login> safe = new java.util.ArrayList<>();
        for (Login account : loginMapper.findAllAdmin(username)) safe.add(adminView(account));
        return safe;
    }

    private Login adminView(Login account) {
        if (account == null || !Integer.valueOf(1).equals(account.getRole())) return null;
        Login safe = new Login(); safe.setId(account.getId()); safe.setUsername(account.getUsername()); safe.setRole(account.getRole());
        return safe;
    }
    private boolean missingAdminName(Login login) {
        return login == null || login.getUsername() == null || login.getUsername().trim().isEmpty();
    }
    private boolean blankPassword(String password) { return password == null || password.trim().isEmpty(); }
    private Login adminWrite(Login login) {
        Login write = new Login(); write.setId(login.getId()); write.setUsername(login.getUsername());
        return write;
    }

    @Override
    public String addAmin(Login login) {
        if (missingAdminName(login) || blankPassword(login.getPassword())) return "用户名和密码不能为空";
        if (loginMapper.findByUsername(login.getUsername()) != null) return CommonService.add_message_error2;
        Login write = adminWrite(login);
        try { write.setPassword(com.hospital.finance.auth.LoginPasswordCodec.encode(login.getPassword())); }
        catch (IllegalArgumentException invalid) { return "新密码须为8至72个字符"; }
        return loginMapper.insertAdmin(write) > 0 ? CommonService.add_message_success : CommonService.add_message_error;
    }

    @Override
    public String updateAdmin(Login login) {
        if (missingAdminName(login)) return "用户名不能为空";
        if (login.getId() == null) return CommonService.upd_message_error;
        Login existing = loginMapper.selectByPrimaryKey(login.getId());
        if (existing == null || !Integer.valueOf(1).equals(existing.getRole())) return CommonService.upd_message_error;
        Login sameName = loginMapper.findByUsername(login.getUsername());
        if (sameName != null && !login.getId().equals(sameName.getId())) return CommonService.add_message_error2;
        Login write = adminWrite(login);
        if (!blankPassword(login.getPassword())) {
            try { write.setPassword(com.hospital.finance.auth.LoginPasswordCodec.encode(login.getPassword())); }
            catch (IllegalArgumentException invalid) { return "新密码须为8至72个字符"; }
        }
        // Null password deliberately omits the column, preserving the existing credential byte-for-byte.
        return loginMapper.updateByPrimaryKeySelective(write) > 0 ? CommonService.upd_message_success : CommonService.upd_message_error;
    }

    @Override
    public String delAdmin(Integer id) {
        return loginMapper.deleteByPrimaryKey(id)>0?CommonService.del_message_success:CommonService.del_message_error;
    }

    @Override
    public Login getAdmin(Integer id) {
        return adminView(loginMapper.selectByPrimaryKey(id));
    }

    @Override
    public String login(Login  login) {
        String message="";
        login.setId(null);
        login.setRole(null);
        Login login2=loginMapper.findByUsername(login.getUsername());
        if(login2!=null){
            if(!com.hospital.finance.auth.LoginPasswordCodec.matches(login2.getPassword(),login.getPassword())){
                message="密码错误";
            }
            else{
                String stored = login2.getPassword();
                if (!stored.startsWith("pbkdf2$")) {
                    if (blankPassword(login.getPassword()) || login.getPassword().length() > 256) return "密码错误";
                    String encoded = com.hospital.finance.auth.LoginPasswordCodec.encodeLegacyVerified(login.getPassword());
                    if (loginMapper.migratePassword(login2.getId(), stored, encoded) != 1) {
                        // A concurrent reset/migration wins. Never overwrite it or authenticate stale credentials.
                        Login current = loginMapper.selectByPrimaryKey(login2.getId());
                        if (current == null || !com.hospital.finance.auth.LoginPasswordCodec.matches(current.getPassword(), login.getPassword())) return "密码错误";
                        login2 = current;
                    }
                }
                login.setId(login2.getId());
                login.setRole(login2.getRole());
                message="登录成功"+login2.getRole();
            }
        }
        else{
            message="用户名不存在";
        }
        return message;
    }

    @Override
    @Transactional
    public String regist(Login login) {
        // Public registration may bind a pre-recorded patient only. Staff are assigned by administrators.
        login.setId(null);
        login.setRole(null);
        if (login.getUsername() == null || login.getUsername().trim().isEmpty()
                || login.getPassword() == null || login.getPassword().isEmpty()) {
            return "用户名和密码不能为空";
        }
        if (loginMapper.findByUsername(login.getUsername()) != null) return "该用户名已被注册";
        if (login.getCertId() == null || login.getCertId().trim().isEmpty()) {
            return "管理员账号须由已登录管理员创建";
        }
        Doctor doctor = doctorMapper.getDoctorByCertId(login.getCertId());
        if (doctor != null) return "医生账号只能由管理员创建或分配，请联系管理员";
        Patient patient = patientMapper.findPatientByCertId(login.getCertId());
        if (patient == null) return "该证件信息未入库，不能注册该医生或者患者";
        if (patient.getLoginid() != null) return "该证件号已被注册";
        login.setRole(3);
        if (loginMapper.insert(login) != 1) throw new IllegalStateException("账号创建失败");
        Login saved = loginMapper.findByUsername(login.getUsername());
        if (saved == null || saved.getId() == null) throw new IllegalStateException("账号创建失败");
        patient.setLoginid(saved.getId());
        if (patientMapper.updateByPrimaryKeySelective(patient) != 1) throw new IllegalStateException("账号绑定失败");
        return "注册成功";
    }
}
