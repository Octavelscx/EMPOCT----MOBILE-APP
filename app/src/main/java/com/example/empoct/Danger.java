package com.example.empoct;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Danger extends Fragment {

    public Danger() {
        // Constructeur vide requis
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_danger, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button backButton = view.findViewById(R.id.backButton);

        // Gestion du bouton "Retour"
        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed(); // Retour à la page précédente
            }
        });
    }
}