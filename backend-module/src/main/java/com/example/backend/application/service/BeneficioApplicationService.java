package com.example.backend.application.service;

import com.example.backend.application.port.in.BeneficioUseCase;
import com.example.backend.application.port.in.command.AtualizarBeneficioCommand;
import com.example.backend.application.port.in.command.CriarBeneficioCommand;
import com.example.backend.application.port.in.command.TransferirBeneficioCommand;
import com.example.backend.application.port.in.query.ListarBeneficiosQuery;
import com.example.backend.application.port.out.BeneficioRepositoryPort;
import com.example.backend.application.port.out.BeneficioTransferenciaPort;
import com.example.backend.application.shared.PageResult;
import com.example.backend.domain.exception.BeneficioNaoEncontradoException;
import com.example.backend.domain.exception.RegraNegocioException;
import com.example.backend.domain.model.Beneficio;
import java.math.BigDecimal;

public class BeneficioApplicationService implements BeneficioUseCase {

    private final BeneficioRepositoryPort beneficioRepositoryPort;
    private final BeneficioTransferenciaPort beneficioTransferenciaPort;

    public BeneficioApplicationService(
            BeneficioRepositoryPort beneficioRepositoryPort,
            BeneficioTransferenciaPort beneficioTransferenciaPort
    ) {
        this.beneficioRepositoryPort = beneficioRepositoryPort;
        this.beneficioTransferenciaPort = beneficioTransferenciaPort;
    }

    @Override
    public PageResult<Beneficio> listar(ListarBeneficiosQuery query) {
        if (query == null) {
            throw new RegraNegocioException("Consulta de paginacao e obrigatoria");
        }
        validarPaginacao(query.page(), query.size());
        return beneficioRepositoryPort.findAll(query.page(), query.size());
    }

    @Override
    public Beneficio buscarPorId(Long id) {
        if (id == null) {
            throw new RegraNegocioException("Id do beneficio e obrigatorio");
        }
        return beneficioRepositoryPort.findById(id)
                .orElseThrow(() -> new BeneficioNaoEncontradoException(id));
    }

    @Override
    public Beneficio criar(CriarBeneficioCommand command) {
        if (command == null) {
            throw new RegraNegocioException("Comando de criacao e obrigatorio");
        }
        return beneficioRepositoryPort.save(Beneficio.novo(
                command.nome(),
                command.descricao(),
                command.valor(),
                command.ativo()
        ));
    }

    @Override
    public Beneficio atualizar(Long id, AtualizarBeneficioCommand command) {
        if (id == null) {
            throw new RegraNegocioException("Id do beneficio e obrigatorio");
        }
        if (command == null) {
            throw new RegraNegocioException("Comando de atualizacao e obrigatorio");
        }

        Beneficio atual = buscarPorId(id);
        return beneficioRepositoryPort.save(atual.atualizarCom(
                command.nome(),
                command.descricao(),
                command.valor(),
                command.ativo()
        ));
    }

    @Override
    public void transferir(TransferirBeneficioCommand command) {
        if (command == null) {
            throw new RegraNegocioException("Comando de transferencia e obrigatorio");
        }

        if (command.beneficioOrigemId() == null || command.beneficioDestinoId() == null) {
            throw new RegraNegocioException("Ids de origem e destino sao obrigatorios");
        }
        if (command.beneficioOrigemId().equals(command.beneficioDestinoId())) {
            throw new RegraNegocioException("Origem e destino devem ser diferentes");
        }
        if (command.valor() == null || command.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("Valor da transferencia deve ser maior que zero");
        }

        beneficioTransferenciaPort.transferir(
                command.beneficioOrigemId(),
                command.beneficioDestinoId(),
                command.valor()
        );
    }

    @Override
    public void remover(Long id) {
        if (id == null) {
            throw new RegraNegocioException("Id do beneficio e obrigatorio");
        }
        if (!beneficioRepositoryPort.existsById(id)) {
            throw new BeneficioNaoEncontradoException(id);
        }
        beneficioRepositoryPort.deleteById(id);
    }

    private void validarPaginacao(int page, int size) {
        if (page < 0) {
            throw new RegraNegocioException("Parametro page deve ser maior ou igual a zero");
        }
        if (size < 1 || size > 100) {
            throw new RegraNegocioException("Parametro size deve estar entre 1 e 100");
        }
    }
}
