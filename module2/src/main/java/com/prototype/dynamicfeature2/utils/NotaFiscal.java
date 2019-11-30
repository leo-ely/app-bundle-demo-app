package com.prototype.dynamicfeature2.utils;

import java.io.Serializable;

public class NotaFiscal implements Serializable {

    public int Id;
    public int NumeroNota;
    public int SerieNota;
    public String ChaveNota;
    public String DataEmissao;
    public float ValorNota;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getNumeroNota() {
        return NumeroNota;
    }

    public void setNumeroNota(int numeroNota) {
        NumeroNota = numeroNota;
    }

    public int getSerieNota() {
        return SerieNota;
    }

    public void setSerieNota(int serieNota) {
        SerieNota = serieNota;
    }

    public String getChaveNota() {
        return ChaveNota;
    }

    public void setChaveNota(String chaveNota) {
        ChaveNota = chaveNota;
    }

    public String getDataEmissao() {
        return DataEmissao;
    }

    public void setDataEmissao(String dataEmissao) {
        DataEmissao = dataEmissao;
    }

    public float getValorNota() {
        return ValorNota;
    }

    public void setValorNota(float valorNota) {
        ValorNota = valorNota;
    }

}
