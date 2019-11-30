package com.prototype.dynamicfeature2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.splitcompat.SplitCompat;
import com.prototype.dynamicfeature2.utils.NotaFiscal;
import com.prototype.dynamicfeature2.utils.NotaFiscalAdapter;
import com.prototype.dynamicfeature2.utils.NotaFiscalController;
import com.prototype.dynamicfeature2.utils.RestService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity extends AppCompatActivity {

    private static final int REQUEST_CODE = 0;
    private ImageButton imageButton;
    private ListView listaNotas;
    private Toolbar toolbar;

    private NotaFiscalAdapter adapter;
    private NotaFiscalController controller;
    private RestService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageButton = findViewById(R.id.imageButton);
        listaNotas = findViewById(R.id.list_notas);
        service = new RestService();

        controller = new NotaFiscalController
                (this, this, service, findViewById(R.id.activity_notas));
        inflateList();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.createDialog(null);
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSecondActivity();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void inflateList() {
        Call<List<NotaFiscal>> call = service.getService().getAll();

        call.enqueue(new Callback<List<NotaFiscal>>() {
            @Override
            public void onResponse(Call<List<NotaFiscal>> call,
                                   Response<List<NotaFiscal>> response) {
                if (response.isSuccessful()) {
                    createAdapter(response);
                }
            }

            @Override
            public void onFailure(Call<List<NotaFiscal>> call, Throwable t) {
                Snackbar.make
                        (findViewById(R.id.activity_notas),
                                com.prototype.appbundle.R.string.snack_error_rest,
                                Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        SplitCompat.installActivity(this);
    }

    private void createAdapter(Response<List<NotaFiscal>> response) {
        List<NotaFiscal> notas = response.body();

        if (adapter == null) {
            adapter = new NotaFiscalAdapter
                    (this, findViewById(R.id.activity_notas), notas, controller);

            listaNotas.setAdapter(adapter);
        } else {
            adapter.updateList(notas);
            adapter.notifyDataSetChanged();
        }
    }

    private void openSecondActivity() {
        Intent i = new Intent(this, Scanner.class);
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            controller.importNotaFiscal
                    ((NotaFiscal) data.getSerializableExtra("nota_fiscal"));
        } else {
            Snackbar.make(findViewById(R.id.activity_notas),
                    com.prototype.appbundle.R.string.snack_scanner_error,
                    Snackbar.LENGTH_LONG).show();
        }
    }

}
