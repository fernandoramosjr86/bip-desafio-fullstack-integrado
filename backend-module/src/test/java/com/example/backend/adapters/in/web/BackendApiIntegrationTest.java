package com.example.backend.adapters.in.web;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class BackendApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveExporOpenApi() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"title\":\"Beneficios API\"")));
    }

    @Test
    void deveListarBeneficiosComPaginacao() throws Exception {
        mockMvc.perform(get("/api/v1/beneficios")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Correlation-Id"))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.totalItems", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.totalPages", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.hasPrevious").value(false));
    }

    @Test
    void deveRetornarErroDeValidacaoAoCriarBeneficioInvalido() throws Exception {
        mockMvc.perform(post("/api/v1/beneficios")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "",
                                  "descricao": "desc",
                                  "valor": 0,
                                  "ativo": true
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Erro de validacao"))
                .andExpect(jsonPath("$.fields.nome").value("nome e obrigatorio"))
                .andExpect(jsonPath("$.fields.valor").value("valor deve ser maior que zero"));
    }

    @Test
    void deveExecutarFluxoCrudCompleto() throws Exception {
        Long id = criarBeneficio("Beneficio CRUD", "Inicial", new BigDecimal("123.45"), true);

        mockMvc.perform(get("/api/v1/beneficios/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value("Beneficio CRUD"))
                .andExpect(jsonPath("$.ativo").value(true));

        mockMvc.perform(put("/api/v1/beneficios/{id}", id)
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Beneficio Atualizado",
                                  "descricao": "Novo",
                                  "valor": 200.00,
                                  "ativo": false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value("Beneficio Atualizado"))
                .andExpect(jsonPath("$.ativo").value(false));

        mockMvc.perform(delete("/api/v1/beneficios/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/beneficios/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Beneficio nao encontrado para id=" + id));
    }

    @Test
    void deveTransferirValorEntreBeneficios() throws Exception {
        Long origemId = criarBeneficio("Origem Transferencia", "Origem", new BigDecimal("100.00"), true);
        Long destinoId = criarBeneficio("Destino Transferencia", "Destino", new BigDecimal("50.00"), true);

        mockMvc.perform(post("/api/v1/beneficios/transferencias")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "beneficioOrigemId": %d,
                                  "beneficioDestinoId": %d,
                                  "valor": 20.00
                                }
                                """.formatted(origemId, destinoId)))
                .andExpect(status().isNoContent());

        assertValorBeneficio(origemId, new BigDecimal("80.00"));
        assertValorBeneficio(destinoId, new BigDecimal("70.00"));

        mockMvc.perform(get("/api/v1/beneficios/transferencias/historico")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.items[0].beneficioOrigemId").value(origemId))
                .andExpect(jsonPath("$.items[0].beneficioDestinoId").value(destinoId));
    }

    @Test
    void deveRetornarErroQuandoTransferenciaSemSaldo() throws Exception {
        Long origemId = criarBeneficio("Origem Sem Saldo", "Origem", new BigDecimal("10.00"), true);
        Long destinoId = criarBeneficio("Destino Sem Saldo", "Destino", new BigDecimal("5.00"), true);

        mockMvc.perform(post("/api/v1/beneficios/transferencias")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "beneficioOrigemId": %d,
                                  "beneficioDestinoId": %d,
                                  "valor": 99.00
                                }
                                """.formatted(origemId, destinoId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Saldo insuficiente para transferencia"));
    }

    @Test
    void deveRetornarErroQuandoTransferenciaEnvolveBeneficioInativo() throws Exception {
        Long origemId = criarBeneficio("Origem Inativa", "Origem", new BigDecimal("50.00"), false);
        Long destinoId = criarBeneficio("Destino Ativo", "Destino", new BigDecimal("20.00"), true);

        mockMvc.perform(post("/api/v1/beneficios/transferencias")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "beneficioOrigemId": %d,
                                  "beneficioDestinoId": %d,
                                  "valor": 10.00
                                }
                                """.formatted(origemId, destinoId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Transferencia permitida apenas entre beneficios ativos"));
    }

    private Long criarBeneficio(String nome, String descricao, BigDecimal valor, boolean ativo) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/beneficios")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "%s",
                                  "descricao": "%s",
                                  "valor": %s,
                                  "ativo": %s
                                }
                                """.formatted(nome, descricao, valor, ativo)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/beneficios/")))
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        return body.path("id").asLong();
    }

    private void assertValorBeneficio(Long id, BigDecimal expected) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/beneficios/{id}", id))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        BigDecimal atual = body.path("valor").decimalValue().setScale(2);
        org.junit.jupiter.api.Assertions.assertEquals(expected.setScale(2), atual);
    }
}
