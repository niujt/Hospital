package com.hospital.web;

import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageResponsesTest {
    @Test
    void createsIndependentMutableResponses() {
        JSONObject first = MessageResponses.message("first");
        JSONObject second = MessageResponses.message("second");
        assertNotSame(first, second);
        first.put("message", "changed");
        first.put("extra", true);
        assertEquals("second", second.get("message"));
        assertEquals(1, second.size());
    }

    @Test
    void retainsNullMessageKey() {
        JSONObject response = MessageResponses.message(null);
        assertTrue(response.containsKey("message"));
        assertNull(response.get("message"));
        assertEquals(1, response.size());
    }
}
