package com.phouthasak.controlHQ.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_login_thenAccessProtectedEndpoint() throws Exception {
        String registerBody = "{\"username\":\"carol\",\"email\":\"carol@example.com\",\"password\":\"password123\"}";
        mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON).content(registerBody))
                .andExpect(status().isCreated());

        String loginBody = "{\"username\":\"carol\",\"password\":\"password123\"}";
        String loginResponse = mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(loginResponse);
        String token = node.path("data").path("token").asText();
        assertTrue(!token.isEmpty(), "login response should contain a token: " + loginResponse);

        mockMvc.perform(get("/api/v1/devices").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void register_duplicateUsername_returnsBadRequest() throws Exception {
        String body = "{\"username\":\"dave\",\"email\":\"dave@example.com\",\"password\":\"password123\"}";
        mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"dave\",\"email\":\"dave2@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_invalidPayload_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\",\"email\":\"not-an-email\",\"password\":\"short\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void protectedEndpoint_withoutToken_isRejected() throws Exception {
        mockMvc.perform(get("/api/v1/devices"))
                .andExpect(result -> assertTrue(
                        result.getResponse().getStatus() == 401 || result.getResponse().getStatus() == 403));
    }
}
