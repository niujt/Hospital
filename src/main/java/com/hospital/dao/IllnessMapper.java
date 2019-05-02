package com.hospital.dao;

import com.hospital.entity.Illness;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IllnessMapper {
    Integer insert(Illness illness);
    Integer deleteById(@Param("id")Integer id);
    Integer updateById(Illness illness);
    List<Illness> selectAll();
    Illness getIllness(@Param("id")Integer id);
    List<Illness> getIllnessByName(@Param("name")String name);
}
