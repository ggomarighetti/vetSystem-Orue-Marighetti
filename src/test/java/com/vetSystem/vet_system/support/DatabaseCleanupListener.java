package com.vetSystem.vet_system.support;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;

public class DatabaseCleanupListener extends AbstractTestExecutionListener {

    @Override
    public int getOrder() {
        return SqlScriptsTestExecutionListener.ORDER - 1;
    }

    @Override
    public void beforeTestMethod(TestContext testContext) {
        limpiar(testContext);
    }

    @Override
    public void afterTestMethod(TestContext testContext) {
        limpiar(testContext);
    }

    private void limpiar(TestContext testContext) {
        var context = testContext.getApplicationContext();
        var transaction = new TransactionTemplate(context.getBean(PlatformTransactionManager.class));
        transaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        var scripts = new ResourceDatabasePopulator(new ClassPathResource("fixtures/cleanup.sql"));
        scripts.setSqlScriptEncoding(StandardCharsets.UTF_8.name());
        transaction.executeWithoutResult(status -> scripts.execute(context.getBean(DataSource.class)));
    }
}
