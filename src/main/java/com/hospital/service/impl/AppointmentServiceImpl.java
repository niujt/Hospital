package com.hospital.service.impl;

import com.hospital.common.CommonService;
import com.hospital.dao.AppointmentMapper;
import com.hospital.dao.DoctorMapper;
import com.hospital.dao.PatientMapper;
import com.hospital.entity.Appointment;
import com.hospital.entity.Doctor;
import com.hospital.entity.Patient;
import com.hospital.service.AppointmentService;
import com.hospital.uitls.PatientDoctorutils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    @Autowired
    AppointmentMapper appointmentMapper;
    @Autowired
    PatientMapper patientMapper;
    @Autowired
    DoctorMapper doctorMapper;
    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentMapper.findAll(null,null);
    }

    @Override
    public List<Appointment> getAllAppointments(String doctorname, String patientname) {
        Map map= PatientDoctorutils.getDoctorIdsAndPatientIds(doctorname,doctorMapper,patientname,patientMapper);
        return appointmentMapper.findAll((List)map.get("doctorids"),(List)map.get("patientids"));
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

    @Override
    public List<Appointment> getPatientMessage(Integer patientId) {
        return appointmentMapper.selectByPatientId(patientId);
    }

    @Override
    public List<Appointment> selectByDoctorId(Integer doctorId,String patientname,String time) {
        List<Integer> patientids=PatientDoctorutils.getPatientIds(patientname,patientMapper);
        return appointmentMapper.selectByDoctorId(doctorId,patientids,time);
    }

    @Override
    public Integer selectTheLastAppointment(Integer patientId) {
        return appointmentMapper.selectTheLast(patientId);
    }
}
