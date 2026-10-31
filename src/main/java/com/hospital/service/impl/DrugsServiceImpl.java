package com.hospital.service.impl;

import com.hospital.common.CommonService;
import com.hospital.dao.DrugsMapper;
import com.hospital.entity.Drugs;
import com.hospital.service.DrugsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DrugsServiceImpl implements DrugsService {
    @Autowired
    DrugsMapper drugsMapper;

    @Override
    public List<Drugs> getAllDrugs() {
        return drugsMapper.findAll(null);
    }

    @Override
    public List<Drugs> getAllDrugs(Drugs drugs) {
        return drugsMapper.findAll(drugs);
    }

    @Override
    public String delDrug(Integer id) {
        Drugs existing = drugsMapper.selectByPrimaryKey(id);
        if (existing != null && existing.getNumber() != null && existing.getNumber() > 0)
            return "药品仍有库存，不能删除；财务历史关联记录也需保留";
        return drugsMapper.deleteByPrimaryKey(id)>0? CommonService.del_message_success:CommonService.del_message_error;
    }

    @Override
    public String addDrug(Drugs drugs) {
        String message;
        if (drugs.getNumber() != null && drugs.getNumber() != 0)
            return "新药品初始库存须为0，请通过财务进货登记增加库存";
        drugs.setNumber(0);
        if(drugsMapper.findByName(drugs.getName())!=null){
            message=CommonService.add_message_error2;
        }
        else{
            message=drugsMapper.insert(drugs)>0?CommonService.add_message_success:CommonService.add_message_error;
        }
        return message;
    }

    @Override
    public Drugs getDrug(Integer id) {
        return drugsMapper.selectByPrimaryKey(id);
    }

    @Override
    public String updateDrug(Drugs drugs) {
        Drugs existing = drugsMapper.selectByPrimaryKey(drugs.getId());
        if (existing == null) return CommonService.upd_message_error;
        if (drugs.getNumber() != null && !drugs.getNumber().equals(existing.getNumber()))
            return "库存只能通过进货登记或已支付订单变更";
        return drugsMapper.updateByPrimaryKey(drugs)>0?CommonService.upd_message_success:CommonService.upd_message_error;
    }
}
