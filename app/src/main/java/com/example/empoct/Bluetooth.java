package com.example.empoct;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Bluetooth extends Fragment {

    private static final String TAG = "BluetoothFragment";
    private static final UUID MY_UUID = UUID.randomUUID(); // UUID unique pour votre app.

    private BluetoothAdapter bluetoothAdapter;
    private ArrayList<String> deviceList = new ArrayList<>();
    private ArrayList<BluetoothDevice> devices = new ArrayList<>();

    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private InputStream inputStream;

    // Référence vers le LineChart
    private LineChart lineChart;

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() in Bluetooth fragment, reloading chart...");
        displayChart(); // On recharge le graph
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: inflating fragment_bluetooth...");
        View view = inflater.inflate(R.layout.fragment_bluetooth, container, false);

        // Récupération de tous les boutons
        Button button1 = view.findViewById(R.id.button1);
        Button button2 = view.findViewById(R.id.button2);
        Button button3 = view.findViewById(R.id.button3);
        Button button4 = view.findViewById(R.id.button4);
        Button button5 = view.findViewById(R.id.button5);
        Button buttonRefresh = view.findViewById(R.id.buttonRefresh);

        // Récupérer le chart
        lineChart = view.findViewById(R.id.chart);

        // Bouton 1
        button1.setOnClickListener(v ->
                Toast.makeText(getActivity(), "Paramètre Indisponible", Toast.LENGTH_SHORT).show()
        );

        // Bouton Refresh
        if (buttonRefresh != null) {
            buttonRefresh.setOnClickListener(v -> {
                Log.d(TAG, "buttonRefresh clicked!");
                Toast.makeText(getActivity(), "Rafraîchissement du JSON / Graph...", Toast.LENGTH_SHORT).show();
                displayChart();
            });
        } else {
            Log.e(TAG, "buttonRefresh is NULL! Check fragment_bluetooth.xml");
        }

        // Bouton 2 => retour accueil
        button2.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Retour à l'accueil", Toast.LENGTH_SHORT).show();
            MainPageConso mainPageConso = new MainPageConso();
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_left,
                            R.anim.slide_out_right,
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                    )
                    .replace(R.id.main, mainPageConso)
                    .addToBackStack(null)
                    .commit();
        });

        // Bouton 3 => “CO”
        button3.setOnClickListener(v ->
                Toast.makeText(getActivity(), "CO", Toast.LENGTH_SHORT).show()
        );

        // Bouton 4 => “Nix”
        button4.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Nix", Toast.LENGTH_SHORT).show();
            NixFragment nixFragment = new NixFragment();
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_bottom,
                            R.anim.slide_out_bottom,
                            R.anim.slide_in_bottom,
                            R.anim.slide_out_bottom
                    )
                    .replace(R.id.main, nixFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Bouton 5 => Démarrer connexion => HomeFragment
        button5.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Démarrage connexion", Toast.LENGTH_SHORT).show();
            HomeFragment homeFragment = new HomeFragment();
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.main, homeFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Afficher le graphique une première fois
        displayChart();

        return view;
    }

    /**
     * Lis le fichier measurements.json, parse (timestamp, ppb) et renvoie une liste de Measurement
     */
    private List<Measurement> loadMeasurementsFromFile() {
        List<Measurement> measurements = new ArrayList<>();
        try {
            File file = new File(requireContext().getFilesDir(), "measurements.json");
            Log.d(TAG, "loadMeasurementsFromFile: " + file.getAbsolutePath());

            if (!file.exists()) {
                Log.d(TAG, "Fichier JSON inexistant, aucune mesure.");
                return measurements; // vide
            }
            String jsonString = readFileAsString(file);
            if (jsonString.isEmpty()) {
                Log.d(TAG, "Fichier vide ou illisible.");
                return measurements; // vide
            }

            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                long timestamp = obj.optLong("timestamp", 0L);
                int ppb = obj.optInt("ppb", 0);

                // Conversion ppb => ppm
                float ppm = ppb / 1000f;
                measurements.add(new Measurement(timestamp, ppm));
            }
            Log.d(TAG, "Nb de mesures lues = " + measurements.size());

        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de la lecture/parse JSON", e);
        }
        return measurements;
    }

    /**
     * Affiche le graphique dans le lineChart
     */
    private void displayChart() {
        // Charger les mesures
        List<Measurement> measurements = loadMeasurementsFromFile();
        if (measurements.isEmpty()) {
            // S'il n'y a aucune mesure, on clear le chart
            lineChart.clear();
            lineChart.invalidate();
            return;
        }

        // Construire la liste d'Entry pour MPAndroidChart
        List<Entry> entries = new ArrayList<>();
        // On va se servir de l'index (i) comme X, et on stocke les timestamps à part
        final List<Long> timestamps = new ArrayList<>();

        for (int i = 0; i < measurements.size(); i++) {
            Measurement m = measurements.get(i);
            entries.add(new Entry(i, m.ppm));
            timestamps.add(m.timestamp);
        }

        // Créer un dataset
        LineDataSet dataSet = new LineDataSet(entries, "PPM (ppb/1000) vs Time");
        dataSet.setColor(android.graphics.Color.BLUE);
        dataSet.setCircleColor(android.graphics.Color.RED);
        dataSet.setValueTextColor(android.graphics.Color.BLACK);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(true);

        // Créer un LineData et l'associer au chart
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Formater l'axe X pour afficher la date
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f); // interval min = 1
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(-45); // Rotation pour mieux afficher les labels

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                int index = Math.round(value);
                if (index < 0 || index >= timestamps.size()) {
                    return "";
                }
                long tsSeconds = timestamps.get(index);
                long tsMillis = tsSeconds * 1000L; // Conversion secondes en millisecondes
                return formatDate(tsMillis);
            }
        });

        // Configurer le chart
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(true);
        lineChart.getAxisRight().setEnabled(false); // Désactiver l'axe droit

        // Rafraîchir l'affichage
        lineChart.invalidate();
    }

    /**
     * Formate un timestamp (ms) en date "MM-dd HH:mm" (ex: 01-30 14:25)
     */
    private String formatDate(long timestampMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
        Date date = new Date(timestampMillis);
        return sdf.format(date);
    }

    /**
     * Lecture brute du fichier
     */
    private String readFileAsString(File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        int readCount = fis.read(data);
        fis.close();
        if (readCount > 0) {
            return new String(data, 0, readCount, StandardCharsets.UTF_8);
        } else {
            return "";
        }
    }

    /**
     * Classe interne pour stocker une mesure
     */
    private static class Measurement {
        long timestamp;  // seconds
        float ppm;       // ppb/1000

        Measurement(long t, float p) {
            this.timestamp = t;
            this.ppm = p;
        }
    }
}
