package com.gymlog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymlog.dto.response.WorkoutResponse;
import com.gymlog.security.CustomUserDetailsService;
import com.gymlog.security.JwtAuthEntryPoint;
import com.gymlog.security.JwtTokenProvider;
import com.gymlog.service.impl.WorkoutServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkoutController.class)
class WorkoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkoutServiceImpl workoutService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthEntryPoint jwtAuthEntryPoint;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "test@example.com")
    void getWorkout_ShouldReturnWorkout() throws Exception {
        WorkoutResponse response = WorkoutResponse.builder()
                .id(1L)
                .name("Morning Run")
                .build();

        when(workoutService.getWorkoutById(anyLong(), anyString())).thenReturn(response);

        mockMvc.perform(get("/api/workouts/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Morning Run"));
    }
}
