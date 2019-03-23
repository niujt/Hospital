package com.hospital.service;

import com.hospital.entity.Drugs;

import java.util.List;

public interface DrugsService {
    List<Drugs> getAllDrugs();
    String delDrug(Integer id);
    String addDrug(Drugs drugs);
    Drugs getDrug(Integer id);
    String updateDrug(Drugs drugs);
}
