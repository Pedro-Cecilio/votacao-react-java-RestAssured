package com.dbserver.restassured.fixture;

import java.util.Locale;

import com.dbserver.restassured.models.sessaoVotacao.VotoExternoDados;
import com.dbserver.restassured.models.sessaoVotacao.enums.TipoDeVotoEnum;


import net.datafaker.Faker;


public class VotoExternoFixture {
    private static final Faker faker = new Faker(new Locale("pt-BR"));

    public static VotoExternoDados dadosVotoExternoUsuarioExistenteValido(Integer pautaid) {
        return VotoExternoDados.builder()
                .pautaId(pautaid)
                .cpf("12345678912")
                .senha("testes123")
                .tipoDeVoto(TipoDeVotoEnum.VOTO_POSITIVO)
                .build();
    }
    public static VotoExternoDados dadosVotoExternoUsuarioExistenteSemSenha(Integer pautaid) {
        return VotoExternoDados.builder()
                .pautaId(pautaid)
                .cpf("12345678912")
                .tipoDeVoto(TipoDeVotoEnum.VOTO_POSITIVO)
                .build();
    }
    public static VotoExternoDados dadosVotoExternoUsuarioNaoCadastradoValido(Integer pautaid) {
        return VotoExternoDados.builder()
                .pautaId(pautaid)
                .tipoDeVoto(TipoDeVotoEnum.VOTO_NEGATIVO)
                .cpf(faker.number().digits(11))
                .build();
    }
    public static VotoExternoDados dadosVotoExternoUsuarioNaoCadastradoCpfInvalido(Integer pautaid) {
        return VotoExternoDados.builder()
                .pautaId(pautaid)
                .tipoDeVoto(TipoDeVotoEnum.VOTO_NEGATIVO)
                .cpf(faker.number().digits(12))
                .build();
    }
}
