package com.prototype.dynamicfeature2.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.prototype.dynamicfeature2.R;

import java.util.List;

public class NotaFiscalAdapter extends BaseAdapter implements ListAdapter {

    private Context context;
    private NotaFiscalController controller;
    private View view;

    private List<NotaFiscal> notas;

    public NotaFiscalAdapter(Context context,
                             View view,
                             List<NotaFiscal> notas,
                             NotaFiscalController controller) {
        this.context = context;
        this.view = view;
        this.controller = controller;
        this.notas = notas;
    }

    public void updateList(List<NotaFiscal> notas) {
        this.notas = notas;
    }

    @Override
    public int getCount() {
        return notas.size();
    }

    @Override
    public Object getItem(int i) {
        return notas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return notas.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = inflater.inflate(R.layout.notas_holder, null);
        }

        TextView nota = v.findViewById(R.id.nota_nome);
        ImageButton delete = v.findViewById(R.id.nota_delete);

        if (!notas.isEmpty()) {
            nota.setText(String.format("%s/%s",
                    notas.get(i).getNumeroNota(), notas.get(i).getSerieNota()));

            delete.setOnClickListener(d -> {
                showAlertDeleteNota(notas.get(i).getId(), i);
            });
        }

        v.setOnClickListener(click -> {
            NotaFiscal nf = notas.get(i);
            controller.createDialog(nf);
        });

        return v;
    }

    public void showAlertDeleteNota(Integer id, int i) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage(com.prototype.appbundle.R.string.delete);

        dialog.setPositiveButton
                (com.prototype.appbundle.R.string.dialog_button_yes,
                        (dialogInterface, which) -> {
                            controller.delete(id);
                            notas.remove(i);
                            notifyDataSetChanged();
                            dialogInterface.dismiss();
                        });

        dialog.setNegativeButton
                (com.prototype.appbundle.R.string.dialog_button_no,
                        (dialogInterface, which) -> {
                            dialogInterface.dismiss();
                        });

        dialog.create().show();
    }

}
