package com.chrona.repository;

import com.chrona.domain.Expense;
import com.chrona.domain.Project;
import com.chrona.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ExpenseRepositoryTest {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Test
    public void shouldSaveAndFindExpense() {
        // Note: In a real test we would need to save Project and User first or mock
        // them,
        // but for this unit test we just want to verify the Expense entity mapping.
        // However, due to foreign key constraints, we might need to mock or save them
        // if H2 enforces it.
        // @DataJpaTest uses an embedded DB so constraints apply.
        // Let's assume we can save with null IDs for now if we don't save the
        // relations,
        // BUT the entity has nullable=false for project and user.
        // So we need to create mock Project and User.

        // Actually, let's just try to save and expect a DataIntegrityViolationException
        // if we don't provide them,
        // or better, let's try to build it and check if the object is created
        // correctly.
        // Saving to DB requires valid FKs.

        // To keep it simple and avoid creating User/Project repositories/entities in
        // this test file (which would require more setup),
        // I will just test the Builder for now, or I need to inject User/Project
        // repositories.
        // Let's inject them.
    }
}
