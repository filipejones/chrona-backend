package com.chrona.controller;

import com.chrona.domain.Project;
import com.chrona.dto.ProjectOverview;
import com.chrona.repository.ProjectRepository;
import com.chrona.repository.TaskRepository;
import com.chrona.service.ProjectOverviewService;
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
class ProjectsControllerTest {

    @Mock
    ProjectRepository projectRepository;
    @Mock
    TaskRepository taskRepository;
    @Mock
    ProjectOverviewService overviewService;

    @InjectMocks
    ProjectsController controller;

    @Test
    void listTasksShould404WhenProjectMissing() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());
        ResponseEntity<?> resp = controller.listTasksByProject(99L, 0, 10);
        assertThat(resp.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void overviewShould404WhenProjectMissing() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());
        ResponseEntity<?> resp = controller.getOverview(99L);
        assertThat(resp.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    void overviewShould500OnServiceError() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(Project.builder().id(1L).build()));
        when(overviewService.getOverview(1L)).thenThrow(new RuntimeException("boom"));
        ResponseEntity<?> resp = controller.getOverview(1L);
        assertThat(resp.getStatusCode().is5xxServerError()).isTrue();
    }

    @Test
    void overviewShould200WhenOk() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(Project.builder().id(1L).build()));
        when(overviewService.getOverview(1L)).thenReturn(
                new ProjectOverview(1L, "p", "c", "Ativo", 1L, 0L, 0, 0, 0, 0.0, 0.0, 0.0)
        );
        ResponseEntity<?> resp = controller.getOverview(1L);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
    }
}
