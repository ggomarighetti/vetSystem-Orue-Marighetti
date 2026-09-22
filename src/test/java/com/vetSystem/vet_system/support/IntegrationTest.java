package com.vetSystem.vet_system.support;

import com.vetSystem.vet_system.repository.DuenoRepository;
import com.vetSystem.vet_system.repository.MascotaRepository;
import com.vetSystem.vet_system.repository.TurnoRepository;
import com.vetSystem.vet_system.repository.VeterinarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.jdbc.SqlMergeMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionTemplate;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ResourceLock("api-database")
@SqlConfig(encoding = "UTF-8", transactionMode = SqlConfig.TransactionMode.ISOLATED)
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
public abstract class IntegrationTest {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected DuenoRepository duenoRepository;

    @Autowired
    protected MascotaRepository mascotaRepository;

    @Autowired
    protected TurnoRepository turnoRepository;

    @Autowired
    protected VeterinarioRepository veterinarioRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @AfterEach
    protected final void vaciarBaseDeDatos() {
        transactionTemplate.executeWithoutResult(status -> {
            turnoRepository.deleteAllInBatch();
            mascotaRepository.deleteAllInBatch();
            duenoRepository.deleteAllInBatch();
            veterinarioRepository.deleteAllInBatch();
        });
    }
}
