package com.example.backend.adapters.in.web;

import com.example.backend.adapters.in.web.dto.BeneficioRequest;
import com.example.backend.adapters.in.web.dto.BeneficioResponse;
import com.example.backend.adapters.in.web.dto.PageResponse;
import com.example.backend.adapters.in.web.dto.TransferenciaRequest;
import com.example.backend.adapters.in.web.dto.TransferenciaHistoricoResponse;
import com.example.backend.application.port.in.BeneficioUseCase;
import com.example.backend.application.port.in.command.AtualizarBeneficioCommand;
import com.example.backend.application.port.in.command.CriarBeneficioCommand;
import com.example.backend.application.port.in.command.TransferirBeneficioCommand;
import com.example.backend.application.port.in.query.ListarBeneficiosQuery;
import com.example.backend.application.port.in.query.ListarTransferenciasQuery;
import com.example.backend.application.shared.PageResult;
import com.example.backend.domain.model.Beneficio;
import com.example.backend.domain.model.TransferenciaHistorico;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/beneficios")
@Tag(name = "Beneficios", description = "Operacoes de CRUD e transferencia de beneficios")
public class BeneficioController {

    private final BeneficioUseCase beneficioUseCase;

    public BeneficioController(BeneficioUseCase beneficioUseCase) {
        this.beneficioUseCase = beneficioUseCase;
    }

    @Operation(summary = "Listar beneficios com paginacao")
    @GetMapping
    public PageResponse<BeneficioResponse> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResult<Beneficio> result = beneficioUseCase.listar(new ListarBeneficiosQuery(page, size));
        return new PageResponse<>(
                result.items().stream().map(BeneficioController::toResponse).toList(),
                result.totalItems(),
                result.page(),
                result.size(),
                result.totalPages(),
                result.hasNext(),
                result.hasPrevious()
        );
    }

    @Operation(summary = "Buscar beneficio por id")
    @GetMapping("/{id}")
    public BeneficioResponse buscarPorId(@PathVariable Long id) {
        return toResponse(beneficioUseCase.buscarPorId(id));
    }

    @Operation(summary = "Criar beneficio")
    @PostMapping
    public ResponseEntity<BeneficioResponse> criar(@Valid @RequestBody BeneficioRequest request) {
        Beneficio criado = beneficioUseCase.criar(new CriarBeneficioCommand(
                request.nome(),
                request.descricao(),
                request.valor(),
                request.ativo()
        ));

        return ResponseEntity.created(URI.create("/api/v1/beneficios/" + criado.id()))
                .body(toResponse(criado));
    }

    @Operation(summary = "Atualizar beneficio")
    @PutMapping("/{id}")
    public BeneficioResponse atualizar(@PathVariable Long id, @Valid @RequestBody BeneficioRequest request) {
        return toResponse(beneficioUseCase.atualizar(id, new AtualizarBeneficioCommand(
                request.nome(),
                request.descricao(),
                request.valor(),
                request.ativo()
        )));
    }

    @Operation(summary = "Remover beneficio")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        beneficioUseCase.remover(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Transferir valor entre beneficios")
    @PostMapping("/transferencias")
    public ResponseEntity<Void> transferir(@Valid @RequestBody TransferenciaRequest request) {
        beneficioUseCase.transferir(new TransferirBeneficioCommand(
                request.beneficioOrigemId(),
                request.beneficioDestinoId(),
                request.valor()
        ));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar historico de transferencias com paginacao")
    @GetMapping("/transferencias/historico")
    public PageResponse<TransferenciaHistoricoResponse> listarHistoricoTransferencias(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResult<TransferenciaHistorico> result = beneficioUseCase.listarTransferencias(
                new ListarTransferenciasQuery(page, size)
        );
        return new PageResponse<>(
                result.items().stream().map(BeneficioController::toHistoricoResponse).toList(),
                result.totalItems(),
                result.page(),
                result.size(),
                result.totalPages(),
                result.hasNext(),
                result.hasPrevious()
        );
    }

    private static BeneficioResponse toResponse(Beneficio beneficio) {
        return new BeneficioResponse(
                beneficio.id(),
                beneficio.nome(),
                beneficio.descricao(),
                beneficio.valor(),
                beneficio.ativo(),
                beneficio.version()
        );
    }

    private static TransferenciaHistoricoResponse toHistoricoResponse(TransferenciaHistorico historico) {
        return new TransferenciaHistoricoResponse(
                historico.id(),
                historico.beneficioOrigemId(),
                historico.beneficioDestinoId(),
                historico.valor(),
                historico.executadoEm()
        );
    }
}
