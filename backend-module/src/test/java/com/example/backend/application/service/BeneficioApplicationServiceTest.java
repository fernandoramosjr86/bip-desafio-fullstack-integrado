package com.example.backend.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.backend.application.port.in.command.TransferirBeneficioCommand;
import com.example.backend.application.port.in.query.ListarBeneficiosQuery;
import com.example.backend.application.port.out.BeneficioRepositoryPort;
import com.example.backend.application.port.out.BeneficioTransferenciaPort;
import com.example.backend.application.shared.PageResult;
import com.example.backend.domain.exception.BeneficioNaoEncontradoException;
import com.example.backend.domain.exception.RegraNegocioException;
import com.example.backend.domain.model.Beneficio;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BeneficioApplicationServiceTest {

    @Mock
    private BeneficioRepositoryPort beneficioRepositoryPort;

    @Mock
    private BeneficioTransferenciaPort beneficioTransferenciaPort;

    private BeneficioApplicationService service;

    @BeforeEach
    void setup() {
        service = new BeneficioApplicationService(beneficioRepositoryPort, beneficioTransferenciaPort);
    }

    @Test
    void listarComPaginacaoValidaDeveDelegarParaRepositorio() {
        PageResult<Beneficio> expected = new PageResult<>(
                List.of(new Beneficio(1L, "A", "D", BigDecimal.TEN, true, 0L)),
                1,
                0,
                10,
                1,
                false,
                false
        );
        when(beneficioRepositoryPort.findAll(0, 10)).thenReturn(expected);

        PageResult<Beneficio> actual = service.listar(new ListarBeneficiosQuery(0, 10));

        assertEquals(1, actual.items().size());
        verify(beneficioRepositoryPort).findAll(0, 10);
    }

    @Test
    void listarComPageNegativoDeveFalhar() {
        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> service.listar(new ListarBeneficiosQuery(-1, 10))
        );

        assertEquals("Parametro page deve ser maior ou igual a zero", ex.getMessage());
        verify(beneficioRepositoryPort, never()).findAll(anyInt(), anyInt());
    }

    @Test
    void listarComQueryNulaDeveFalhar() {
        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> service.listar(null)
        );

        assertEquals("Consulta de paginacao e obrigatoria", ex.getMessage());
        verify(beneficioRepositoryPort, never()).findAll(anyInt(), anyInt());
    }

    @Test
    void listarComSizeInvalidoDeveFalhar() {
        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> service.listar(new ListarBeneficiosQuery(0, 0))
        );

        assertEquals("Parametro size deve estar entre 1 e 100", ex.getMessage());
        verify(beneficioRepositoryPort, never()).findAll(anyInt(), anyInt());
    }

    @Test
    void transferirComIdsIguaisDeveFalharSemDelegar() {
        TransferirBeneficioCommand command = new TransferirBeneficioCommand(1L, 1L, BigDecimal.ONE);

        RegraNegocioException ex = assertThrows(RegraNegocioException.class, () -> service.transferir(command));

        assertEquals("Origem e destino devem ser diferentes", ex.getMessage());
        verify(beneficioTransferenciaPort, never()).transferir(any(), any(), any());
    }

    @Test
    void transferirDeveDelegarSemPreValidacaoDeExistencia() {
        TransferirBeneficioCommand command = new TransferirBeneficioCommand(1L, 2L, BigDecimal.ONE);

        service.transferir(command);

        verify(beneficioTransferenciaPort).transferir(1L, 2L, BigDecimal.ONE);
        verify(beneficioRepositoryPort, never()).existsById(any());
    }

    @Test
    void transferirValidaDeveDelegarParaPortaDeTransferencia() {
        TransferirBeneficioCommand command = new TransferirBeneficioCommand(1L, 2L, BigDecimal.ONE);

        service.transferir(command);

        verify(beneficioTransferenciaPort).transferir(1L, 2L, BigDecimal.ONE);
        verify(beneficioRepositoryPort, never()).existsById(any());
    }

    @Test
    void buscarPorIdQuandoExistirDeveRetornarBeneficio() {
        Beneficio beneficio = new Beneficio(7L, "Nome", "Desc", BigDecimal.TEN, true, 0L);
        when(beneficioRepositoryPort.findById(7L)).thenReturn(Optional.of(beneficio));

        Beneficio atual = service.buscarPorId(7L);

        assertEquals(7L, atual.id());
        assertEquals("Nome", atual.nome());
        verify(beneficioRepositoryPort).findById(7L);
    }

    @Test
    void buscarPorIdQuandoNaoExistirDeveFalhar() {
        when(beneficioRepositoryPort.findById(42L)).thenReturn(Optional.empty());

        BeneficioNaoEncontradoException ex = assertThrows(
                BeneficioNaoEncontradoException.class,
                () -> service.buscarPorId(42L)
        );

        assertEquals("Beneficio nao encontrado para id=42", ex.getMessage());
    }

    @Test
    void criarComAtivoNuloDeveAssumirAtivoEAplicarTrimNoNome() {
        when(beneficioRepositoryPort.save(any(Beneficio.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, Beneficio.class));

        Beneficio criado = service.criar(new com.example.backend.application.port.in.command.CriarBeneficioCommand(
                "  Beneficio Novo  ",
                "Descricao",
                new BigDecimal("10.00"),
                null
        ));

        assertEquals("Beneficio Novo", criado.nome());
        assertTrue(criado.ativo());
    }

    @Test
    void criarComNomeInvalidoDeveFalhar() {
        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> service.criar(new com.example.backend.application.port.in.command.CriarBeneficioCommand(
                        " ",
                        "Desc",
                        BigDecimal.ONE,
                        true
                ))
        );

        assertEquals("Nome do beneficio e obrigatorio", ex.getMessage());
        verify(beneficioRepositoryPort, never()).save(any(Beneficio.class));
    }

    @Test
    void criarComValorInvalidoDeveFalhar() {
        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> service.criar(new com.example.backend.application.port.in.command.CriarBeneficioCommand(
                        "Beneficio",
                        "Desc",
                        BigDecimal.ZERO,
                        true
                ))
        );

        assertEquals("Valor do beneficio deve ser maior que zero", ex.getMessage());
        verify(beneficioRepositoryPort, never()).save(any(Beneficio.class));
    }

    @Test
    void atualizarComAtivoNuloDevePreservarStatusAtual() {
        Beneficio existente = new Beneficio(1L, "Atual", "Desc", new BigDecimal("5.00"), false, 3L);
        when(beneficioRepositoryPort.findById(1L)).thenReturn(Optional.of(existente));
        when(beneficioRepositoryPort.save(any(Beneficio.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, Beneficio.class));

        Beneficio atualizado = service.atualizar(
                1L,
                new com.example.backend.application.port.in.command.AtualizarBeneficioCommand(
                        "  Novo Nome ",
                        "Nova Desc",
                        new BigDecimal("9.00"),
                        null
                )
        );

        assertEquals(1L, atualizado.id());
        assertEquals("Novo Nome", atualizado.nome());
        assertEquals(new BigDecimal("9.00"), atualizado.valor());
        assertFalse(atualizado.ativo());
        assertEquals(3L, atualizado.version());
    }

    @Test
    void removerComIdExistenteDeveDelegarExclusao() {
        when(beneficioRepositoryPort.existsById(4L)).thenReturn(true);

        service.remover(4L);

        verify(beneficioRepositoryPort).deleteById(4L);
    }

    @Test
    void removerComIdInexistenteDeveFalhar() {
        when(beneficioRepositoryPort.existsById(5L)).thenReturn(false);

        BeneficioNaoEncontradoException ex = assertThrows(
                BeneficioNaoEncontradoException.class,
                () -> service.remover(5L)
        );

        assertEquals("Beneficio nao encontrado para id=5", ex.getMessage());
        verify(beneficioRepositoryPort, never()).deleteById(any());
    }

    @Test
    void transferirComIdsNulosDeveFalhar() {
        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> service.transferir(new TransferirBeneficioCommand(null, 2L, BigDecimal.ONE))
        );

        assertEquals("Ids de origem e destino sao obrigatorios", ex.getMessage());
        verify(beneficioTransferenciaPort, never()).transferir(any(), any(), any());
    }

    @Test
    void transferirComValorNuloDeveFalhar() {
        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> service.transferir(new TransferirBeneficioCommand(1L, 2L, null))
        );

        assertEquals("Valor da transferencia deve ser maior que zero", ex.getMessage());
        verify(beneficioTransferenciaPort, never()).transferir(any(), any(), any());
    }

    @Test
    void transferirComCommandNuloDeveFalhar() {
        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> service.transferir(null)
        );

        assertEquals("Comando de transferencia e obrigatorio", ex.getMessage());
        verify(beneficioTransferenciaPort, never()).transferir(any(), any(), any());
    }

    @Test
    void buscarPorIdComIdNuloDeveFalhar() {
        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> service.buscarPorId(null)
        );

        assertEquals("Id do beneficio e obrigatorio", ex.getMessage());
        verify(beneficioRepositoryPort, never()).findById(any());
    }

    @Test
    void criarComCommandNuloDeveFalhar() {
        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> service.criar(null)
        );

        assertEquals("Comando de criacao e obrigatorio", ex.getMessage());
        verify(beneficioRepositoryPort, never()).save(any(Beneficio.class));
    }

    @Test
    void atualizarComCommandNuloDeveFalhar() {
        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> service.atualizar(1L, null)
        );

        assertEquals("Comando de atualizacao e obrigatorio", ex.getMessage());
        verify(beneficioRepositoryPort, never()).save(any(Beneficio.class));
    }

    @Test
    void removerComIdNuloDeveFalhar() {
        RegraNegocioException ex = assertThrows(
                RegraNegocioException.class,
                () -> service.remover(null)
        );

        assertEquals("Id do beneficio e obrigatorio", ex.getMessage());
        verify(beneficioRepositoryPort, never()).deleteById(any());
    }
}
