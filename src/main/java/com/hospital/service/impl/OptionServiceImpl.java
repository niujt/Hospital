package com.hospital.service.impl;

import com.hospital.common.CommonService;
import com.hospital.dao.OptionMapper;
import com.hospital.entity.Option;
import com.hospital.service.OptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class OptionServiceImpl implements OptionService {
    @Autowired
    OptionMapper optionMapper;
    @Override
    public List<Option> getAll() {
        return optionMapper.selectAll();
    }

    @Override
    public String insert(Option option) {
        return optionMapper.insert(option)>0? CommonService.add_message_success:CommonService.add_message_error;
    }

    @Override
    public String deleteOption(Integer id) {
        return optionMapper.deleteById(id)>0?CommonService.del_message_success:CommonService.del_message_error;
    }

    @Override
    public String updateBOption(Option option) {
        return optionMapper.updateById(option)>0?CommonService.upd_message_success:CommonService.upd_message_error;
    }

    @Override
    public Option getOption(Integer id) {
        return optionMapper.getOption(id);
    }
}
