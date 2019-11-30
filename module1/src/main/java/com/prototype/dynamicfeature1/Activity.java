package com.prototype.dynamicfeature1;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.splitcompat.SplitCompat;
import com.prototype.dynamicfeature1.utils.Pessoa;
import com.prototype.dynamicfeature1.utils.PessoaAdapter;
import com.prototype.dynamicfeature1.utils.PessoaController;
import com.prototype.dynamicfeature1.utils.RestService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity extends AppCompatActivity {

    private ListView listaPessoas;
    private Toolbar toolbar;

    private PessoaAdapter adapter;
    private PessoaController controller;
    private RestService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pessoas);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listaPessoas = findViewById(R.id.list_pessoas);
        service = new RestService();

        controller = new PessoaController
                (this, this, service, findViewById(R.id.activity_pessoas));
        inflateList();

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.createDialog(null);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        inflateList();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        SplitCompat.installActivity(this);
    }

    public void inflateList() {
        Call<List<Pessoa>> call = service.getService().getAll();

        call.enqueue(new Callback<List<Pessoa>>() {
            @Override
            public void onResponse(Call<List<Pessoa>> call, Response<List<Pessoa>> response) {
                if (response.isSuccessful()) {
                    createAdapter(response);
                }
            }

            @Override
            public void onFailure(Call<List<Pessoa>> call, Throwable t) {
                Snackbar.make
                        (findViewById(R.id.activity_pessoas),
                                com.prototype.appbundle.R.string.snack_error_rest,
                                Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void createAdapter(Response<List<Pessoa>> response) {
        List<Pessoa> pessoas = response.body();

        if (adapter == null) {
            adapter = new PessoaAdapter
                    (this, findViewById(R.id.activity_pessoas), pessoas, controller);

            listaPessoas.setAdapter(adapter);
        } else {
            adapter.updateList(pessoas);
            adapter.notifyDataSetChanged();
        }
    }

}
