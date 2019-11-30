package com.prototype.dynamicfeature2.utils;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface NotaFiscalService {

    @GET("NotasFiscais")
    Call<List<NotaFiscal>> getAll();

    @GET("NotasFiscais/{id}")
    Call<NotaFiscal> getById(@Path("id") Integer id);

    @POST("NotasFiscais")
    Call<NotaFiscal> addNota(@Body NotaFiscal nota);

    @PUT("NotasFiscais/{id}")
    Call<NotaFiscal> updateNota(@Path("id") Integer id, @Body NotaFiscal nota);

    @DELETE("NotasFiscais/{id}")
    Call<NotaFiscal> deleteNota(@Path("id") Integer id);

}
