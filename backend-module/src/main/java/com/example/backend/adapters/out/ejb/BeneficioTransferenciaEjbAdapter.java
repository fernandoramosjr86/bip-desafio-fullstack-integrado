package com.example.backend.adapters.out.ejb;

import com.example.backend.adapters.out.ejb.integration.EjbTransferenciaClient;
import com.example.backend.adapters.out.ejb.integration.EjbTransferenciaNaoEncontradaException;
import com.example.backend.adapters.out.ejb.integration.EjbTransferenciaRegraException;
import com.example.backend.application.port.out.BeneficioTransferenciaPort;
import com.example.backend.domain.exception.BeneficioNaoEncontradoException;
import com.example.backend.domain.exception.RegraNegocioException;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class BeneficioTransferenciaEjbAdapter implements BeneficioTransferenciaPort {

    private final EjbTransferenciaClient ejbTransferenciaClient;

    public BeneficioTransferenciaEjbAdapter(EjbTransferenciaClient ejbTransferenciaClient) {
        this.ejbTransferenciaClient = ejbTransferenciaClient;
    }

    @Override
    public void transferir(Long beneficioOrigemId, Long beneficioDestinoId, BigDecimal valor) {
        try {
            ejbTransferenciaClient.transferir(beneficioOrigemId, beneficioDestinoId, valor);
        } catch (EjbTransferenciaNaoEncontradaException ex) {
            throw new BeneficioNaoEncontradoException(ex.getBeneficioId());
        } catch (EjbTransferenciaRegraException ex) {
            throw new RegraNegocioException(ex.getMessage());
        }
    }
}
