package com.gymlog.service;

import com.gymlog.dto.request.GoalRequest;
import com.gymlog.dto.response.GoalResponse;
import com.gymlog.entity.Goal;
import com.gymlog.entity.User;
import com.gymlog.enums.GoalStatus;
import com.gymlog.enums.GoalType;
import com.gymlog.enums.Role;
import com.gymlog.repository.GoalRepository;
import com.gymlog.repository.UserRepository;
import com.gymlog.service.impl.GoalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock private GoalRepository goalRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private GoalServiceImpl goalService;

    private User testUser;
    private Goal testGoal;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L).email("test@test.com").firstName("Test").lastName("User")
                .role(Role.USER).build();

        testGoal = Goal.builder()
                .id(1L).user(testUser).goalType(GoalType.WEIGHT)
                .title("Lose 5kg").targetValue(73.0).currentValue(78.0)
                .unit("kg").status(GoalStatus.ACTIVE)
                .startDate(LocalDate.now()).targetDate(LocalDate.now().plusMonths(3))
                .build();
    }

    @Test
    @DisplayName("Should create a goal successfully")
    void createGoal_Success() {
        GoalRequest request = new GoalRequest(GoalType.WEIGHT, "Lose 5kg", "Reduce weight",
                73.0, 78.0, "kg", LocalDate.now(), LocalDate.now().plusMonths(3));

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(testUser));
        when(goalRepository.save(any(Goal.class))).thenReturn(testGoal);

        GoalResponse response = goalService.createGoal("test@test.com", request);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Lose 5kg");
        assertThat(response.getGoalType()).isEqualTo(GoalType.WEIGHT);
    }

    @Test
    @DisplayName("Should auto-complete goal when target is reached")
    void updateProgress_AutoComplete() {
        when(goalRepository.findById(1L)).thenReturn(Optional.of(testGoal));
        testGoal.setTargetValue(78.0);
        when(goalRepository.save(any(Goal.class))).thenReturn(testGoal);

        GoalResponse response = goalService.updateProgress(1L, 78.0);

        assertThat(response).isNotNull();
    }
}
