package com.example.backend.adapters.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.backend.adapters.out.persistence.entity.TransferenciaHistoricoJpaEntity;
import com.example.backend.adapters.out.persistence.repository.SpringDataTransferenciaHistoricoRepository;
import com.example.backend.application.shared.PageResult;
import com.example.backend.domain.model.TransferenciaHistorico;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class TransferenciaHistoricoPersistenceAdapterTest {

    @Mock
    private SpringDataTransferenciaHistoricoRepository repository;

    private TransferenciaHistoricoPersistenceAdapter adapter;

    @BeforeEach
    void setup() {
        adapter = new TransferenciaHistoricoPersistenceAdapter(repository);
    }

    @Test
    void saveDevePersistirHistoricoEConverterParaDominio() {
        when(repository.save(any(TransferenciaHistoricoJpaEntity.class))).thenAnswer(invocation -> {
            TransferenciaHistoricoJpaEntity entity = invocation.getArgument(0, TransferenciaHistoricoJpaEntity.class);
            entity.setId(77L);
            return entity;
        });

        TransferenciaHistorico salvo = adapter.save(new TransferenciaHistorico(
                null,
                1L,
                2L,
                new BigDecimal("15.00"),
                Instant.parse("2026-03-09T14:00:00Z")
        ));

        assertEquals(77L, salvo.id());
        assertEquals(1L, salvo.beneficioOrigemId());
        assertEquals(2L, salvo.beneficioDestinoId());
    }

    @Test
    void findAllDeveRetornarPageResultOrdenadoPorExecucaoDesc() {
        TransferenciaHistoricoJpaEntity entity = new TransferenciaHistoricoJpaEntity();
        entity.setId(1L);
        entity.setBeneficioOrigemId(2L);
        entity.setBeneficioDestinoId(3L);
        entity.setValor(new BigDecimal("8.00"));
        entity.setExecutadoEm(Instant.parse("2026-03-09T10:00:00Z"));

        PageImpl<TransferenciaHistoricoJpaEntity> page = new PageImpl<>(
                List.of(entity),
                PageRequest.of(0, 5),
                1
        );
        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        PageResult<TransferenciaHistorico> result = adapter.findAll(0, 5);

        assertEquals(1, result.items().size());
        assertEquals(1L, result.totalItems());
        assertEquals(2L, result.items().get(0).beneficioOrigemId());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).findAll(pageableCaptor.capture());
        Sort.Order order = pageableCaptor.getValue().getSort().getOrderFor("executadoEm");
        assertEquals(Sort.Direction.DESC, order.getDirection());
    }
}
