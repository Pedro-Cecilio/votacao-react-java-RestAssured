package com.dbserver.restassured.sessaoVotacao;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
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
import com.dbserver.restassured.fixture.VotoInternoFixture;
import com.dbserver.restassured.models.auth.LoginEnvio;
import com.dbserver.restassured.models.pauta.CriarPautaDados;
import com.dbserver.restassured.models.sessaoVotacao.AbrirSessaoVotacaoDados;
import com.dbserver.restassured.models.sessaoVotacao.VotoInternoDados;

import io.restassured.http.ContentType;

@SpringBootTest
class VotoInternoTest {
    private String token;
    private Integer pautaId;
    private static final String ENDPOINT = "votacao/votoInterno";

    @BeforeAll
    static void setUp() {
        baseURI = "http://localhost/";
        port = 8080;
    }

    @BeforeEach
    void configurar() {
        LoginEnvio dadosLogin = LoginFixture.dadosLoginAdminValido();
        CriarPautaDados novaPauta = CriarPautaFixture.criarPautaCorretamente();

        this.token = given()
                .body(dadosLogin)
                .contentType(ContentType.JSON)
                .when()
                .post("/auth/login")
                .then()
                .extract().path("token");

        this.pautaId = given()
                .header("Authorization", token)
                .body(novaPauta)
                .contentType(ContentType.JSON)
                .when()
                .post("pauta")
                .then()
                .extract().path("id");

        AbrirSessaoVotacaoDados dadosAbrirSessaoVotacao = AbrirSessaoVotacaoFixture
                .abrirSessaoVotacaoCorretamente(this.pautaId);

        given()
                .header("Authorization", token)
                .body(dadosAbrirSessaoVotacao)
                .contentType(ContentType.JSON)
                .when()
                .post("votacao/abrir")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .and().assertThat().body("id", notNullValue())
                .and().assertThat().body("pautaId", equalTo(this.pautaId));
    }

    @Test
    void dadosEstouLogadoComoUsuarioQuandoVotoEmUmaPautaAbertaEntaoDeveRetornarStatus200() {
        LoginEnvio dadosLogin = LoginFixture.dadosLoginUsuarioValido();
        VotoInternoDados votoInternoDados = VotoInternoFixture.dadosVotoInternoPositivoValidos(pautaId);
        this.token = given()
                .body(dadosLogin)
                .contentType(ContentType.JSON)
                .when()
                .post("/auth/login")
                .then()
                .extract().path("token");

        given()
                .header("Authorization", token)
                .body(votoInternoDados)
                .contentType(ContentType.JSON)
                .when()
                .patch(ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .and().assertThat().body("id", notNullValue())
                .and().assertThat().body("pautaId", equalTo(this.pautaId))
                .and().assertThat().body("votosPositivos", equalTo(1));
    }

    @Test
    void dadosEstouLogadoComoUsuarioQuandoVotoEmUmaPautaQueJaVoteiEntaoDeveRetornarStatus400() {
        LoginEnvio dadosLogin = LoginFixture.dadosLoginUsuarioValido();

        VotoInternoDados votoInternoDados = VotoInternoFixture.dadosVotoInternoPositivoValidos(pautaId);
        
        this.token = given()
                .body(dadosLogin)
                .contentType(ContentType.JSON)
                .when()
                .post("/auth/login")
                .then()
                .extract().path("token");

        given()
                .header("Authorization", token)
                .body(votoInternoDados)
                .contentType(ContentType.JSON)
                .when()
                .patch(ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_OK);

        given()
                .header("Authorization", token)
                .body(votoInternoDados)
                .contentType(ContentType.JSON)
                .when()
                .patch(ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().assertThat().body("erro", equalTo("Não é possível votar duas vezes."));
    }

    @Test
    void dadosEstouLogadoComoUsuarioQuandoVotoEmUmaPautaTipoDeVotoInvalidoEntaoDeveRetornarStatus400() {
        LoginEnvio dadosLogin = LoginFixture.dadosLoginUsuarioValido();

        VotoInternoDados votoInternoDados = VotoInternoFixture.dadosVotoInternoTipoVotoNulo(pautaId);

        this.token = given()
                .body(dadosLogin)
                .contentType(ContentType.JSON)
                .when()
                .post("/auth/login")
                .then()
                .extract().path("token");

        given()
                .header("Authorization", token)
                .body(votoInternoDados)
                .contentType(ContentType.JSON)
                .when()
                .patch(ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().assertThat().body("erro", equalTo("O tipo do voto deve ser informado."));
    }
    @Test
    void dadosEstouLogadoComCriadorDaPautaQuandoVotoEmUmaPautaAbertaEntaoDeveRetornarStatus400() {
        VotoInternoDados votoInternoDados = VotoInternoFixture.dadosVotoInternoPositivoValidos(pautaId);

        given()
                .header("Authorization", token)
                .body(votoInternoDados)
                .contentType(ContentType.JSON)
                .when()
                .patch(ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().assertThat().body("erro", equalTo("O criador não pode votar na pauta criada."));
    }

    @Test
    void dadosNaoEstouLogadoQuandoTentoVotarEmUmaPautaAbertaEntaoDeveRetornarStatus403() {
        VotoInternoDados votoInternoDados = VotoInternoFixture.dadosVotoInternoPositivoValidos(pautaId);

        given()
                .body(votoInternoDados)
                .contentType(ContentType.JSON)
                .when()
                .patch(ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }
}
