package com.hospital.service;

import com.hospital.entity.Appointment;

import java.util.List;

public interface AppointmentService {
    List<Appointment> getAllAppointments();
    List<Appointment> getAllAppointments(String doctorname,String patientname);
    String delAppointment(Integer id);
    Appointment getAppointment(Integer id);
    String UpdateAppointment(Appointment appointment);
    String addAppointment(Appointment appointment);
    List<Appointment> getPatientMessage(Integer patientId);
    List<Appointment> selectByDoctorId(Integer doctorId,String patientname,String time);
    Integer selectTheLastAppointment(Integer patientId);
}
