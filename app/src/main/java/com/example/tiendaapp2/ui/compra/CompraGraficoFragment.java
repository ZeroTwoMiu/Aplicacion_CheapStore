package com.example.tiendaapp2.ui.compra;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.tiendaapp2.Login;
import com.example.tiendaapp2.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class CompraGraficoFragment extends Fragment {

    private BarChart barChart;
    private final ArrayList<String> etiquetasMeses = new ArrayList<>();
    private final ArrayList<BarEntry> entradas = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_compra_grafico, container, false);
        barChart = v.findViewById(R.id.barChartCompras);
        cargarDatosGrafico();
        return v;
    }

    private void cargarDatosGrafico() {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = Login.servidor + "compra_grafico_mes.php";

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONArray array = new JSONArray(new String(responseBody));
                    etiquetasMeses.clear();
                    entradas.clear();

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        String mes = obj.getString("mes");
                        float total = (float) obj.getDouble("total");

                        etiquetasMeses.add(mes);
                        entradas.add(new BarEntry(i, total));
                    }

                    BarDataSet dataSet = new BarDataSet(entradas, "Compras por mes");
                    dataSet.setColors(Color.rgb(76, 175, 80));
                    dataSet.setValueTextSize(12f);

                    BarData barData = new BarData(dataSet);
                    barChart.setData(barData);

                    XAxis xAxis = barChart.getXAxis();
                    xAxis.setValueFormatter(new IndexAxisValueFormatter(etiquetasMeses));
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setGranularity(1f);
                    xAxis.setDrawGridLines(false);
                    xAxis.setLabelRotationAngle(-45);

                    barChart.getAxisRight().setEnabled(false);
                    barChart.getDescription().setEnabled(false);
                    barChart.animateY(1000);
                    barChart.invalidate();

                } catch (Exception e) {
                    Log.e("COMPRA_GRAFICO", "Error parseando JSON", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("COMPRA_GRAFICO", "Error de conexión", error);
            }
        });
    }
}
