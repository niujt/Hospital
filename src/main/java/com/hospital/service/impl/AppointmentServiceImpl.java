package com.hospital.service.impl;

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
}
