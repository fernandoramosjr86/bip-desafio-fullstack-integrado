package com.example.ejb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.ejb.exception.BeneficioEjbNaoEncontradoException;
import com.example.ejb.exception.TransferenciaEjbInvalidaException;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BeneficioEjbServiceTest {

    private FakeEntityManager fakeEm;

    private BeneficioEjbService service;

    @BeforeEach
    void setup() {
        fakeEm = new FakeEntityManager();
        service = new BeneficioEjbService(fakeEm.proxy());
    }

    @Test
    void transferComValorInvalidoDeveFalhar() {
        TransferenciaEjbInvalidaException ex = assertThrows(
                TransferenciaEjbInvalidaException.class,
                () -> service.transfer(1L, 2L, BigDecimal.ZERO)
        );

        assertEquals("Valor da transferencia deve ser maior que zero", ex.getMessage());
        assertEquals(0, fakeEm.getFindCalls());
    }

    @Test
    void transferComValorNuloDeveFalharSemConsultarEntidades() {
        TransferenciaEjbInvalidaException ex = assertThrows(
                TransferenciaEjbInvalidaException.class,
                () -> service.transfer(1L, 2L, null)
        );

        assertEquals("Valor da transferencia deve ser maior que zero", ex.getMessage());
        assertEquals(0, fakeEm.getFindCalls());
    }

    @Test
    void transferComOrigemInexistenteDeveFalhar() {
        BeneficioEjbNaoEncontradoException ex = assertThrows(
                BeneficioEjbNaoEncontradoException.class,
                () -> service.transfer(1L, 2L, BigDecimal.ONE)
        );

        assertEquals(1L, ex.getBeneficioId());
        assertEquals(1, fakeEm.getFindCalls());
    }

    @Test
    void transferComDestinoInexistenteDeveFalhar() {
        Beneficio from = beneficio(1L, BigDecimal.TEN, true);
        fakeEm.stubFind(1L, from);

        BeneficioEjbNaoEncontradoException ex = assertThrows(
                BeneficioEjbNaoEncontradoException.class,
                () -> service.transfer(1L, 2L, BigDecimal.ONE)
        );

        assertEquals(2L, ex.getBeneficioId());
        assertEquals(0, fakeEm.getMergeCalls());
    }

    @Test
    void transferComSaldoInsuficienteDeveFalhar() {
        Beneficio from = beneficio(1L, BigDecimal.ONE, true);
        Beneficio to = beneficio(2L, BigDecimal.TEN, true);
        fakeEm.stubFind(1L, from);
        fakeEm.stubFind(2L, to);

        TransferenciaEjbInvalidaException ex = assertThrows(
                TransferenciaEjbInvalidaException.class,
                () -> service.transfer(1L, 2L, new BigDecimal("2.00"))
        );

        assertEquals("Saldo insuficiente para transferencia", ex.getMessage());
        assertEquals(0, fakeEm.getMergeCalls());
    }

    @Test
    void transferComBeneficioInativoDeveFalhar() {
        Beneficio from = beneficio(1L, BigDecimal.TEN, false);
        Beneficio to = beneficio(2L, BigDecimal.TEN, true);
        fakeEm.stubFind(1L, from);
        fakeEm.stubFind(2L, to);

        TransferenciaEjbInvalidaException ex = assertThrows(
                TransferenciaEjbInvalidaException.class,
                () -> service.transfer(1L, 2L, BigDecimal.ONE)
        );

        assertEquals("Transferencia permitida apenas entre beneficios ativos", ex.getMessage());
        assertEquals(0, fakeEm.getMergeCalls());
    }

    @Test
    void transferComDestinoInativoDeveFalhar() {
        Beneficio from = beneficio(1L, BigDecimal.TEN, true);
        Beneficio to = beneficio(2L, BigDecimal.TEN, false);
        fakeEm.stubFind(1L, from);
        fakeEm.stubFind(2L, to);

        TransferenciaEjbInvalidaException ex = assertThrows(
                TransferenciaEjbInvalidaException.class,
                () -> service.transfer(1L, 2L, BigDecimal.ONE)
        );

        assertEquals("Transferencia permitida apenas entre beneficios ativos", ex.getMessage());
        assertEquals(0, fakeEm.getMergeCalls());
    }

    @Test
    void transferComValorNuloNosBeneficiosDeveFalhar() {
        Beneficio from = beneficio(1L, null, true);
        Beneficio to = beneficio(2L, BigDecimal.TEN, true);
        fakeEm.stubFind(1L, from);
        fakeEm.stubFind(2L, to);

        TransferenciaEjbInvalidaException ex = assertThrows(
                TransferenciaEjbInvalidaException.class,
                () -> service.transfer(1L, 2L, BigDecimal.ONE)
        );

        assertEquals("Beneficios com valor invalido para transferencia", ex.getMessage());
    }

    @Test
    void transferComIdsNulosDeveFalharSemConsultarEntidades() {
        TransferenciaEjbInvalidaException ex = assertThrows(
                TransferenciaEjbInvalidaException.class,
                () -> service.transfer(null, 2L, BigDecimal.ONE)
        );

        assertEquals("Ids de origem e destino sao obrigatorios", ex.getMessage());
        assertEquals(0, fakeEm.getFindCalls());
    }

    @Test
    void transferComIdsIguaisDeveFalharSemConsultarEntidades() {
        TransferenciaEjbInvalidaException ex = assertThrows(
                TransferenciaEjbInvalidaException.class,
                () -> service.transfer(1L, 1L, BigDecimal.ONE)
        );

        assertEquals("Origem e destino devem ser diferentes", ex.getMessage());
        assertEquals(0, fakeEm.getFindCalls());
    }

    @Test
    void transferValidaDeveAtualizarValoresComLockEPersistir() {
        Beneficio from = beneficio(1L, new BigDecimal("10.00"), true);
        Beneficio to = beneficio(2L, new BigDecimal("3.00"), true);
        fakeEm.stubFind(1L, from);
        fakeEm.stubFind(2L, to);

        service.transfer(1L, 2L, new BigDecimal("2.50"));

        assertEquals(new BigDecimal("7.50"), from.getValor());
        assertEquals(new BigDecimal("5.50"), to.getValor());
        assertEquals(2, fakeEm.getMergeCalls());
        assertEquals(1, fakeEm.getFlushCalls());
        assertEquals(from, fakeEm.getMerged().get(0));
        assertEquals(to, fakeEm.getMerged().get(1));
    }

    @Test
    void transferSemEntityManagerInicializadoDeveFalhar() {
        BeneficioEjbService serviceSemEntityManager = new BeneficioEjbService();

        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> serviceSemEntityManager.transfer(1L, 2L, BigDecimal.ONE)
        );

        assertEquals("EntityManager nao inicializado", ex.getMessage());
    }

    private Beneficio beneficio(Long id, BigDecimal valor, boolean ativo) {
        Beneficio beneficio = new Beneficio();
        beneficio.setId(id);
        beneficio.setValor(valor);
        beneficio.setAtivo(ativo);
        return beneficio;
    }

    private static final class FakeEntityManager implements InvocationHandler {

        private final Map<Long, Beneficio> beneficioPorId = new HashMap<>();
        private final List<Beneficio> merged = new ArrayList<>();
        private int findCalls;
        private int mergeCalls;
        private int flushCalls;

        EntityManager proxy() {
            return (EntityManager) Proxy.newProxyInstance(
                    EntityManager.class.getClassLoader(),
                    new Class[] { EntityManager.class },
                    this
            );
        }

        void stubFind(Long id, Beneficio beneficio) {
            beneficioPorId.put(id, beneficio);
        }

        int getFindCalls() {
            return findCalls;
        }

        int getMergeCalls() {
            return mergeCalls;
        }

        int getFlushCalls() {
            return flushCalls;
        }

        List<Beneficio> getMerged() {
            return merged;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            String methodName = method.getName();
            if ("find".equals(methodName) && args != null && args.length >= 2) {
                findCalls++;
                if (args[1] instanceof Long id) {
                    return beneficioPorId.get(id);
                }
                return null;
            }

            if ("merge".equals(methodName) && args != null && args.length == 1) {
                mergeCalls++;
                Beneficio beneficio = (Beneficio) args[0];
                merged.add(beneficio);
                return beneficio;
            }

            if ("flush".equals(methodName)) {
                flushCalls++;
                return null;
            }

            if ("close".equals(methodName)) {
                return null;
            }

            if ("isOpen".equals(methodName)) {
                return true;
            }

            if ("toString".equals(methodName)) {
                return "FakeEntityManager";
            }

            if ("hashCode".equals(methodName)) {
                return System.identityHashCode(proxy);
            }

            if ("equals".equals(methodName) && args != null && args.length == 1) {
                return proxy == args[0];
            }

            throw new UnsupportedOperationException("Metodo nao suportado no fake EntityManager: " + method);
        }
    }
}
