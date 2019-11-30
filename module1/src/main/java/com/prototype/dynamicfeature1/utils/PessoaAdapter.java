package com.prototype.dynamicfeature1.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.prototype.dynamicfeature1.R;

import java.util.List;

public class PessoaAdapter extends BaseAdapter implements ListAdapter {

    private Context context;
    private PessoaController controller;
    private View view;

    private List<Pessoa> pessoas;

    public PessoaAdapter(Context context,
                         View view,
                         List<Pessoa> pessoas,
                         PessoaController controller) {
        this.context = context;
        this.view = view;
        this.controller = controller;
        this.pessoas = pessoas;
    }

    public void updateList(List<Pessoa> pessoas) {
        this.pessoas = pessoas;
    }

    @Override
    public int getCount() {
        return pessoas.size();
    }

    @Override
    public Object getItem(int i) {
        return pessoas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return pessoas.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = inflater.inflate(R.layout.pessoas_holder, null);
        }

        TextView nome = v.findViewById(R.id.pessoa_nome);
        ImageButton delete = v.findViewById(R.id.pessoa_delete);

        if (!pessoas.isEmpty()) {
            nome.setText(pessoas.get(i).getNome());

            delete.setOnClickListener(d -> {
                showAlertDeletePessoa(pessoas.get(i).getId(), i);
            });
        }

        v.setOnClickListener(click -> {
            Pessoa p = pessoas.get(i);
            controller.createDialog(p);
        });

        return v;
    }

    public void showAlertDeletePessoa(Integer id, int i) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage(com.prototype.appbundle.R.string.delete);

        dialog.setPositiveButton
                (com.prototype.appbundle.R.string.dialog_button_yes,
                        (dialogInterface, which) -> {
                            controller.delete(id);
                            pessoas.remove(i);
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
