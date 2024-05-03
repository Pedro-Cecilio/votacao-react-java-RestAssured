package com.dbserver.restassured.fixture;

import com.dbserver.restassured.models.sessaoVotacao.AbrirSessaoVotacaoDados;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AbrirSessaoVotacaoFixture {
    private static final long MINUTOS = 2L;
    private static final Random random = new Random();

    public static AbrirSessaoVotacaoDados abrirSessaoVotacaoCorretamente(int pautaid) {
        return AbrirSessaoVotacaoDados.builder()
                .minutos(MINUTOS)
                .pautaId(pautaid)
                .build();
    }
    public static AbrirSessaoVotacaoDados abrirSessaoVotacaoMinutosInvalido(int pautaid) {
        return AbrirSessaoVotacaoDados.builder()
                .minutos(0L)
                .pautaId(pautaid)
                .build();
    }
    public static AbrirSessaoVotacaoDados abrirSessaoVotacaoPautaInexistente() {
        int pautaIdInexistente = random.nextInt(Integer.MAX_VALUE - 100000) + 100001;
        return AbrirSessaoVotacaoDados.builder()
                .minutos(MINUTOS)
                .pautaId(pautaIdInexistente)
                .build();
    }
}
