package com.hospital.dao;

import com.hospital.uitls.PatientDoctorutils;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class OptionTotalPriceTest {
    SqlSession session;
    OptionMapper mapper;
    @BeforeEach void setup()throws Exception {
        DriverManagerDataSource ds=new DriverManagerDataSource("jdbc:h2:mem:options"+UUID.randomUUID()+";MODE=MySQL;DB_CLOSE_DELAY=-1","sa","");
        JdbcTemplate jdbc=new JdbcTemplate(ds);
        jdbc.execute("create schema hospital");
        jdbc.execute("create table hospital.option(id int primary key,price decimal(12,2))");
        jdbc.update("insert into hospital.option values(1,10.10),(2,20.20),(3,999.99)");
        Configuration configuration=new Configuration(new Environment("test",new JdbcTransactionFactory(),ds));
        try(InputStream xml=getClass().getClassLoader().getResourceAsStream("mapper/OptionMapper.xml")) {
            new XMLMapperBuilder(xml,configuration,"mapper/OptionMapper.xml",configuration.getSqlFragments()).parse();
        }
        session=new SqlSessionFactoryBuilder().build(configuration).openSession();
        mapper=session.getMapper(OptionMapper.class);
    }
    @AfterEach void close()throws Exception{session.getConnection().createStatement().execute("DROP ALL OBJECTS");session.close();}
    void amount(String expected,List<Integer> ids){assertEquals(0,new BigDecimal(expected).compareTo(mapper.getTotalPrice(ids)));}
    @Test void selectedOptionsSumExactlyWithoutOtherRows(){amount("30.30",Arrays.asList(1,2));}
    @Test void duplicateSelectionsDoNotChargeTwice(){amount("10.10",Arrays.asList(1,1));}
    @Test void nullAndEmptyDoNotSumEntireCatalog(){amount("0",null);amount("0",Collections.emptyList());}
    @Test void unknownOptionsHaveZeroTotal(){amount("0",Collections.singletonList(999));}
    @Test void emptyInputParsesAsNoOptions(){for(String value:new String[]{null,"","   "}){List<Integer> ids=PatientDoctorutils.getOptionIds(value);assertTrue(ids.isEmpty());amount("0",ids);}}
    @Test void normalIdsParseAndExecute(){List<Integer> ids=PatientDoctorutils.getOptionIds("1,2");assertEquals(Arrays.asList(1,2),ids);amount("30.30",ids);}
    @Test void malformedInputStillFailsInsteadOfSilentlySelectingAll(){for(String value:new String[]{"abc","1,no","1,,2",",","1,","2147483648"})assertThrows(NumberFormatException.class,()->PatientDoctorutils.getOptionIds(value));}
    @Test void emptyFormSelectionSurvivesExtractionAndTotalsZero(){Map<String,String> form=new HashMap<>();form.put("days","0");form.put("patientid","1");String ids=com.hospital.uitls.DrugsUtils.vaild2(form);assertEquals("",ids);amount("0",PatientDoctorutils.getOptionIds(ids));}
}
