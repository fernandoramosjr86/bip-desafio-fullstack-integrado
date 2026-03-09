package com.example.backend.application.port.out;

import com.example.backend.domain.model.Beneficio;
import com.example.backend.application.shared.PageResult;
import java.util.Optional;

public interface BeneficioRepositoryPort {
    PageResult<Beneficio> findAll(int page, int size);

    Optional<Beneficio> findById(Long id);

    boolean existsById(Long id);

    Beneficio save(Beneficio beneficio);

    void deleteById(Long id);
}
