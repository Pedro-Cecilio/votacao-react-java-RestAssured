package com.dbserver.restassured.fixture;

import java.util.Locale;

import com.dbserver.restassured.models.auth.AutenticacaoDados;
import com.dbserver.restassured.models.usuario.CriarUsuarioEnvio;


import net.datafaker.Faker;

public class CriarUsuarioFixture {
    private static final Faker faker = new Faker(new Locale("pt-BR"));
    private static final String SENHA_VALIDA = "senhaTestes";

    public static CriarUsuarioEnvio criarUsuarioCorretamente() {
        AutenticacaoDados autenticacaoDados = AutenticacaoDados.builder()
                .email(faker.internet().emailAddress())
                .senha(SENHA_VALIDA)
                .build();
        return CriarUsuarioEnvio.builder()
                .autenticacaoDto(autenticacaoDados)
                .nome(faker.name().firstName())
                .sobrenome(faker.name().lastName())
                .cpf(faker.number().digits(11))
                .admin(true)
                .build();
    }

    public static CriarUsuarioEnvio criarUsuarioEmailInvalido() {
        AutenticacaoDados autenticacaoDados = AutenticacaoDados.builder()
                .email("emailInvalido")
                .senha(SENHA_VALIDA)
                .build();
        return CriarUsuarioEnvio.builder()
                .autenticacaoDto(autenticacaoDados)
                .nome(faker.name().firstName())
                .sobrenome(faker.name().lastName())
                .cpf(faker.number().digits(11))
                .admin(true)
                .build();
    }

    public static CriarUsuarioEnvio criarUsuarioSenhaInvalida() {
        AutenticacaoDados autenticacaoDados = AutenticacaoDados.builder()
                .email(faker.internet().emailAddress())
                .senha("senha")
                .build();
        return CriarUsuarioEnvio.builder()
                .autenticacaoDto(autenticacaoDados)
                .nome(faker.name().firstName())
                .sobrenome(faker.name().lastName())
                .cpf(faker.number().digits(11))
                .admin(true)
                .build();
    }

    public static CriarUsuarioEnvio criarUsuarioNomeInvalido() {
        AutenticacaoDados autenticacaoDados = AutenticacaoDados.builder()
                .email(faker.internet().emailAddress())
                .senha(SENHA_VALIDA)
                .build();
        return CriarUsuarioEnvio.builder()
                .autenticacaoDto(autenticacaoDados)
                .nome("No")
                .sobrenome(faker.name().lastName())
                .cpf(faker.number().digits(11))
                .admin(true)
                .build();
    }

    public static CriarUsuarioEnvio criarUsuarioSobrenomeInvalido() {
        AutenticacaoDados autenticacaoDados = AutenticacaoDados.builder()
                .email(faker.internet().emailAddress())
                .senha(SENHA_VALIDA)
                .build();
        return CriarUsuarioEnvio.builder()
                .autenticacaoDto(autenticacaoDados)
                .nome(faker.name().firstName())
                .sobrenome("S")
                .cpf(faker.number().digits(11))
                .admin(true)
                .build();
    }

    public static CriarUsuarioEnvio criarUsuarioCpfInvalido() {
        AutenticacaoDados autenticacaoDados = AutenticacaoDados.builder()
                .email(faker.internet().emailAddress())
                .senha(SENHA_VALIDA)
                .build();
        return CriarUsuarioEnvio.builder()
                .autenticacaoDto(autenticacaoDados)
                .nome(faker.name().firstName())
                .sobrenome(faker.name().lastName())
                .cpf(faker.number().digits(10))
                .admin(true)
                .build();
    }

}
