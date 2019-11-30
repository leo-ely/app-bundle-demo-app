package com.prototype.dynamicfeature1.utils;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;
import com.prototype.dynamicfeature1.Activity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PessoaController {

    private Activity activity;
    private AlertDialog.Builder dialog;
    private Context context;
    private RestService service;
    private View view;

    private EditText nome;
    private EditText cpf;
    private EditText telefone;
    private EditText email;

    public PessoaController(Activity activity, Context context, RestService service, View view) {
        this.activity = activity;
        this.context = context;
        this.service = service;
        this.view = view;
    }

    public void createDialog(Pessoa p) {
        dialog = new AlertDialog.Builder(context);
        dialog.setMessage(com.prototype.appbundle.R.string.required);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);

        nome = new EditText(context);
        nome.setHint(com.prototype.appbundle.R.string.hint_nome);
        nome.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});

        cpf = new EditText(context);
        cpf.setHint(com.prototype.appbundle.R.string.hint_cpf);
        cpf.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        cpf.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});

        telefone = new EditText(context);
        telefone.setHint(com.prototype.appbundle.R.string.hint_telefone);
        telefone.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        telefone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});

        email = new EditText(context);
        email.setHint(com.prototype.appbundle.R.string.hint_email);
        email.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});

        if (p != null) {
            nome.setText(p.getNome());
            cpf.setText(p.getCpf());
            telefone.setText(p.getTelefone() != null ? p.getTelefone() : null);
            email.setText(p.getEmail() != null ? p.getEmail() : null);
        }

        nome.setLayoutParams(new LinearLayout.LayoutParams(600, 120));
        cpf.setLayoutParams(new LinearLayout.LayoutParams(600, 120));
        telefone.setLayoutParams(new LinearLayout.LayoutParams(600, 120));
        email.setLayoutParams(new LinearLayout.LayoutParams(600, 120));

        layout.addView(nome);
        layout.addView(cpf);
        layout.addView(telefone);
        layout.addView(email);

        dialog.setView(layout);

        dialog.setPositiveButton(com.prototype.appbundle.R.string.save, (dialog, which) -> {
            save(p);
        });

        dialog.setNegativeButton(com.prototype.appbundle.R.string.cancel, null);

        dialog.create().show();
    }

    private void save(Pessoa p) {
        if (p == null) {
            if (validateFields()) {
                Snackbar.make(view,
                        com.prototype.appbundle.R.string.snack_error_fields,
                        Snackbar.LENGTH_LONG).show();
            } else {
                Pessoa pessoa = new Pessoa();
                pessoa.setNome(nome.getText().toString());
                pessoa.setCpf(cpf.getText().toString());

                if (!telefone.getText().toString().isEmpty() ||
                        !telefone.getText().toString().equals("")) {
                    pessoa.setTelefone(telefone.getText().toString());
                }

                if (!email.getText().toString().isEmpty() ||
                        !email.getText().toString().equals("") ||
                        !email.getText().toString().contains("@")) {
                    pessoa.setEmail(email.getText().toString());
                }

                service.getService().addPessoa(pessoa).enqueue(new Callback<Pessoa>() {
                    @Override
                    public void onResponse(Call<Pessoa> call, Response<Pessoa> response) {
                        if (response.isSuccessful()) {
                            Snackbar.make(view,
                                    com.prototype.appbundle.R.string.snack_saved,
                                    Snackbar.LENGTH_LONG).show();

                            activity.inflateList();
                        }
                    }

                    @Override
                    public void onFailure(Call<Pessoa> call, Throwable t) {
                        Snackbar.make(view,
                                com.prototype.appbundle.R.string.snack_save_error,
                                Snackbar.LENGTH_LONG).show();
                        t.printStackTrace();
                    }
                });
            }
        } else {
            if (validateFields()) {
                Snackbar.make(view,
                        com.prototype.appbundle.R.string.snack_error_fields,
                        Snackbar.LENGTH_LONG).show();
            } else {
                p.setNome(nome.getText().toString());
                p.setCpf(cpf.getText().toString());

                if (!telefone.getText().toString().isEmpty() ||
                        !telefone.getText().toString().equals("")) {
                    p.setTelefone(telefone.getText().toString());
                }

                if (!email.getText().toString().isEmpty() ||
                        !email.getText().toString().equals("")) {
                    p.setEmail(email.getText().toString());
                }

                service.getService().updatePessoa(p.getId(), p).enqueue(new Callback<Pessoa>() {
                    @Override
                    public void onResponse(Call<Pessoa> call, Response<Pessoa> response) {
                        if (response.isSuccessful()) {
                            Snackbar.make(view,
                                    com.prototype.appbundle.R.string.snack_saved,
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Pessoa> call, Throwable t) {
                        Snackbar.make(view,
                                com.prototype.appbundle.R.string.snack_save_error,
                                Snackbar.LENGTH_LONG).show();
                        t.printStackTrace();
                    }
                });
            }
        }
    }

    public void delete(Integer id) {
        service.getService().deletePessoa(id).enqueue(new Callback<Pessoa>() {
            @Override
            public void onResponse(Call<Pessoa> call, Response<Pessoa> response) {
                if (response.isSuccessful()) {
                    Snackbar.make(view,
                            com.prototype.appbundle.R.string.snack_deleted,
                            Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Pessoa> call, Throwable t) {
                Snackbar.make(view,
                        com.prototype.appbundle.R.string.snack_delete_error,
                        Snackbar.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private boolean validateFields() {
        return nome.getText() == null || cpf.getText() == null ||
                nome.getText().toString().isEmpty() || cpf.getText().toString().isEmpty() ||
                nome.getText().toString().equals("") || cpf.getText().toString().equals("");
    }

}
