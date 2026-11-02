package com.hospital.controller;

import com.hospital.dao.HospitalizationMapper;
import com.hospital.dao.LoginMapper;
import com.hospital.service.impl.HospitalizationServiceImpl;
import com.hospital.service.impl.LoginServiceImpl;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.io.*;
import java.sql.*;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Real legacy controllers, services, MyBatis XML and isolated H2. No production/runtime DB.
 * This exercises persistence/HTTP contracts, not browser interaction or authorization interceptors. */
class LegacyAdminPersistenceRegressionTest {
    SqlSession sql; MockMvc mvc; HospitalizationController exportController;
    @BeforeEach void setup() throws Exception {
        UnpooledDataSource ds=new UnpooledDataSource("org.h2.Driver","jdbc:h2:mem:legacy_"+UUID.randomUUID()+";MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1","sa","");
        try(Connection c=ds.getConnection();Statement s=c.createStatement()) {
            s.execute("create table login(id int auto_increment primary key,username varchar(100),password varchar(200),role int)");
            s.execute("create table patient(id int primary key,name varchar(100))");
            s.execute("insert into patient values(1,'Synthetic patient')");
            s.execute("create table hospitalization(id int auto_increment primary key,floor varchar(30),bed varchar(30),door varchar(30),patientid int,intime date,outtime date,medicalname varchar(100))");
        }
        Configuration cfg=new Configuration(new Environment("isolated",new JdbcTransactionFactory(),ds));
        for(String name:new String[]{"HospitalizationMapper","LoginMapper"}) {
            String path="mapper/"+name+".xml";
            try(InputStream in=getClass().getClassLoader().getResourceAsStream(path)) {new XMLMapperBuilder(in,cfg,path,cfg.getSqlFragments()).parse();}
        }
        sql=new SqlSessionFactoryBuilder().build(cfg).openSession(true);
        HospitalizationServiceImpl hs=new HospitalizationServiceImpl();ReflectionTestUtils.setField(hs,"hospitalizationMapper",sql.getMapper(HospitalizationMapper.class));
        HospitalizationController hc=new HospitalizationController();hc.hospitalizationService=hs; exportController=hc;
        LoginServiceImpl ls=new LoginServiceImpl();ReflectionTestUtils.setField(ls,"loginMapper",sql.getMapper(LoginMapper.class));
        LoginController lc=new LoginController();lc.loginService=ls;
        mvc=MockMvcBuilders.standaloneSetup(hc,lc).build();
    }
    @AfterEach void close(){if(sql!=null)sql.close();}
    String stay(String bed){return "{\"id\":901,\"floor\":\"F2\",\"door\":\"ROOM201\",\"bed\":\""+bed+"\",\"patientid\":1,\"intime\":\"2026-10-30\",\"outtime\":\"2026-10-31\",\"medicalname\":\"Synthetic only\"}";}
    @Test void hospitalizationCreatesUpdatesAndDeletesRealRows()throws Exception{
        mvc.perform(post("/admin/hospitalization").contentType("application/json").content(stay("BED3"))).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("添加成功"));
        assertEquals("BED3",sql.getMapper(HospitalizationMapper.class).selectByPrimaryKey(901).getBed());
        mvc.perform(put("/admin/hospitalization").contentType("application/json").content(stay("BED4"))).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("更新成功"));
        assertEquals("BED4",sql.getMapper(HospitalizationMapper.class).selectByPrimaryKey(901).getBed());
        mvc.perform(delete("/admin/hospitalization/901")).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("删除成功"));
        assertNull(sql.getMapper(HospitalizationMapper.class).selectByPrimaryKey(901));
    }
    @Test void administratorCreateDuplicateRenameAndDeleteArePersisted()throws Exception{
        String body="{\"id\":902,\"username\":\"synthetic-admin\",\"password\":\"test-only\",\"role\":3}";
        mvc.perform(post("/admin/admin").contentType("application/json").content(body)).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("添加成功"));
        assertEquals(1,sql.getMapper(LoginMapper.class).selectByPrimaryKey(902).getRole());
        mvc.perform(post("/admin/admin").contentType("application/json").content(body)).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("已存在"));
        mvc.perform(put("/admin/admin").contentType("application/json").content(body.replace("synthetic-admin","synthetic-renamed"))).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("更新成功"));
        assertEquals("synthetic-renamed",sql.getMapper(LoginMapper.class).selectByPrimaryKey(902).getUsername());
        mvc.perform(delete("/admin/admin/902")).andExpect(status().isOk()).andExpect(jsonPath("$.message").value("删除成功"));
        assertNull(sql.getMapper(LoginMapper.class).selectByPrimaryKey(902));
    }
    @Test void sameUsernameEditUpdatesOwnAccountWithoutChangingRole()throws Exception{
        String body="{\"id\":903,\"username\":\"same-name\",\"password\":\"test-only\"}";
        mvc.perform(post("/admin/admin").contentType("application/json").content(body)).andExpect(status().isOk());
        mvc.perform(put("/admin/admin").contentType("application/json").content(body.replace("test-only", "updated-only"))).andExpect(jsonPath("$.message").value("更新成功"));
        assertTrue(com.hospital.finance.auth.LoginPasswordCodec.matches(sql.getMapper(LoginMapper.class).selectByPrimaryKey(903).getPassword(),"updated-only"));
        assertEquals(1,sql.getMapper(LoginMapper.class).selectByPrimaryKey(903).getRole());
    }
    @Test void adminRejectsMissingCredentialsWhileLegacyHospitalizationValidationIsUnchanged()throws Exception{
        // The account boundary is now enforced server-side; unrelated hospitalization business validation is unchanged.
        mvc.perform(post("/admin/admin").contentType("application/json").content("{}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.message").value("用户名和密码不能为空"));
        mvc.perform(post("/admin/hospitalization").contentType("application/json").content("{}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.message").value("添加成功"));
    }
    @Test void whitespaceCredentialsAndEditingOtherRolesAreRejectedWithoutMutation()throws Exception{
        for(String body:new String[]{"{}","{\"username\": \"   \", \"password\": \"valid\"}","{\"username\": \"valid\", \"password\": \"   \"}"}) {
            mvc.perform(post("/admin/admin").contentType("application/json").content(body)).andExpect(jsonPath("$.message").value("用户名和密码不能为空"));

        }
        try(Statement statement=sql.getConnection().createStatement()) { statement.execute("insert into login values(904,'doctor-only','unchanged',2)"); }
        mvc.perform(put("/admin/admin").contentType("application/json").content("{\"id\":904,\"username\":\"doctor-only\",\"password\":\"changed\",\"role\":1}"))
                .andExpect(jsonPath("$.message").value("更新失败"));
        assertEquals(2,sql.getMapper(LoginMapper.class).selectByPrimaryKey(904).getRole());
        assertEquals("unchanged",sql.getMapper(LoginMapper.class).selectByPrimaryKey(904).getPassword());
    }
    @Test void duplicateOtherUsernameRejectsButOwnUsernameIsAllowed()throws Exception{
        try(Statement statement=sql.getConnection().createStatement()) { statement.execute("insert into login values(905,'first-admin','unchanged',1),(906,'other-admin','other',1)"); }
        mvc.perform(put("/admin/admin").contentType("application/json").content("{\"id\":905,\"username\":\"other-admin\",\"password\":\"changed\"}"))
                .andExpect(jsonPath("$.message").value("已存在"));
        assertEquals("first-admin",sql.getMapper(LoginMapper.class).selectByPrimaryKey(905).getUsername());
    }
    @Test void passwordMigrationUsesRealCompareAndSwapAndNeverUpdatesRole()throws Exception{
        LoginMapper mapper=sql.getMapper(LoginMapper.class);
        try(Statement statement=sql.getConnection().createStatement()) { statement.execute("insert into login values(907,'legacy','123456',3)"); }
        String encoded=com.hospital.finance.auth.LoginPasswordCodec.encodeLegacyVerified("123456");
        assertEquals(0,mapper.migratePassword(907,"wrong",encoded));
        assertEquals("123456",mapper.selectByPrimaryKey(907).getPassword());
        assertEquals(1,mapper.migratePassword(907,"123456",encoded));
        assertEquals(0,mapper.migratePassword(907,"123456","must-not-overwrite"));
        assertEquals(encoded,mapper.selectByPrimaryKey(907).getPassword());
        assertEquals(3,mapper.selectByPrimaryKey(907).getRole());
        try(Statement statement=sql.getConnection().createStatement()) { statement.execute("update login set password='CaseSecret' where id=907"); }
        assertEquals(0,mapper.migratePassword(907,"casesecret",encoded));
        assertEquals("CaseSecret",mapper.selectByPrimaryKey(907).getPassword());
    }
    @Test void blankAdminEditPreservesStoredHashExactly()throws Exception{
        String body="{\"id\":908,\"username\":\"keep-secret\",\"password\":\"new-only-123\"}";
        mvc.perform(post("/admin/admin").contentType("application/json").content(body)).andExpect(jsonPath("$.message").value("添加成功"));
        String stored=sql.getMapper(LoginMapper.class).selectByPrimaryKey(908).getPassword();
        assertTrue(stored.startsWith("pbkdf2$"));
        mvc.perform(put("/admin/admin").contentType("application/json").content(body.replace("new-only-123","")))
                .andExpect(jsonPath("$.message").value("更新成功"));
        assertEquals(stored,sql.getMapper(LoginMapper.class).selectByPrimaryKey(908).getPassword());
    }
    @Test void exportedWorkbookHasCorrectHeadersDataAndHttpAttachment()throws Exception{
        mvc.perform(post("/admin/hospitalization").contentType("application/json").content(stay("BED3"))).andExpect(status().isOk());
        byte[] bytes=mvc.perform(get("/admin/getHospitalization")).andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"hospitalization.xlsx\"; filename*=UTF-8''" + java.net.URLEncoder.encode("住院信息.xlsx", "UTF-8")))
                .andReturn().getResponse().getContentAsByteArray();
        try(XSSFWorkbook book=new XSSFWorkbook(new ByteArrayInputStream(bytes))){
            assertEquals(1,book.getNumberOfSheets());assertEquals(1,book.getSheetAt(0).getLastRowNum());
            assertEquals("患者",book.getSheetAt(0).getRow(0).getCell(4).getStringCellValue());
            assertEquals("Synthetic patient",book.getSheetAt(0).getRow(1).getCell(4).getStringCellValue());
            // Header and value remain aligned, including distinct door/bed fixture values.
            assertEquals("门牌号",book.getSheetAt(0).getRow(0).getCell(2).getStringCellValue());
            assertEquals("ROOM201",book.getSheetAt(0).getRow(1).getCell(2).getStringCellValue());
            assertEquals("床号",book.getSheetAt(0).getRow(0).getCell(3).getStringCellValue());
            assertEquals("BED3",book.getSheetAt(0).getRow(1).getCell(3).getStringCellValue());
            assertEquals("统计表",book.getSheetName(0));
            assertEquals("m/d/yy",book.getSheetAt(0).getRow(1).getCell(6).getCellStyle().getDataFormatString());
            assertTrue(org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(book.getSheetAt(0).getRow(1).getCell(6)));
            assertEquals(sql.getMapper(HospitalizationMapper.class).selectByPrimaryKey(901).getIntime().getTime(),book.getSheetAt(0).getRow(1).getCell(6).getDateCellValue().getTime());
            assertEquals(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER,book.getSheetAt(0).getRow(0).getCell(0).getCellStyle().getAlignment());
            assertTrue(book.getFontAt(book.getSheetAt(0).getRow(0).getCell(0).getCellStyle().getFontIndex()).getBold());
            assertEquals("Arial",book.getFontAt(book.getSheetAt(0).getRow(0).getCell(0).getCellStyle().getFontIndex()).getFontName());
            assertEquals(10,book.getFontAt(book.getSheetAt(0).getRow(0).getCell(0).getCellStyle().getFontIndex()).getFontHeightInPoints());
            assertEquals("Arial",book.getFontAt(0).getFontName());
            assertEquals(10,book.getFontAt(0).getFontHeightInPoints());
        }
    }
    @Test void emptyExportStillContainsXlsxHeader() throws Exception {
        byte[] bytes = mvc.perform(get("/admin/getHospitalization")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();
        try (XSSFWorkbook book = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            assertEquals(0, book.getSheetAt(0).getLastRowNum());
            assertEquals(8, book.getSheetAt(0).getRow(0).getLastCellNum());
        }
    }

    @Test void exportPreservesNullDatesAndFormulaLikeTextAsLiteral() throws Exception {
        try (Statement statement = sql.getConnection().createStatement()) {
            statement.execute("update patient set name='演示患者'");
            statement.execute("insert into hospitalization values(902,'F1','BED1','ROOM1',1,null,null,'=1+1')");
        }
        byte[] bytes = mvc.perform(get("/admin/getHospitalization")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();
        try (XSSFWorkbook book = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            org.apache.poi.ss.usermodel.Row row = book.getSheetAt(0).getRow(1);
            assertEquals("演示患者", row.getCell(4).getStringCellValue());
            assertEquals(org.apache.poi.ss.usermodel.CellType.STRING, row.getCell(5).getCellType());
            assertEquals("=1+1", row.getCell(5).getStringCellValue());
            assertEquals(org.apache.poi.ss.usermodel.CellType.BLANK, row.getCell(6).getCellType());
            assertEquals(org.apache.poi.ss.usermodel.CellType.BLANK, row.getCell(7).getCellType());
            assertEquals(12 * 256, book.getSheetAt(0).getColumnWidth(1));
            assertEquals(17 * 256, book.getSheetAt(0).getColumnWidth(3));
        }
    }

}
