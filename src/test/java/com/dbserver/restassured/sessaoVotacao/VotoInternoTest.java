package com.dbserver.restassured.sessaoVotacao;

import static io.restassured.RestAssured.given;
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
import com.dbserver.restassured.fixture.VotoInternoFixture;
import com.dbserver.restassured.models.auth.LoginEnvio;
import com.dbserver.restassured.models.pauta.CriarPautaDados;
import com.dbserver.restassured.models.sessaoVotacao.AbrirSessaoVotacaoDados;
import com.dbserver.restassured.models.sessaoVotacao.VotoInternoDados;
import com.dbserver.restassured.restAssuredConfig.RestAssuredBase;
import com.dbserver.restassured.utils.TestUtils;

import io.restassured.http.ContentType;

@SpringBootTest
class VotoInternoTest extends RestAssuredBase{
        private static String tokenAdmin;
        private static String tokenUsuario;
        private Integer pautaId;

        @BeforeAll
        static void setUp() {
                LoginEnvio dadosLoginAdmin = LoginFixture.dadosLoginAdminValido();
                LoginEnvio dadosLoginUsuario = LoginFixture.dadosLoginUsuarioValido();

                tokenAdmin = TestUtils.fazerLoginEObterToken(dadosLoginAdmin);
                tokenUsuario = TestUtils.fazerLoginEObterToken(dadosLoginUsuario);
        }

        @BeforeEach
        void configurar() {
                CriarPautaDados novaPauta = CriarPautaFixture.criarPautaCorretamente();

                this.pautaId = TestUtils.cadastrarPauta(novaPauta, tokenAdmin)
                                .then()
                                .extract().path("id");

                AbrirSessaoVotacaoDados dadosAbrirSessaoVotacao = AbrirSessaoVotacaoFixture
                                .abrirSessaoVotacaoCorretamente(this.pautaId);

                TestUtils.abrirSessaoVotacao(dadosAbrirSessaoVotacao, tokenAdmin)
                                .then()
                                .statusCode(HttpStatus.SC_OK)
                                .and().assertThat().body("id", notNullValue())
                                .and().assertThat().body("pautaId", equalTo(this.pautaId));
        }

        @Test
        @DisplayName("Deve ser possível votar em uma pauta estando logado na aplicação")
        void dadosEstouLogadoComoUsuarioQuandoVotoEmUmaPautaAbertaEntaoDeveRetornarStatus200() {
                VotoInternoDados votoInternoDados = VotoInternoFixture.dadosVotoInternoPositivoValidos(pautaId);

                TestUtils.inserirVotoInterno(votoInternoDados, tokenUsuario)
                                .then()
                                .statusCode(HttpStatus.SC_OK)
                                .and().assertThat().body("id", notNullValue())
                                .and().assertThat().body("pautaId", equalTo(this.pautaId))
                                .and().assertThat().body("votosPositivos", equalTo(1));
        }

        @Test
        @DisplayName("Não deve ser possível votar em uma pauta que já votei")
        void dadosEstouLogadoComoUsuarioQuandoVotoEmUmaPautaQueJaVoteiEntaoDeveRetornarStatus400() {
                VotoInternoDados votoInternoDados = VotoInternoFixture.dadosVotoInternoPositivoValidos(pautaId);

                TestUtils.inserirVotoInterno(votoInternoDados, tokenUsuario)
                                .then()
                                .statusCode(HttpStatus.SC_OK);

                TestUtils.inserirVotoInterno(votoInternoDados, tokenUsuario)
                                .then()
                                .statusCode(HttpStatus.SC_BAD_REQUEST)
                                .and().assertThat().body("erro", equalTo("Não é possível votar duas vezes."));
        }

        @Test
        @DisplayName("Não deve ser possível votar em uma pauta ao informar tipo de voto inválido")
        void dadosEstouLogadoComoUsuarioQuandoVotoEmUmaPautaTipoDeVotoInvalidoEntaoDeveRetornarStatus400() {
                VotoInternoDados votoInternoDados = VotoInternoFixture.dadosVotoInternoTipoVotoNulo(pautaId);

                TestUtils.inserirVotoInterno(votoInternoDados, tokenUsuario)
                                .then()
                                .statusCode(HttpStatus.SC_BAD_REQUEST)
                                .and().assertThat().body("erro", equalTo("O tipo do voto deve ser informado."));
        }

        @Test
        @DisplayName("Não deve ser possível o criador da pauta votar na pauta criada")
        void dadosEstouLogadoComCriadorDaPautaQuandoVotoEmUmaPautaAbertaEntaoDeveRetornarStatus400() {
                VotoInternoDados votoInternoDados = VotoInternoFixture.dadosVotoInternoPositivoValidos(pautaId);

                TestUtils.inserirVotoInterno(votoInternoDados, tokenAdmin)
                                .then()
                                .statusCode(HttpStatus.SC_BAD_REQUEST)
                                .and().assertThat().body("erro", equalTo("O criador não pode votar na pauta criada."));

        }

        @Test
        @DisplayName("Não deve ser possível votar internamente em uma pauta quando não estiver logado na aplicação")
        void dadosNaoEstouLogadoQuandoTentoVotarEmUmaPautaAbertaEntaoDeveRetornarStatus403() {
                VotoInternoDados votoInternoDados = VotoInternoFixture.dadosVotoInternoPositivoValidos(pautaId);

                given()
                                .body(votoInternoDados)
                                .contentType(ContentType.JSON)
                                .when()
                                .patch("votacao/votoInterno")
                                .then()
                                .statusCode(HttpStatus.SC_FORBIDDEN);
        }
}
