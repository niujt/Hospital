package com.hospital.finance;

import com.hospital.dao.DrugsMapper;
import com.hospital.entity.Drugs;
import com.hospital.service.impl.DrugsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class LegacyInventoryBoundaryTest {
    @Test void cannotCreateInitialStockWithoutProcurement(){DrugsMapper mapper=mock(DrugsMapper.class);DrugsServiceImpl service=new DrugsServiceImpl();ReflectionTestUtils.setField(service,"drugsMapper",mapper);Drugs drug=new Drugs();drug.setNumber(5);assertTrue(service.addDrug(drug).contains("初始库存"));verifyNoInteractions(mapper);}
    @Test void cannotOverwriteStockFromLegacyEdit(){DrugsMapper mapper=mock(DrugsMapper.class);DrugsServiceImpl service=new DrugsServiceImpl();ReflectionTestUtils.setField(service,"drugsMapper",mapper);Drugs existing=new Drugs();existing.setNumber(10);when(mapper.selectByPrimaryKey(1)).thenReturn(existing);Drugs edited=new Drugs();edited.setId(1);edited.setNumber(20);assertTrue(service.updateDrug(edited).contains("库存只能"));verify(mapper,never()).updateByPrimaryKey(any());}
    @Test void cannotDeletePositiveStock(){DrugsMapper mapper=mock(DrugsMapper.class);DrugsServiceImpl service=new DrugsServiceImpl();ReflectionTestUtils.setField(service,"drugsMapper",mapper);Drugs existing=new Drugs();existing.setNumber(10);when(mapper.selectByPrimaryKey(1)).thenReturn(existing);assertTrue(service.delDrug(1).contains("仍有库存"));verify(mapper,never()).deleteByPrimaryKey(anyInt());}
    @Test void generatedMetadataUpdateNeverWritesStock() throws Exception {
        org.apache.ibatis.session.Configuration configuration=new org.apache.ibatis.session.Configuration();
        try(java.io.InputStream stream=getClass().getClassLoader().getResourceAsStream("mapper/DrugsMapper.xml")) {
            new org.apache.ibatis.builder.xml.XMLMapperBuilder(stream,configuration,"mapper/DrugsMapper.xml",configuration.getSqlFragments()).parse();
        }
        Drugs drug=new Drugs();drug.setId(1);drug.setNumber(999);
        String sql=configuration.getMappedStatement("com.hospital.dao.DrugsMapper.updateByPrimaryKey").getBoundSql(drug).getSql();
        assertFalse(sql.matches("(?is).*\\bnumber\\s*=.*"));
    }

}
