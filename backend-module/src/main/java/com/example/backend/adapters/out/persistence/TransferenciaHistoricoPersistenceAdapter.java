package com.example.backend.adapters.out.persistence;

import com.example.backend.adapters.out.persistence.entity.TransferenciaHistoricoJpaEntity;
import com.example.backend.adapters.out.persistence.repository.SpringDataTransferenciaHistoricoRepository;
import com.example.backend.application.port.out.TransferenciaHistoricoPort;
import com.example.backend.application.shared.PageResult;
import com.example.backend.domain.model.TransferenciaHistorico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class TransferenciaHistoricoPersistenceAdapter implements TransferenciaHistoricoPort {

    private final SpringDataTransferenciaHistoricoRepository repository;

    public TransferenciaHistoricoPersistenceAdapter(SpringDataTransferenciaHistoricoRepository repository) {
        this.repository = repository;
    }

    @Override
    public TransferenciaHistorico save(TransferenciaHistorico historico) {
        TransferenciaHistoricoJpaEntity entity = toEntity(historico);
        return toDomain(repository.save(entity));
    }

    @Override
    public PageResult<TransferenciaHistorico> findAll(int page, int size) {
        Page<TransferenciaHistoricoJpaEntity> paged = repository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "executadoEm"))
        );
        return new PageResult<>(
                paged.getContent().stream()
                        .map(TransferenciaHistoricoPersistenceAdapter::toDomain)
                        .toList(),
                paged.getTotalElements(),
                paged.getNumber(),
                paged.getSize(),
                paged.getTotalPages(),
                paged.hasNext(),
                paged.hasPrevious()
        );
    }

    private static TransferenciaHistoricoJpaEntity toEntity(TransferenciaHistorico historico) {
        TransferenciaHistoricoJpaEntity entity = new TransferenciaHistoricoJpaEntity();
        entity.setId(historico.id());
        entity.setBeneficioOrigemId(historico.beneficioOrigemId());
        entity.setBeneficioDestinoId(historico.beneficioDestinoId());
        entity.setValor(historico.valor());
        entity.setExecutadoEm(historico.executadoEm());
        return entity;
    }

    private static TransferenciaHistorico toDomain(TransferenciaHistoricoJpaEntity entity) {
        return new TransferenciaHistorico(
                entity.getId(),
                entity.getBeneficioOrigemId(),
                entity.getBeneficioDestinoId(),
                entity.getValor(),
                entity.getExecutadoEm()
        );
    }
}
