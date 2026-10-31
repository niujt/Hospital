package com.hospital.web;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/** Guards the versioned local vendor paths and removal of the superseded custom theme. */
class LayuiAssetMigrationTest {
    @Test
    void officialDistributionAndLicenseArePresent() throws Exception {
        Path vendor = Paths.get("src/main/resources/static/vendor/layui/2.13.9");
        assertTrue(Files.isRegularFile(vendor.resolve("layui.js")));
        assertTrue(Files.isRegularFile(vendor.resolve("css/layui.css")));
        assertTrue(Files.isRegularFile(vendor.resolve("font/iconfont.woff2")));
        assertTrue(Files.isRegularFile(vendor.resolve("LICENSE")));
        String script = new String(Files.readAllBytes(vendor.resolve("layui.js")), StandardCharsets.UTF_8);
        assertTrue(script.contains("v2.13.9"));
    }

    @Test
    void templatesDoNotReferenceRemovedFrameworkOrThemeAssets() throws Exception {
        List<Path> templates;
        try (Stream<Path> paths = Files.walk(Paths.get("src/main/resources/templates"))) {
            templates = paths.filter(path -> path.toString().endsWith(".ftl")).collect(Collectors.toList());
        }
        boolean hasVersionedScript = false;
        boolean hasVersionedStylesheet = false;
        for (Path template : templates) {
            String text = new String(Files.readAllBytes(template), StandardCharsets.UTF_8);
            for (String obsolete : new String[]{"layui.all.js", "layui.min.css", "/static/css/workspace.css", "/static/js/workspace.js"}) {
                assertFalse(text.contains(obsolete), template + " still references " + obsolete);
            }
            hasVersionedScript |= text.contains("/static/vendor/layui/2.13.9/layui.js");
            hasVersionedStylesheet |= text.contains("/static/vendor/layui/2.13.9/css/layui.css");
        }
        assertTrue(hasVersionedScript);
        assertTrue(hasVersionedStylesheet);
    }
}
