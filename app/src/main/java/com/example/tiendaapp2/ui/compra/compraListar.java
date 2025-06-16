package com.example.tiendaapp2.ui.compra;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.tiendaapp2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class compraListar extends Fragment implements View.OnClickListener {

    ListView lista;
    FloatingActionButton nueva;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_compra_listar, container, false);

        lista = rootView.findViewById(R.id.lstCompras);
        nueva = rootView.findViewById(R.id.fabNuevaCompra);
        nueva.setOnClickListener(this);


        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fabNuevaCompra) {

        }
    }
}