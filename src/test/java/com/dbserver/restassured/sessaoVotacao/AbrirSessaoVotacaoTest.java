package com.dbserver.restassured.sessaoVotacao;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.dbserver.restassured.fixture.AbrirSessaoVotacaoFixture;
import com.dbserver.restassured.fixture.CriarPautaFixture;
import com.dbserver.restassured.fixture.LoginFixture;
import com.dbserver.restassured.models.auth.LoginEnvio;
import com.dbserver.restassured.models.pauta.CriarPautaDados;
import com.dbserver.restassured.models.sessaoVotacao.AbrirSessaoVotacaoDados;
import com.dbserver.restassured.restAssuredConfig.RestAssuredBase;
import com.dbserver.restassured.utils.TestUtils;

@SpringBootTest
class AbrirSessaoVotacaoTest extends RestAssuredBase{
        private static String tokenAdmin;
        private Integer pautaId;

        @BeforeAll
        static void setUp() {
                LoginEnvio dadosLogin = LoginFixture.dadosLoginAdminValido();
                tokenAdmin = TestUtils.fazerLoginEObterToken(dadosLogin);
        }

        @BeforeEach
        void configurar() {
                CriarPautaDados novaPauta = CriarPautaFixture.criarPautaCorretamente();
                this.pautaId = TestUtils.cadastrarPauta(novaPauta, tokenAdmin)
                                .then()
                                .extract().path("id");
        }

        @Test
        @DisplayName("Deve ser possível abrir sessão de votação em uma pauta corretamente")
        void dadosEstouLogadoComoAdminQuandoAbroSessaoVotacaoEntaoDeveRetornarStatus200() {
                AbrirSessaoVotacaoDados dadosAbrirSessaoVotacao = AbrirSessaoVotacaoFixture
                                .abrirSessaoVotacaoCorretamente(this.pautaId);

                TestUtils.abrirSessaoVotacao(dadosAbrirSessaoVotacao, tokenAdmin)
                                .then()
                                .statusCode(HttpStatus.SC_OK)
                                .and().assertThat().body("id", notNullValue())
                                .and().assertThat().body("pautaId", equalTo(this.pautaId));
        }

        @Test
        @DisplayName("Não deve ser possível abrir sessão de votação , ao informar uma pauta inexistente, ou que o admin logado não possui")
        void dadosEstouLogadoComoAdminQuandoAbroSessaoVotacaoEmPautaInexistenteEntaoDeveRetornarStatus404() {
                AbrirSessaoVotacaoDados dadosAbrirSessaoVotacao = AbrirSessaoVotacaoFixture
                                .abrirSessaoVotacaoPautaInexistente();

                TestUtils.abrirSessaoVotacao(dadosAbrirSessaoVotacao, tokenAdmin)
                                .then()
                                .statusCode(HttpStatus.SC_NOT_FOUND)
                                .and().assertThat().body("erro", equalTo("Pauta não encontrada."));
        }

        @Test
        @DisplayName("Não deve ser possível abrir sessão de votação, ao enviar minutos inválidos")
        void dadosEstouLogadoComoAdminQuandoAbroSessaoVotacaoMinutosInvalidosEntaoDeveRetornarStatus404() {
                AbrirSessaoVotacaoDados dadosAbrirSessaoVotacao = AbrirSessaoVotacaoFixture
                                .abrirSessaoVotacaoMinutosInvalido(this.pautaId);

                TestUtils.abrirSessaoVotacao(dadosAbrirSessaoVotacao, tokenAdmin)
                                .then()
                                .statusCode(HttpStatus.SC_BAD_REQUEST)
                                .and().assertThat().body("erro", equalTo("Minutos deve ser maior que 0."));
        }

        @Test
        @DisplayName("Não deve ser possível abrir sessão de votação, ao estar logado como usuário")
        void dadosEstouLogadoComoUsuarioQuandoTentoAbrirSessaoVotacaoEntaoDeveRetornarStatus403() {
                AbrirSessaoVotacaoDados dadosAbrirSessaoVotacao = AbrirSessaoVotacaoFixture
                                .abrirSessaoVotacaoMinutosInvalido(this.pautaId);

                LoginEnvio dadosLogin = LoginFixture.dadosLoginUsuarioValido();

                String tokenUsuario = TestUtils.fazerLoginEObterToken(dadosLogin);

                TestUtils.abrirSessaoVotacao(dadosAbrirSessaoVotacao, tokenUsuario)
                                .then()
                                .statusCode(HttpStatus.SC_FORBIDDEN);
        }

        @Test
        @DisplayName("Não deve ser possível abrir sessão de votação em uma pauta que já teve sessão aberta")
        void dadosEstouLogadoComoAdminQuandoAbroSessaoVotacaoJaAbertaEntaoDeveRetornarStatus400() {
                AbrirSessaoVotacaoDados dadosAbrirSessaoVotacao = AbrirSessaoVotacaoFixture
                                .abrirSessaoVotacaoCorretamente(this.pautaId);

                TestUtils.abrirSessaoVotacao(dadosAbrirSessaoVotacao, tokenAdmin)
                                .then()
                                .statusCode(HttpStatus.SC_OK);

                TestUtils.abrirSessaoVotacao(dadosAbrirSessaoVotacao, tokenAdmin)
                                .then()
                                .statusCode(HttpStatus.SC_BAD_REQUEST)
                                .and().assertThat().body("erro", equalTo("Pauta já possui uma votação aberta."));

        }
}
