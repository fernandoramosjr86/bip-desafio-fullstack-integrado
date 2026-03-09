package com.example.backend.adapters.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.backend.adapters.out.persistence.entity.BeneficioJpaEntity;
import com.example.backend.adapters.out.persistence.repository.SpringDataBeneficioRepository;
import com.example.backend.application.shared.PageResult;
import com.example.backend.domain.model.Beneficio;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
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
class BeneficioPersistenceAdapterTest {

    @Mock
    private SpringDataBeneficioRepository repository;

    private BeneficioPersistenceAdapter adapter;

    @BeforeEach
    void setup() {
        adapter = new BeneficioPersistenceAdapter(repository);
    }

    @Test
    void findAllDeveMapearPaginacaoEConverterEntidades() {
        BeneficioJpaEntity entity = entity(1L, "A", "D", new BigDecimal("12.00"), null, 3L);
        PageImpl<BeneficioJpaEntity> page = new PageImpl<>(List.of(entity), PageRequest.of(0, 5), 1);
        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        PageResult<Beneficio> result = adapter.findAll(0, 5);

        assertEquals(1, result.items().size());
        assertEquals(1L, result.totalItems());
        assertEquals(0, result.page());
        assertEquals(5, result.size());
        assertFalse(result.items().get(0).ativo());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).findAll(pageableCaptor.capture());
        Sort.Order order = pageableCaptor.getValue().getSort().getOrderFor("id");
        assertEquals(Sort.Direction.ASC, order.getDirection());
    }

    @Test
    void findByIdDeveMapearEntidadeParaDominio() {
        when(repository.findById(2L)).thenReturn(Optional.of(entity(
                2L,
                "Beneficio",
                "Desc",
                new BigDecimal("7.50"),
                true,
                1L
        )));

        Optional<Beneficio> result = adapter.findById(2L);

        assertTrue(result.isPresent());
        assertEquals("Beneficio", result.get().nome());
        assertEquals(new BigDecimal("7.50"), result.get().valor());
    }

    @Test
    void saveDeveConverterDominioParaEntidadeERetornarDominio() {
        when(repository.save(any(BeneficioJpaEntity.class))).thenAnswer(invocation -> {
            BeneficioJpaEntity persisted = invocation.getArgument(0, BeneficioJpaEntity.class);
            persisted.setId(9L);
            persisted.setVersion(4L);
            return persisted;
        });

        Beneficio salvo = adapter.save(new Beneficio(
                null,
                "Novo",
                "Descricao",
                new BigDecimal("3.00"),
                true,
                null
        ));

        assertEquals(9L, salvo.id());
        assertEquals("Novo", salvo.nome());
        assertTrue(salvo.ativo());
        assertEquals(4L, salvo.version());
    }

    @Test
    void existsByIdEDeleteByIdDevemDelegarParaRepositorio() {
        when(repository.existsById(10L)).thenReturn(true);

        assertTrue(adapter.existsById(10L));
        adapter.deleteById(10L);

        verify(repository).existsById(10L);
        verify(repository).deleteById(10L);
    }

    private BeneficioJpaEntity entity(
            Long id,
            String nome,
            String descricao,
            BigDecimal valor,
            Boolean ativo,
            Long version
    ) {
        BeneficioJpaEntity entity = new BeneficioJpaEntity();
        entity.setId(id);
        entity.setNome(nome);
        entity.setDescricao(descricao);
        entity.setValor(valor);
        entity.setAtivo(ativo);
        entity.setVersion(version);
        return entity;
    }
}
