package com.dbserver.restassured.fixture;


import com.dbserver.restassured.models.sessaoVotacao.VotoInternoDados;
import com.dbserver.restassured.models.sessaoVotacao.enums.TipoDeVotoEnum;




public class VotoInternoFixture {

    public static VotoInternoDados dadosVotoInternoPositivoValidos(Integer pautaid) {
        return VotoInternoDados.builder()
                .pautaId(pautaid)
                .tipoDeVoto(TipoDeVotoEnum.VOTO_POSITIVO)
                .build();
    }
    public static VotoInternoDados dadosVotoInternoTipoVotoNulo(Integer pautaid) {
        return VotoInternoDados.builder()
                .pautaId(pautaid)
                .tipoDeVoto(null)
                .build();
    }
}
