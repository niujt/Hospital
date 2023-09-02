package com.hospital.service;

import com.hospital.entity.Illness;

import java.util.List;

public interface IllnessService {
    List<Illness> getAll();
    Illness getIllness(Integer id);
    String insert(Illness option);
    String deleteIllness(Integer id);
    String updateIllness(Illness option);
}
