package com.example.taskmanager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void healthEndpoint_ShouldBeAccessibleWithoutToken() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk());
    }

    @Test
    public void tasksAllEndpoint_ShouldReturn401_WhenNoToken() throws Exception {
        mockMvc.perform(get("/api/tasks/all"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void tasksAllEndpoint_ShouldReturn403_WhenUserLacksAdminRole() throws Exception {
        mockMvc.perform(get("/api/tasks/all")
                        .with(jwt().jwt(builder -> builder.claim("scope", "user"))))
                .andExpect(status().isForbidden());
    }

    @Test
    public void tasksAllEndpoint_ShouldReturn200_WhenUserIsAdmin() throws Exception {
        mockMvc.perform(get("/api/tasks/all")
                        .with(jwt().jwt(builder -> builder.claim("scope", "admin"))))
                .andExpect(status().isOk());
    }
}