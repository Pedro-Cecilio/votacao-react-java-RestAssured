package com.dbserver.restassured.sessaoVotacao;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.port;
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
import com.dbserver.restassured.fixture.VotoExternoFixture;
import com.dbserver.restassured.models.auth.LoginEnvio;
import com.dbserver.restassured.models.pauta.CriarPautaDados;
import com.dbserver.restassured.models.sessaoVotacao.AbrirSessaoVotacaoDados;
import com.dbserver.restassured.models.sessaoVotacao.VotoExternoDados;
import com.dbserver.restassured.utils.TestUtils;

@SpringBootTest
class VotoExternoTest {
    private Integer pautaId;
    private static String tokenAdmin;

    @BeforeAll
    static void setUp() {
        baseURI = "http://localhost/";
        port = 8080;
        LoginEnvio dadosLoginAdmin = LoginFixture.dadosLoginAdminValido();
        tokenAdmin = TestUtils.fazerLoginEObterToken(dadosLoginAdmin);
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
    @DisplayName("Deve ser possível votar externamente em uma pauta")
    void dadosPossuoDadosVotoExternoValidosQuandoVotoExternamenteEmUmaPautaEntaoDeveRetornarStatus200() {
        VotoExternoDados votoExternoDados = VotoExternoFixture.dadosVotoExternoUsuarioExistenteValido(pautaId);

        TestUtils.inserirVotoExterno(votoExternoDados)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .and().assertThat().body("id", notNullValue())
                .and().assertThat().body("pautaId", equalTo(this.pautaId))
                .and().assertThat().body("votosPositivos", equalTo(1));
    }

    @Test
    @DisplayName("Não deve ser possível votar externamente em uma pauta ao informar cpf de usuario cadastrado e não informar senha")
    void dadosCpfUsuarioExistenteESemSenhaQuandoVotoExternamenteEmUmaPautaEntaoDeveRetornarStatus401() {
        VotoExternoDados votoExternoDados = VotoExternoFixture.dadosVotoExternoUsuarioExistenteSemSenha(pautaId);

        TestUtils.inserirVotoExterno(votoExternoDados)
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and().assertThat().body("erro", equalTo("Dados de autenticação inválidos."));
    }

    @Test
    @DisplayName("Não deve ser possível votar externamente em uma pauta ao informar cpf inválido")
    void dadosCpfInvalidoQuandoVotoExternamenteEmUmaPautaEntaoDeveRetornarStatus400() {
        VotoExternoDados votoExternoDados = VotoExternoFixture.dadosVotoExternoUsuarioNaoCadastradoCpfInvalido(pautaId);

        TestUtils.inserirVotoExterno(votoExternoDados)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().assertThat().body("erro", equalTo("Cpf deve conter 11 caracteres numéricos."));
    }
}
