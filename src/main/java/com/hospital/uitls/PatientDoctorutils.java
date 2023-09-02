package com.hospital.uitls;

import com.hospital.dao.DoctorMapper;
import com.hospital.dao.PatientMapper;
import com.hospital.entity.Doctor;
import com.hospital.entity.Patient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PatientDoctorutils {
    public  static Map<String,List<Integer>> getDoctorIdsAndPatientIds(String doctorname, DoctorMapper doctorMapper, String patientname, PatientMapper patientMapper){
        Map<String,List<Integer>> map=new HashMap<>();
        Integer doctorid=99999999;
        List<Integer> doctorids=new ArrayList<>();
        if(doctorname!="" &&doctorname!=null){
            doctorname=doctorname.trim();
            List<Doctor> doctorList=doctorMapper.getDoctorByName(doctorname);
            if(doctorList!=null){
                doctorids.add(doctorid);
                for(Doctor doctor:doctorList){
                    doctorid=doctor.getId();
                    doctorids.add(doctorid);
                }
            }
        }
        else{
            doctorids=null;
        }
        Integer patientid=99999999;
        List<Integer> patientids=new ArrayList<>();
        if(patientname!="" &&patientname!=null){
            patientname=patientname.trim();
            List<Patient> patientList=patientMapper.getPatientByName(patientname);
            if(patientList!=null){
                patientids.add(patientid);
                for(Patient patient:patientList){
                    patientid=patient.getId();
                    patientids.add(patientid);
                }
            }
        }
        else{
            patientids=null;
        }
        map.put("patientids",patientids);
        map.put("doctorids",doctorids);
        return map;
    }
    public  static List<Integer> getPatientIds(String patientname, PatientMapper patientMapper){
        Integer patientid=99999999;
        List<Integer> patientids=new ArrayList<>();
        if(patientname!="" &&patientname!=null){
            patientname=patientname.trim();
            List<Patient> patientList=patientMapper.getPatientByName(patientname);
            if(patientList!=null){
                patientids.add(patientid);
                for(Patient patient:patientList){
                    patientid=patient.getId();
                    patientids.add(patientid);
                }
            }
        }
        else{
            patientids=null;
        }
        return patientids;
    }
    public static List<Integer> getOptionIds(String ids){
        List<Integer> integerList=new ArrayList<>();
        String[] idarray=ids.split(",");
        if (idarray.length==0){
            integerList.add(Integer.parseInt(ids));
            return integerList;
        }else{
        integerList.clear();
            for(String id:idarray){
                integerList.add(Integer.parseInt(id));
            }
            return integerList;
        }

    }
}
