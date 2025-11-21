package com.chrona.repository;

import com.chrona.domain.Phase;
import com.chrona.domain.Project;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class PhaseRepositoryTest {

    @Autowired
    private PhaseRepository phaseRepository;

    @Test
    public void shouldSaveAndFindPhase() {
        Phase phase = Phase.builder()
                .name("Estudo Preliminar")
                .description("Primeira etapa")
                .standard(true)
                .build();

        Phase savedPhase = phaseRepository.save(phase);

        assertThat(savedPhase.getId()).isNotNull();
        assertThat(savedPhase.getName()).isEqualTo("Estudo Preliminar");
    }
}
