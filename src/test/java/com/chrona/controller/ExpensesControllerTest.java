package com.chrona.controller;

import com.chrona.domain.Phase;
import com.chrona.domain.Project;
import com.chrona.domain.User;
import com.chrona.dto.ExpenseRequest;
import com.chrona.repository.ExpenseRepository;
import com.chrona.repository.PhaseRepository;
import com.chrona.repository.ProjectRepository;
import com.chrona.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpensesControllerTest {

    @Mock
    ExpenseRepository expenseRepository;
    @Mock
    ProjectRepository projectRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    PhaseRepository phaseRepository;

    @InjectMocks
    ExpensesController controller;

    private ExpenseRequest buildReq(Long projectId, Long userId, Long phaseId) {
        ExpenseRequest req = new ExpenseRequest();
        req.setProjectId(projectId);
        req.setUserId(userId);
        req.setPhaseId(phaseId);
        req.setAmount(BigDecimal.valueOf(1000));
        req.setDate(LocalDate.now());
        req.setDescription("Despesa teste");
        req.setReimbursable(false);
        return req;
    }

    @Test
    void createShouldRejectInvalidProject() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<?> resp = controller.create(buildReq(1L, 1L, null));
        assertThat(resp.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void createShouldRejectInvalidUser() {
        Project p = Project.builder().id(1L).build();
        when(projectRepository.findById(1L)).thenReturn(Optional.of(p));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<?> resp = controller.create(buildReq(1L, 99L, null));
        assertThat(resp.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void createShouldRejectPhaseFromOtherProject() {
        Project p = Project.builder().id(1L).build();
        User u = User.builder().id(1L).build();
        Phase ph = Phase.builder().id(5L).project(Project.builder().id(2L).build()).build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(p));
        when(userRepository.findById(1L)).thenReturn(Optional.of(u));
        when(phaseRepository.findById(5L)).thenReturn(Optional.of(ph));

        ResponseEntity<?> resp = controller.create(buildReq(1L, 1L, 5L));
        assertThat(resp.getStatusCode().is4xxClientError()).isTrue();
    }
}
