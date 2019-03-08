package com.hospital.service.impl;

import com.hospital.common.CommonService;
import com.hospital.dao.AppointmentMapper;
import com.hospital.entity.Appointment;
import com.hospital.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    @Autowired
    AppointmentMapper appointmentMapper;

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentMapper.findAll();
    }

    @Override
    public String delAppointment(Integer id) {
        return appointmentMapper.deleteByPrimaryKey(id)>0? CommonService.del_message_success:CommonService.del_message_error;
    }

    @Override
    public Appointment getAppointment(Integer id) {
        return appointmentMapper.selectByPrimaryKey(id);
    }

    @Override
    public String UpdateAppointment(Appointment appointment) {
        return appointmentMapper.updateByPrimaryKey(appointment)>0?CommonService.upd_message_success:CommonService.upd_message_error;
    }

    @Override
    public String addAppointment(Appointment appointment) {
        return appointmentMapper.insert(appointment)>0?CommonService.add_message_success:CommonService.add_message_error;
    }
}
