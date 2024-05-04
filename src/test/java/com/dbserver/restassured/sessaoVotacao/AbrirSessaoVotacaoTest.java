package com.dbserver.restassured.sessaoVotacao;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.port;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.dbserver.restassured.fixture.AbrirSessaoVotacaoFixture;
import com.dbserver.restassured.fixture.CriarPautaFixture;
import com.dbserver.restassured.fixture.LoginFixture;
import com.dbserver.restassured.models.auth.LoginEnvio;
import com.dbserver.restassured.models.pauta.CriarPautaDados;
import com.dbserver.restassured.models.sessaoVotacao.AbrirSessaoVotacaoDados;
import com.dbserver.restassured.utils.TestUtils;


@SpringBootTest
class AbrirSessaoVotacaoTest {
        private static String tokenAdmin;
        private Integer pautaId;

        @BeforeAll
        static void setUp() {
                baseURI = "http://localhost/";
                port = 8080;
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
        void dadosEstouLogadoComoAdminQuandoAbroSessaoVotacaoEmPautaInexistenteEntaoDeveRetornarStatus404() {
                AbrirSessaoVotacaoDados dadosAbrirSessaoVotacao = AbrirSessaoVotacaoFixture
                                .abrirSessaoVotacaoPautaInexistente();

                TestUtils.abrirSessaoVotacao(dadosAbrirSessaoVotacao, tokenAdmin)
                                .then()
                                .statusCode(HttpStatus.SC_NOT_FOUND)
                                .and().assertThat().body("erro", equalTo("Pauta não encontrada."));
        }

        @Test
        void dadosEstouLogadoComoAdminQuandoAbroSessaoVotacaoMinutosInvalidosEntaoDeveRetornarStatus404() {
                AbrirSessaoVotacaoDados dadosAbrirSessaoVotacao = AbrirSessaoVotacaoFixture
                                .abrirSessaoVotacaoMinutosInvalido(this.pautaId);

                TestUtils.abrirSessaoVotacao(dadosAbrirSessaoVotacao, tokenAdmin)
                                .then()
                                .statusCode(HttpStatus.SC_BAD_REQUEST)
                                .and().assertThat().body("erro", equalTo("Minutos deve ser maior que 0."));
        }

        @Test
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
