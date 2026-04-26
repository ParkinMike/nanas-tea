package com.parkin.mike.config;

import com.parkin.mike.controller.NanasController;
import com.parkin.mike.exception.GlobalExceptionHandler;
import com.parkin.mike.service.NanasService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NanasController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
@TestPropertySource(properties = "app.passcode=averysecurepass")
class SecurityConfigWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NanasService nanasService;

    @Test
    void unauthenticatedRequestsRedirectToLogin() throws Exception {
        mockMvc.perform(get("/nanas"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "nanas", roles = "USER")
    void authenticatedRequestUsesConfiguredSecurityHeaders() throws Exception {
        when(nanasService.isNanasOnThisWeek()).thenReturn(true);

        mockMvc.perform(get("/nanas"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Security-Policy", "default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; img-src 'self' data:; object-src 'none'; base-uri 'self'; form-action 'self'; frame-ancestors 'none'"))
                .andExpect(header().string("Referrer-Policy", "same-origin"))
                .andExpect(cookie().exists("XSRF-TOKEN"));
    }

    @Test
    @WithMockUser(username = "nanas", roles = "USER")
    void postWithoutCsrfIsForbidden() throws Exception {
        mockMvc.perform(post("/nanas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\":\"2026-04-26\",\"on\":true}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "nanas", roles = "USER")
    void postWithCsrfIsAllowed() throws Exception {
        mockMvc.perform(post("/nanas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\":\"2026-04-26\",\"on\":true}"))
                .andExpect(status().isOk());
    }
}
