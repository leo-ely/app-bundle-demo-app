package com.prototype.dynamicfeature2.utils;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;
import com.prototype.dynamicfeature2.Activity;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotaFiscalController {

    private Activity activity;
    private AlertDialog.Builder dialog;
    private Context context;
    private RestService service;
    private View view;

    private EditText numero;
    private EditText serie;
    private EditText chave;
    private EditText data_emissao;
    private EditText valor_nota;

    public NotaFiscalController(Activity activity,
                                Context context,
                                RestService service,
                                View view) {
        this.activity = activity;
        this.context = context;
        this.service = service;
        this.view = view;
    }

    public void createDialog(NotaFiscal nf) {
        dialog = new AlertDialog.Builder(context);
        dialog.setMessage(com.prototype.appbundle.R.string.required);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);

        numero = new EditText(context);
        numero.setHint(com.prototype.appbundle.R.string.hint_numero);
        numero.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        numero.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

        serie = new EditText(context);
        serie.setHint(com.prototype.appbundle.R.string.hint_serie);
        serie.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        serie.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});

        chave = new EditText(context);
        chave.setHint(com.prototype.appbundle.R.string.hint_chave);
        chave.setRawInputType(InputType.TYPE_CLASS_NUMBER);

        data_emissao = new EditText(context);
        data_emissao.setHint(com.prototype.appbundle.R.string.hint_data_emissao);
        data_emissao.setRawInputType(InputType.TYPE_CLASS_DATETIME |
                InputType.TYPE_DATETIME_VARIATION_DATE);
        data_emissao.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

        valor_nota = new EditText(context);
        valor_nota.setHint(com.prototype.appbundle.R.string.hint_valor_nota);
        valor_nota.setRawInputType(InputType.TYPE_CLASS_NUMBER
                | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        valor_nota.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

        if (nf != null) {
            numero.setText(String.valueOf(nf.getNumeroNota()));
            serie.setText(String.valueOf(nf.getSerieNota()));
            chave.setText(nf.getChaveNota());
            data_emissao.setText(decodeDate(nf.getDataEmissao()));
            valor_nota.setText(String.valueOf(nf.getValorNota()));
        }

        numero.setLayoutParams(new LinearLayout.LayoutParams(600, 120));
        serie.setLayoutParams(new LinearLayout.LayoutParams(600, 120));
        chave.setLayoutParams(new LinearLayout.LayoutParams(600, 120));
        data_emissao.setLayoutParams(new LinearLayout.LayoutParams(600, 120));
        valor_nota.setLayoutParams(new LinearLayout.LayoutParams(600, 120));

        layout.addView(numero);
        layout.addView(serie);
        layout.addView(chave);
        layout.addView(data_emissao);
        layout.addView(valor_nota);

        dialog.setView(layout);

        dialog.setPositiveButton(com.prototype.appbundle.R.string.save, (dialog, which) -> {
            save(nf);
        });

        dialog.setNegativeButton(com.prototype.appbundle.R.string.cancel, null);

        dialog.create().show();
    }

    private void save(NotaFiscal nf) {
        if (nf == null) {
            if (validateFields()) {
                Snackbar.make(view,
                        com.prototype.appbundle.R.string.snack_error_fields,
                        Snackbar.LENGTH_LONG).show();
            } else {
                NotaFiscal nota = new NotaFiscal();

                try {
                    nota.setNumeroNota(Integer.valueOf(numero.getText().toString()));
                    nota.setSerieNota(Integer.valueOf(serie.getText().toString()));
                    nota.setChaveNota(chave.getText().toString());
                    nota.setDataEmissao(parseDate(data_emissao.getText().toString()));
                    nota.setValorNota(Float.valueOf(valor_nota.getText().toString()));
                } catch (NumberFormatException e) {
                    Snackbar.make(view,
                            com.prototype.appbundle.R.string.snack_error_fields,
                            Snackbar.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                service.getService().addNota(nota).enqueue(new Callback<NotaFiscal>() {
                    @Override
                    public void onResponse(Call<NotaFiscal> call, Response<NotaFiscal> response) {
                        if (response.isSuccessful()) {
                            Snackbar.make(view,
                                    com.prototype.appbundle.R.string.snack_saved,
                                    Snackbar.LENGTH_LONG).show();

                            activity.inflateList();
                        }
                    }

                    @Override
                    public void onFailure(Call<NotaFiscal> call, Throwable t) {
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
                try {
                    nf.setNumeroNota(Integer.valueOf(numero.getText().toString()));
                    nf.setSerieNota(Integer.valueOf(serie.getText().toString()));
                    nf.setChaveNota(chave.getText().toString());
                    nf.setDataEmissao(parseDate(data_emissao.getText().toString()));
                    nf.setValorNota(Float.valueOf(valor_nota.getText().toString()));
                } catch (NumberFormatException e) {
                    Snackbar.make(view,
                            com.prototype.appbundle.R.string.snack_error_fields,
                            Snackbar.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                service.getService().updateNota(nf.getId(), nf).enqueue(new Callback<NotaFiscal>() {
                    @Override
                    public void onResponse(Call<NotaFiscal> call, Response<NotaFiscal> response) {
                        if (response.isSuccessful()) {
                            Snackbar.make(view,
                                    com.prototype.appbundle.R.string.snack_saved,
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<NotaFiscal> call, Throwable t) {
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
        service.getService().deleteNota(id).enqueue(new Callback<NotaFiscal>() {
            @Override
            public void onResponse(Call<NotaFiscal> call, Response<NotaFiscal> response) {
                if (response.isSuccessful()) {
                    Snackbar.make(view,
                            com.prototype.appbundle.R.string.snack_deleted,
                            Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<NotaFiscal> call, Throwable t) {
                Snackbar.make(view,
                        com.prototype.appbundle.R.string.snack_delete_error,
                        Snackbar.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    public void importNotaFiscal(NotaFiscal nota) {
        nota.setDataEmissao(parseDate(nota.getDataEmissao()));

        service.getService().addNota(nota).enqueue(new Callback<NotaFiscal>() {
            @Override
            public void onResponse(Call<NotaFiscal> call, Response<NotaFiscal> response) {
                if (response.isSuccessful()) {
                    Snackbar.make(view,
                            com.prototype.appbundle.R.string.snack_saved,
                            Snackbar.LENGTH_LONG).show();

                    activity.inflateList();
                }
            }

            @Override
            public void onFailure(Call<NotaFiscal> call, Throwable t) {
                Snackbar.make(view,
                        com.prototype.appbundle.R.string.snack_error_import_nota,
                        Snackbar.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private String parseDate(String date) {
        List<String> dateSplit = Arrays.asList(date.split("/"));
        DateTime d = new DateTime(Integer.parseInt(dateSplit.get(2)),
                Integer.parseInt(dateSplit.get(1)),
                Integer.parseInt(dateSplit.get(0)),
                0, 0, 0);

        return d.toString(ISODateTimeFormat.dateTime());
    }

    private String decodeDate(String date) {
        return DateTime.parse(date).toString("dd/MM/yyyy");
    }

    private boolean validateFields() {
        return numero.getText() == null ||
                serie.getText() == null ||
                chave.getText() == null ||
                data_emissao.getText() == null ||
                valor_nota.getText() == null ||
                numero.getText().toString().isEmpty() ||
                serie.getText().toString().isEmpty() ||
                chave.getText().toString().isEmpty() ||
                data_emissao.getText().toString().isEmpty() ||
                valor_nota.getText().toString().isEmpty() ||
                numero.getText().equals("") ||
                serie.getText().equals("") ||
                chave.getText().equals("") ||
                data_emissao.getText().equals("") ||
                valor_nota.getText().equals("") ||
                data_emissao.getText().toString().length() != 10 ||
                !data_emissao.getText().toString().contains("/");
    }

}
