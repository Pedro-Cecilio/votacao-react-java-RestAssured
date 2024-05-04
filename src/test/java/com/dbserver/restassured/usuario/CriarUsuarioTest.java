package com.dbserver.restassured.usuario;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.dbserver.restassured.fixture.CriarUsuarioFixture;
import com.dbserver.restassured.fixture.LoginFixture;
import com.dbserver.restassured.models.auth.LoginEnvio;
import com.dbserver.restassured.models.usuario.CriarUsuarioEnvio;
import com.dbserver.restassured.utils.TestUtils;


@SpringBootTest
class CriarUsuarioTest {
    private static String tokenAdmin;

    @BeforeAll
    static void setUp() {
        baseURI = "http://localhost/";
        port = 8080;

        LoginEnvio dadosLogin = LoginFixture.dadosLoginAdminValido();
        tokenAdmin = TestUtils.fazerLoginEObterToken(dadosLogin);
    }

    @Test
    void dadosEstouLogadoComoAdminQuandoCrioNovoUsuarioEntaoDeveRetornarNovoUsuario() {

        CriarUsuarioEnvio novoUsuario = CriarUsuarioFixture.criarUsuarioCorretamente();

        TestUtils.cadastrarUsuario(novoUsuario, tokenAdmin)
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
        String tokenUsuario = TestUtils.fazerLoginEObterToken(dadosLogin);

        CriarUsuarioEnvio novoUsuario = CriarUsuarioFixture.criarUsuarioCorretamente();

        TestUtils.cadastrarUsuario(novoUsuario, tokenUsuario)
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Test
    void dadosEstouLogadoComoAdminQuandoCrioNovoUsuarioEmailInvalidoEntaoDeveRetornarStatus400() {

        CriarUsuarioEnvio novoUsuario = CriarUsuarioFixture.criarUsuarioEmailInvalido();

        TestUtils.cadastrarUsuario(novoUsuario, tokenAdmin)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().assertThat().body("erro", equalTo("Email com formato inválido."));

    }

    @Test
    void dadosEstouLogadoComoAdminQuandoCrioNovoUsuarioSenhaInvalidaEntaoDeveRetornarStatus400() {

        CriarUsuarioEnvio novoUsuario = CriarUsuarioFixture.criarUsuarioSenhaInvalida();

        TestUtils.cadastrarUsuario(novoUsuario, tokenAdmin)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().assertThat().body("erro", equalTo("Senha deve conter 8 caracteres no mínimo."));
    }

    @Test
    void dadosEstouLogadoComoAdminQuandoCrioNovoUsuarioNomeInvalidoEntaoDeveRetornarStatus400() {

        CriarUsuarioEnvio novoUsuario = CriarUsuarioFixture.criarUsuarioNomeInvalido();

        TestUtils.cadastrarUsuario(novoUsuario, tokenAdmin)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().assertThat().body("erro", equalTo("Nome deve conter entre 3 e 20 caracteres."));
    }

    @Test
    void dadosEstouLogadoComoAdminQuandoCrioNovoUsuarioSobrenomeInvalidoEntaoDeveRetornarStatus400() {

        CriarUsuarioEnvio novoUsuario = CriarUsuarioFixture.criarUsuarioSobrenomeInvalido();

        TestUtils.cadastrarUsuario(novoUsuario, tokenAdmin)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().assertThat().body("erro", equalTo("Sobrenome deve conter entre 2 e 20 caracteres."));
    }

    @Test
    void dadosEstouLogadoComoAdminQuandoCrioNovoUsuarioCpfInvalidoEntaoDeveRetornarStatus400() {

        CriarUsuarioEnvio novoUsuario = CriarUsuarioFixture.criarUsuarioCpfInvalido();

        TestUtils.cadastrarUsuario(novoUsuario, tokenAdmin)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and().assertThat().body("erro", equalTo("Cpf deve conter 11 caracteres numéricos."));
    }
}
