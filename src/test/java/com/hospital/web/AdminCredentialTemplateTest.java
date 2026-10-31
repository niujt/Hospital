package com.hospital.web;

import com.hospital.entity.Login;
import freemarker.template.Configuration;
import org.junit.jupiter.api.Test;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class AdminCredentialTemplateTest {
    @Test void editNeverRendersStoredCredentialAndBlankMeansKeep() throws Exception {
        Configuration config = new Configuration(Configuration.VERSION_2_3_30);
        config.setDirectoryForTemplateLoading(Paths.get("src/main/resources/templates").toFile());
        Map<String,Object> admin = new HashMap<>();
        admin.put("id", 1); admin.put("username", "<example>");
        admin.put("password", "stored-secret-must-not-render");
        StringWriter output = new StringWriter();
        config.getTemplate("admin/info/admininfo.ftl").process(java.util.Collections.singletonMap("admin", admin), output);
        String html=output.toString();
        assertFalse(html.contains("stored-secret-must-not-render"));
        assertTrue(html.contains("&lt;example&gt;"));
        assertTrue(html.contains("留空表示不修改"));
        assertFalse(html.matches("(?s).*name=\"password\"[^>]*required.*"));
        assertTrue(html.contains("autocomplete=\"new-password\""));
    }
    @Test void createKeepsNewCredentialPolicyAndEntityLoggingExcludesSecret() throws Exception {
        String html=Files.readString(Paths.get("src/main/resources/templates/admin/add/adminadd.ftl"));
        assertTrue(html.contains("minlength=\"8\" maxlength=\"72\""));
        Login login=new Login();login.setUsername("example");login.setPassword("never-log-this");
        assertFalse(login.toString().contains("never-log-this"));
    }
}
