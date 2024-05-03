package com.dbserver.restassured.usuario;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.dbserver.restassured.fixture.CriarUsuarioFixture;
import com.dbserver.restassured.fixture.LoginFixture;
import com.dbserver.restassured.models.auth.LoginEnvio;
import com.dbserver.restassured.models.usuario.CriarUsuarioEnvio;

import io.restassured.http.ContentType;

class CriarUsuarioTest {
    private String token;
    private final String ENDPOINT = "usuario";
    @BeforeAll
    static void setUp() {
        baseURI = "http://localhost/";
        port = 8080;
    }

    @BeforeEach
    void configurar() {
        LoginEnvio dadosLogin = LoginFixture.dadosLoginAdminValido();
        this.token = given()
                .body(dadosLogin)
                .contentType(ContentType.JSON)
                .when()
                .post("/auth/login")
                .then()
                .extract().path("token");
    }

    @Test
    void dadosEstouLogadoComoAdminQuandoCrioNovoUsuarioEntaoDeveRetornarNovoUsuario() {

        CriarUsuarioEnvio novoUsuario = CriarUsuarioFixture.criarUsuarioCorretamente();

        given()
                .header("Authorization", token)
                .body(novoUsuario)
                .contentType(ContentType.JSON)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .and().assertThat().body("id", notNullValue())
                .and().assertThat().body("email", equalTo(novoUsuario.getAutenticacaoDto().getEmail()))
                .and().assertThat().body("nome", equalTo(novoUsuario.getNome()))
                .and().assertThat().body("sobrenome", equalTo(novoUsuario.getSobrenome()))
                .and().assertThat().body("cpf", equalTo(novoUsuario.getCpf()))
                .and().assertThat().body("admin", equalTo(novoUsuario.isAdmin()));
    }

    @Test
    void dadosEstouLogadoComoUsuarioQuandoTentoCriarNovoUsuarioEntaoDeveRetornarStatus403() {
        LoginEnvio dadosLogin = LoginFixture.dadosLoginUsuarioValido();
        this.token = given()
                .body(dadosLogin)
                .contentType(ContentType.JSON)
                .when()
                .post("/auth/login")
                .then()
                .extract().path("token");

        CriarUsuarioEnvio novoUsuario = CriarUsuarioFixture.criarUsuarioCorretamente();

        given()
                .header("Authorization", token)
                .body(novoUsuario)
                .contentType(ContentType.JSON)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Test
    void dadosEstouLogadoComoAdminQuandoCrioNovoUsuarioEmailInvalidoEntaoDeveRetornarStatus400() {

        CriarUsuarioEnvio novoUsuario = CriarUsuarioFixture.criarUsuarioEmailInvalido();

        given()
                .header("Authorization", token)
                .body(novoUsuario)
                .contentType(ContentType.JSON)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().assertThat().body("erro", equalTo("Email com formato inválido."));
    }

    @Test
    void dadosEstouLogadoComoAdminQuandoCrioNovoUsuarioSenhaInvalidaEntaoDeveRetornarStatus400() {

        CriarUsuarioEnvio novoUsuario = CriarUsuarioFixture.criarUsuarioSenhaInvalida();

        given()
                .header("Authorization", token)
                .body(novoUsuario)
                .contentType(ContentType.JSON)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().assertThat().body("erro", equalTo("Senha deve conter 8 caracteres no mínimo."));
    }

    @Test
    void dadosEstouLogadoComoAdminQuandoCrioNovoUsuarioNomeInvalidoEntaoDeveRetornarStatus400() {

        CriarUsuarioEnvio novoUsuario = CriarUsuarioFixture.criarUsuarioNomeInvalido();

        given()
                .header("Authorization", token)
                .body(novoUsuario)
                .contentType(ContentType.JSON)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().assertThat().body("erro", equalTo("Nome deve conter entre 3 e 20 caracteres."));
    }

    @Test
    void dadosEstouLogadoComoAdminQuandoCrioNovoUsuarioSobrenomeInvalidoEntaoDeveRetornarStatus400() {

        CriarUsuarioEnvio novoUsuario = CriarUsuarioFixture.criarUsuarioSobrenomeInvalido();

        given()
                .header("Authorization", token)
                .body(novoUsuario)
                .contentType(ContentType.JSON)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().assertThat().body("erro", equalTo("Sobrenome deve conter entre 2 e 20 caracteres."));
    }

    @Test
    void dadosEstouLogadoComoAdminQuandoCrioNovoUsuarioCpfInvalidoEntaoDeveRetornarStatus400() {

        CriarUsuarioEnvio novoUsuario = CriarUsuarioFixture.criarUsuarioCpfInvalido();

        given()
                .header("Authorization", token)
                .body(novoUsuario)
                .contentType(ContentType.JSON)
                .when()
                .post(ENDPOINT)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().assertThat().body("erro", equalTo("Cpf deve conter 11 caracteres numéricos."));
    }
}
