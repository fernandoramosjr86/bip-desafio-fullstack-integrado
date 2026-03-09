package com.example.backend.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.backend.BackendApplication;
import com.example.backend.application.port.in.BeneficioUseCase;
import com.example.backend.application.port.in.command.CriarBeneficioCommand;
import com.example.backend.application.port.in.query.ListarBeneficiosQuery;
import com.example.backend.application.service.BeneficioApplicationService;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.AopTestUtils;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest(
        classes = BackendApplication.class,
        properties = {
                "spring.sql.init.mode=never",
                "spring.datasource.url=jdbc:h2:mem:beneficiosdb_configtest;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
        }
)
class ApplicationLayerConfigurationTest {

    @Autowired
    private BeneficioUseCase beneficioUseCase;

    @Autowired
    private BeneficioApplicationService beneficioApplicationService;

    @Test
    void beneficioUseCaseDeveEstarProxificadoComInterceptorTransacional() throws Exception {
        assertTrue(AopUtils.isAopProxy(beneficioUseCase));

        Class<?> targetClass = AopUtils.getTargetClass(beneficioUseCase);
        assertTrue(ApplicationLayerConfiguration.TransactionalBeneficioUseCase.class.isAssignableFrom(targetClass));

        assertTrue(beneficioUseCase instanceof Advised);
        Advised advised = (Advised) beneficioUseCase;

        TransactionInterceptor transactionInterceptor = Arrays.stream(advised.getAdvisors())
                .map(Advisor::getAdvice)
                .filter(TransactionInterceptor.class::isInstance)
                .map(TransactionInterceptor.class::cast)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Interceptor transacional nao encontrado"));

        TransactionAttributeSource transactionAttributeSource = transactionInterceptor.getTransactionAttributeSource();
        assertNotNull(transactionAttributeSource);

        Method listarMethod = ApplicationLayerConfiguration.TransactionalBeneficioUseCase.class
                .getMethod("listar", ListarBeneficiosQuery.class);
        Method criarMethod = ApplicationLayerConfiguration.TransactionalBeneficioUseCase.class
                .getMethod("criar", CriarBeneficioCommand.class);

        TransactionAttribute listarTx = transactionAttributeSource.getTransactionAttribute(
                listarMethod,
                ApplicationLayerConfiguration.TransactionalBeneficioUseCase.class
        );
        TransactionAttribute criarTx = transactionAttributeSource.getTransactionAttribute(
                criarMethod,
                ApplicationLayerConfiguration.TransactionalBeneficioUseCase.class
        );

        assertNotNull(listarTx);
        assertTrue(listarTx.isReadOnly());
        assertNotNull(criarTx);
        assertFalse(criarTx.isReadOnly());
    }

    @Test
    void beneficioUseCaseDeveDelegarParaCoreService() throws Exception {
        Object target = AopTestUtils.getUltimateTargetObject(beneficioUseCase);
        Field delegateField = target.getClass().getDeclaredField("delegate");
        delegateField.setAccessible(true);
        Object delegate = delegateField.get(target);

        assertSame(beneficioApplicationService, delegate);
    }

    @Test
    void coreServiceDevePermanecerSemProxyDeFramework() {
        assertFalse(AopUtils.isAopProxy(beneficioApplicationService));
    }
}
