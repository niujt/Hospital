package com.hospital.web;

import freemarker.template.Configuration;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Parses every template; deliberately does not start the application or use patient data. */
class TemplateSyntaxTest {
    @TestFactory
    List<DynamicTest> allFreeMarkerTemplatesParse() throws Exception {
        Path root = Paths.get("src/main/resources/templates");
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_30);
        configuration.setDirectoryForTemplateLoading(root.toFile());
        configuration.setDefaultEncoding("UTF-8");
        try (Stream<Path> paths = Files.walk(root)) {
            return paths.filter(path -> path.toString().endsWith(".ftl"))
                    .sorted()
                    .map(path -> root.relativize(path).toString().replace('\\', '/'))
                    .map(name -> DynamicTest.dynamicTest(name, () -> configuration.getTemplate(name)))
                    .collect(Collectors.toList());
        }
    }
}
