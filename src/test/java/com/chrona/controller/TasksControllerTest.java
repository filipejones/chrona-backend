package com.chrona.controller;

import com.chrona.domain.Project;
import com.chrona.domain.Task;
import com.chrona.domain.Phase;
import com.chrona.dto.TaskRequest;
import com.chrona.repository.ProjectRepository;
import com.chrona.repository.TaskRepository;
import com.chrona.repository.PhaseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TasksControllerTest {

    @Mock
    TaskRepository taskRepository;
    @Mock
    ProjectRepository projectRepository;
    @Mock
    PhaseRepository phaseRepository;

    @InjectMocks
    TasksController controller;

    private TaskRequest buildRequest(Long projectId, Long parentId, Long phaseId) {
        TaskRequest req = new TaskRequest();
        req.setProjectId(projectId);
        req.setParentId(parentId);
        req.setPhaseId(phaseId);
        req.setName("Task");
        req.setBillable(true);
        req.setStatus("Ativo");
        return req;
    }

    @Test
    void createShouldRejectParentFromAnotherProject() {
        Project project1 = Project.builder().id(1L).build();
        Project project2 = Project.builder().id(2L).build();
        Task parent = Task.builder().id(10L).project(project2).build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project1));
        when(taskRepository.findById(10L)).thenReturn(Optional.of(parent));

        TaskRequest req = buildRequest(1L, 10L, null);
        ResponseEntity<?> resp = controller.create(req);

        assertThat(resp.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void createShouldRejectPhaseFromAnotherProject() {
        Project project1 = Project.builder().id(1L).build();
        Project project2 = Project.builder().id(2L).build();
        Phase phase = Phase.builder().id(5L).project(project2).build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project1));
        when(taskRepository.findById(10L)).thenReturn(Optional.empty());
        when(phaseRepository.findById(5L)).thenReturn(Optional.of(phase));

        TaskRequest req = buildRequest(1L, null, 5L);
        ResponseEntity<?> resp = controller.create(req);

        assertThat(resp.getStatusCode().is4xxClientError()).isTrue();
    }
}
