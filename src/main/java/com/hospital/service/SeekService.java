package com.hospital.service;

import com.hospital.entity.Seek;

public interface SeekService {
    String addSeek(Seek seek);
    Seek getSeekByPatientId(Integer patientid);
}
