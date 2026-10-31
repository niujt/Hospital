package com.hospital.controller;

import com.hospital.entity.Option;
import com.hospital.service.OptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OptionHttpContractTest {
    private OptionService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(OptionService.class);
        OptionController controller = new OptionController();
        controller.optionService = service;
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void deleteRetainsRouteAndUnicodeMessage() throws Exception {
        when(service.deleteOption(42)).thenReturn("删除成功");
        mvc.perform(delete("/admin/option/42"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"message\":\"删除成功\"}", true));
        verify(service).deleteOption(42);
        verifyNoMoreInteractions(service);
    }

    @Test
    void postRetainsBodyBindingAndFailureMessage() throws Exception {
        when(service.insert(any(Option.class))).thenReturn("操作失败");
        mvc.perform(post("/admin/option").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\":\"操作失败\"}", true));
        verify(service).insert(any(Option.class));
        verifyNoMoreInteractions(service);
    }

    @Test
    void putRetainsNullMessageSerialization() throws Exception {
        mvc.perform(put("/admin/option").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\":null}", true));
        verify(service).updateBOption(any(Option.class));
        verifyNoMoreInteractions(service);
    }
}
