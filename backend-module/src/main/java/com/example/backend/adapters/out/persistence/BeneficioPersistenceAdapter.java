package com.example.backend.adapters.out.persistence;

import com.example.backend.adapters.out.persistence.entity.BeneficioJpaEntity;
import com.example.backend.adapters.out.persistence.repository.SpringDataBeneficioRepository;
import com.example.backend.application.port.out.BeneficioRepositoryPort;
import com.example.backend.application.shared.PageResult;
import com.example.backend.domain.model.Beneficio;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Component
public class BeneficioPersistenceAdapter implements BeneficioRepositoryPort {

    private final SpringDataBeneficioRepository repository;

    public BeneficioPersistenceAdapter(SpringDataBeneficioRepository repository) {
        this.repository = repository;
    }

    @Override
    public PageResult<Beneficio> findAll(int page, int size) {
        Page<BeneficioJpaEntity> paged = repository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"))
        );
        return new PageResult<>(
                paged.getContent().stream()
                .map(BeneficioPersistenceAdapter::toDomain)
                .toList(),
                paged.getTotalElements(),
                paged.getNumber(),
                paged.getSize(),
                paged.getTotalPages(),
                paged.hasNext(),
                paged.hasPrevious()
        );
    }

    @Override
    public Optional<Beneficio> findById(Long id) {
        return repository.findById(id).map(BeneficioPersistenceAdapter::toDomain);
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public Beneficio save(Beneficio beneficio) {
        BeneficioJpaEntity entity = toEntity(beneficio);
        return toDomain(repository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    private static Beneficio toDomain(BeneficioJpaEntity entity) {
        return new Beneficio(
                entity.getId(),
                entity.getNome(),
                entity.getDescricao(),
                entity.getValor(),
                entity.getAtivo() != null && entity.getAtivo(),
                entity.getVersion()
        );
    }

    private static BeneficioJpaEntity toEntity(Beneficio beneficio) {
        BeneficioJpaEntity entity = new BeneficioJpaEntity();
        entity.setId(beneficio.id());
        entity.setNome(beneficio.nome());
        entity.setDescricao(beneficio.descricao());
        entity.setValor(beneficio.valor());
        entity.setAtivo(beneficio.ativo());
        entity.setVersion(beneficio.version());
        return entity;
    }
}
