package com.example.backend.config;

import com.example.backend.application.port.in.BeneficioUseCase;
import com.example.backend.application.port.in.command.AtualizarBeneficioCommand;
import com.example.backend.application.port.in.command.CriarBeneficioCommand;
import com.example.backend.application.port.in.command.TransferirBeneficioCommand;
import com.example.backend.application.port.in.query.ListarBeneficiosQuery;
import com.example.backend.application.port.in.query.ListarTransferenciasQuery;
import com.example.backend.application.port.out.BeneficioRepositoryPort;
import com.example.backend.application.port.out.BeneficioTransferenciaPort;
import com.example.backend.application.port.out.TransferenciaEventPublisherPort;
import com.example.backend.application.port.out.TransferenciaHistoricoPort;
import com.example.backend.application.service.BeneficioApplicationService;
import com.example.backend.application.shared.PageResult;
import com.example.backend.domain.model.Beneficio;
import com.example.backend.domain.model.TransferenciaHistorico;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class ApplicationLayerConfiguration {

    @Bean
    BeneficioApplicationService beneficioApplicationService(
            BeneficioRepositoryPort beneficioRepositoryPort,
            BeneficioTransferenciaPort beneficioTransferenciaPort,
            TransferenciaHistoricoPort transferenciaHistoricoPort,
            TransferenciaEventPublisherPort transferenciaEventPublisherPort
    ) {
        return new BeneficioApplicationService(
                beneficioRepositoryPort,
                beneficioTransferenciaPort,
                transferenciaHistoricoPort,
                transferenciaEventPublisherPort
        );
    }

    @Bean
    @Primary
    BeneficioUseCase beneficioUseCase(BeneficioApplicationService delegate) {
        return new TransactionalBeneficioUseCase(delegate);
    }

    public static class TransactionalBeneficioUseCase implements BeneficioUseCase {

        private final BeneficioUseCase delegate;

        public TransactionalBeneficioUseCase(BeneficioUseCase delegate) {
            this.delegate = delegate;
        }

        @Override
        @Transactional(readOnly = true)
        public PageResult<Beneficio> listar(ListarBeneficiosQuery query) {
            return delegate.listar(query);
        }

        @Override
        @Transactional(readOnly = true)
        public Beneficio buscarPorId(Long id) {
            return delegate.buscarPorId(id);
        }

        @Override
        @Transactional
        public Beneficio criar(CriarBeneficioCommand command) {
            return delegate.criar(command);
        }

        @Override
        @Transactional
        public Beneficio atualizar(Long id, AtualizarBeneficioCommand command) {
            return delegate.atualizar(id, command);
        }

        @Override
        @Transactional
        public void transferir(TransferirBeneficioCommand command) {
            delegate.transferir(command);
        }

        @Override
        @Transactional(readOnly = true)
        public PageResult<TransferenciaHistorico> listarTransferencias(ListarTransferenciasQuery query) {
            return delegate.listarTransferencias(query);
        }

        @Override
        @Transactional
        public void remover(Long id) {
            delegate.remover(id);
        }
    }
}
