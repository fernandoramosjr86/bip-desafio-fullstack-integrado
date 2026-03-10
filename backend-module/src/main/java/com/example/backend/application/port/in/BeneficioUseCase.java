package com.example.backend.application.port.in;

import com.example.backend.application.port.in.command.AtualizarBeneficioCommand;
import com.example.backend.application.port.in.command.CriarBeneficioCommand;
import com.example.backend.application.port.in.command.TransferirBeneficioCommand;
import com.example.backend.application.port.in.query.ListarBeneficiosQuery;
import com.example.backend.application.port.in.query.ListarTransferenciasQuery;
import com.example.backend.application.shared.PageResult;
import com.example.backend.domain.model.Beneficio;
import com.example.backend.domain.model.TransferenciaHistorico;

public interface BeneficioUseCase {
    PageResult<Beneficio> listar(ListarBeneficiosQuery query);

    Beneficio buscarPorId(Long id);

    Beneficio criar(CriarBeneficioCommand command);

    Beneficio atualizar(Long id, AtualizarBeneficioCommand command);

    void transferir(TransferirBeneficioCommand command);

    PageResult<TransferenciaHistorico> listarTransferencias(ListarTransferenciasQuery query);

    void remover(Long id);
}
