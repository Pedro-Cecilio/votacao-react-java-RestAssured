package com.dbserver.restassured.fixture;

import java.util.Locale;

import com.dbserver.restassured.models.pauta.CriarPautaDados;
import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CriarPautaFixture {
    private static final Faker faker = new Faker(new Locale("pt-BR"));
    private static final String ASSUNTO = "Você sabe programar em Java?";
    private static String[] categorias = {
            "TRANSPORTE",
            "EDUCACAO",
            "SAUDE",
            "MORADIA",
            "MEIO_AMBIENTE",
            "CULTURA_LAZER",
            "SEGURANCA",
            "EMPREGO",
            "SERVICOS_PUBLICOS",
            "ASSUNTOS_GERAIS"
    };

    public static CriarPautaDados criarPautaCorretamente() {
        return CriarPautaDados.builder()
                .assunto(ASSUNTO)
                .categoria(categorias[faker.random().nextInt(categorias.length)])
                .build();
    }

    public static CriarPautaDados criarPautaAssuntoNulo() {
        return CriarPautaDados.builder()
                .categoria(categorias[faker.random().nextInt(categorias.length)])
                .build();
    }

    public static CriarPautaDados criarPautaCategoriaNula() {
        return CriarPautaDados.builder()
                .assunto(ASSUNTO)
                .build();
    }

    public static CriarPautaDados criarPautaCategoriaInvalida() {
        return CriarPautaDados.builder()
                .assunto(ASSUNTO)
                .categoria("CATEGORIA_INVALIDA")
                .build();
    }
}
