package com.prototype.dynamicfeature1.utils;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PessoaService {

    @GET("Pessoas")
    Call<List<Pessoa>> getAll();

    @GET("Pessoas/{id}")
    Call<Pessoa> getById(@Path("id") Integer id);

    @POST("Pessoas")
    Call<Pessoa> addPessoa(@Body Pessoa pessoa);

    @PUT("Pessoas/{id}")
    Call<Pessoa> updatePessoa(@Path("id") Integer id, @Body Pessoa pessoa);

    @DELETE("Pessoas/{id}")
    Call<Pessoa> deletePessoa(@Path("id") Integer id);

}
