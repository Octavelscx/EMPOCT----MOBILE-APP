package com.example.empoct;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.view.Gravity;
import android.widget.BaseAdapter;
import android.widget.GridView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;

public class MainPageConso extends Fragment {

    private static final String FILE_NAME = "smoke.json"; // Fichier où sont stockées les entrées
    private Map<String, Integer> dateEntries = new HashMap<>(); // Map pour stocker les enregistrements par date
    private GridView gridViewCalendar;
    private CalendarAdapter calendarAdapter;
    private TextView monthYearTextView;
    private int currentMonth, currentYear;

    public MainPageConso() {
        // Constructeur public requis
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Liaison avec le fichier XML
        View rootView = inflater.inflate(R.layout.fragment_main_page_conso, container, false);

        // Initialisation des éléments du layout
        Button button1 = rootView.findViewById(R.id.button1); // Paramètre Indisponible
        Button button2 = rootView.findViewById(R.id.button2); // Retour à l'accueil
        Button button3 = rootView.findViewById(R.id.button3); // Suivant
        Button button4 = rootView.findViewById(R.id.button4); // Nix Indisponible
        Button button5 = rootView.findViewById(R.id.button5); // Enregistrement Indisponible
        Button prevMonthButton = rootView.findViewById(R.id.prevMonthButton); // Bouton précédent
        Button nextMonthButton = rootView.findViewById(R.id.nextMonthButton); // Bouton suivant
        gridViewCalendar = rootView.findViewById(R.id.gridViewCalendar); // Le GridView
        monthYearTextView = rootView.findViewById(R.id.monthYearTextView); // Afficher le mois et l'année

        // Initialiser le mois et l'année actuels
        Calendar calendar = Calendar.getInstance();
        currentMonth = calendar.get(Calendar.MONTH);
        currentYear = calendar.get(Calendar.YEAR);

        // Charger les enregistrements à partir du fichier JSON
        loadEntries();

        // Définir un adaptateur pour le GridView
        calendarAdapter = new CalendarAdapter();
        gridViewCalendar.setAdapter(calendarAdapter);

        // Afficher le mois et l'année actuels
        updateMonthYearDisplay();

        // Action sur le bouton précédent pour changer le mois
        prevMonthButton.setOnClickListener(v -> {
            currentMonth--;
            if (currentMonth < 0) {
                currentMonth = 11;
                currentYear--;
            }
            updateMonthYearDisplay();
            calendarAdapter.notifyDataSetChanged();
        });

        // Action sur le bouton suivant pour changer le mois
        nextMonthButton.setOnClickListener(v -> {
            currentMonth++;
            if (currentMonth > 11) {
                currentMonth = 0;
                currentYear++;
            }
            updateMonthYearDisplay();
            calendarAdapter.notifyDataSetChanged();
        });

        // Action sur le bouton 1 - Paramètre Indisponible
        button1.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Paramètre", Toast.LENGTH_SHORT).show();
            ParametreUtilisateur parametreUtilisateur = new ParametreUtilisateur();
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_bottom,  // Entrée depuis le bas
                            R.anim.slide_out_bottom, // Sortie vers le bas
                            R.anim.slide_in_bottom,  // Retour depuis le bas
                            R.anim.slide_out_bottom  // Retour vers le bas
                    )
                    .replace(R.id.main, parametreUtilisateur) // Remplace le fragment actuel
                    .addToBackStack(null) // Ajoute dans la pile de retour
                    .commit();
        });

        // Action sur le bouton 2 - Retour à l'accueil
        button2.setOnClickListener(v ->
                Toast.makeText(getActivity(), "Retour à l'accueil", Toast.LENGTH_SHORT).show()
        );

        // Action sur le bouton 3 - Suivant (Nouveau Fragment)
        button3.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "CO", Toast.LENGTH_SHORT).show();
            Bluetooth bluetooth = new Bluetooth();
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,  // Animation pour le fragment entrant
                            R.anim.slide_out_left,  // Animation pour le fragment sortant
                            R.anim.slide_in_left,   // Animation pour le fragment entrant (retour)
                            R.anim.slide_out_right  // Animation pour le fragment sortant (retour)
                    )
                    .replace(R.id.main, bluetooth) // Remplace Tuto1 par Tuto2
                    .addToBackStack(null) // Ajoute Tuto1 dans la pile pour pouvoir revenir
                    .commit();
        });

        // Action sur le bouton 4 - Nix Indisponible
        button4.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Nix", Toast.LENGTH_SHORT).show();
            NixFragment nixFragment = new NixFragment();
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_bottom,  // Entrée depuis le bas
                            R.anim.slide_out_bottom, // Sortie vers le bas
                            R.anim.slide_in_bottom,  // Retour depuis le bas
                            R.anim.slide_out_bottom  // Retour vers le bas
                    )
                    .replace(R.id.main, nixFragment) // Remplace le fragment actuel
                    .addToBackStack(null) // Ajoute dans la pile de retour
                    .commit();
        });


        // Action sur le bouton 5 - Enregistrement Indisponible
        button5.setOnClickListener(v -> {
            showDatePickerDialog(); // Afficher le dialogue de sélection de date
        });

        // Gestion du bouton retour
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) { // true signifie actif
                    @Override
                    public void handleOnBackPressed() {
                        // Afficher une boîte de dialogue pour confirmer le retour
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Confirmer")
                                .setMessage("Voulez-vous retourner à la page précédente ?")
                                .setPositiveButton("Retour", (dialog, which) -> {
                                    // Désactiver le callback et permettre le retour en arrière
                                    setEnabled(false);
                                    requireActivity().onBackPressed();
                                })
                                .setNegativeButton("Non", null) // Fermer la boîte de dialogue
                                .show();
                    }
                }
        );

        return rootView;
    }

    // Affiche un DatePickerDialog pour sélectionner la date
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(getActivity(),
                (view, year, month, dayOfMonth) -> showTimePickerDialog(year, month, dayOfMonth),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    // Affiche un TimePickerDialog pour sélectionner l'heure
    private void showTimePickerDialog(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        android.app.TimePickerDialog timePickerDialog = new android.app.TimePickerDialog(getActivity(),
                (view, hourOfDay, minute) -> {
                    // Formater la date et l'heure sélectionnées
                    String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                    String selectedTime = hourOfDay + ":" + minute;

                    // Ajouter l'enregistrement
                    addEntry(selectedDate, selectedTime);

                    // Sauvegarder les enregistrements dans le fichier JSON
                    saveEntries();

                    // Afficher un message de confirmation
                    Toast.makeText(getActivity(), "Enregistrement ajouté pour " + selectedDate + " à " + selectedTime, Toast.LENGTH_SHORT).show();

                    // Réactualiser le GridView après ajout de l'entrée
                    calendarAdapter.notifyDataSetChanged();
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
        timePickerDialog.show();
    }

    // Ajouter un enregistrement pour une date spécifique
    private void addEntry(String dateKey, String time) {
        // Vérifier si la date existe déjà dans la map, sinon en ajouter une nouvelle
        int currentCount = dateEntries.getOrDefault(dateKey, 0);
        dateEntries.put(dateKey, currentCount + 1);
    }

    // Sauvegarder les enregistrements dans le fichier JSON
    private void saveEntries() {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray entriesArray = new JSONArray();

            for (Map.Entry<String, Integer> entry : dateEntries.entrySet()) {
                JSONObject entryObject = new JSONObject();
                entryObject.put("date", entry.getKey());
                entryObject.put("count", entry.getValue());
                entriesArray.put(entryObject);
            }

            jsonObject.put("entries", entriesArray);

            // Sauvegarder le JSON dans un fichier
            FileOutputStream fos = getContext().openFileOutput(FILE_NAME, getContext().MODE_PRIVATE);
            fos.write(jsonObject.toString().getBytes());
            fos.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    // Charger les enregistrements depuis le fichier JSON
    private void loadEntries() {
        File file = new File(getContext().getFilesDir(), FILE_NAME);
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                int size = fis.available();
                byte[] buffer = new byte[size];
                fis.read(buffer);
                fis.close();

                String jsonString = new String(buffer, "UTF-8");
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray entriesArray = jsonObject.getJSONArray("entries");

                // Remplir la map avec les données du fichier
                for (int i = 0; i < entriesArray.length(); i++) {
                    JSONObject entry = entriesArray.getJSONObject(i);
                    String date = entry.getString("date");
                    dateEntries.put(date, entry.getInt("count"));
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // Adapter personnalisé pour afficher les jours dans un GridView
    private class CalendarAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return getDaysInMonth() + getFirstDayOffset();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView dayView;
            if (convertView == null) {
                dayView = new TextView(getActivity());
                dayView.setGravity(Gravity.CENTER);
                dayView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            } else {
                dayView = (TextView) convertView;
            }

            // Calculer le jour et appliquer la couleur
            int firstDayOffset = getFirstDayOffset();
            int dayOfMonth = position - firstDayOffset + 1;
            if (dayOfMonth > 0 && dayOfMonth <= getDaysInMonth()) {
                String dayString = String.valueOf(dayOfMonth);
                dayView.setText(dayString);

                // Vérifier le nombre d'enregistrements pour ce jour
                String date = dayOfMonth + "/" + (currentMonth + 1) + "/" + currentYear;
                int entriesCount = dateEntries.getOrDefault(date, 0);

                // Appliquer la couleur en fonction du nombre d'enregistrements
                dayView.setBackgroundColor(getDayColor(entriesCount));
            } else {
                dayView.setText("");
                dayView.setBackgroundColor(Color.TRANSPARENT);
            }

            dayView.setTextSize(24);

            return dayView;
        }

        // Retourne le nombre de jours dans le mois actuel
        private int getDaysInMonth() {
            Calendar calendar = Calendar.getInstance();
            calendar.set(currentYear, currentMonth, 1);
            return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        }

        // Retourne l'offset du premier jour du mois
        private int getFirstDayOffset() {
            Calendar calendar = Calendar.getInstance();
            calendar.set(currentYear, currentMonth, 1);
            return calendar.get(Calendar.DAY_OF_WEEK) - 1;
        }
    }

    // Met à jour le TextView affichant le mois et l'année
    private void updateMonthYearDisplay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(currentYear, currentMonth, 1);
        String monthName = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, getResources().getConfiguration().locale);
        String displayText = monthName + " " + currentYear;
        monthYearTextView.setText(displayText);
    }

    // Fonction pour obtenir la couleur en fonction du nombre d'enregistrements
    private int getDayColor(int entriesCount) {
        switch (entriesCount) {
            case 0:
                return Color.GREEN;
            case 1:
                return Color.YELLOW;
            case 2:
                return Color.rgb(255, 165, 0);  // Orange
            case 3:
                return Color.RED;
            default:
                return Color.BLACK;
        }
    }
}
