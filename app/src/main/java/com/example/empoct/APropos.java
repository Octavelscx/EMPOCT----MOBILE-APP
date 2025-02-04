package com.example.empoct;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class APropos extends Fragment {

    public APropos() {
        // Constructeur vide requis
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_a_propos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button guideButton = view.findViewById(R.id.guideButton);
        Button tipsButton = view.findViewById(R.id.tipsButton);
        Button dangerButton = view.findViewById(R.id.dangerButton);
        Button backButton = view.findViewById(R.id.backButton);

        // Gestion des clics sur les boutons
        guideButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,  // Animation pour le fragment entrant
                            R.anim.slide_out_left, // Animation pour le fragment sortant
                            R.anim.slide_in_left,  // Animation pour le fragment entrant (retour)
                            R.anim.slide_out_right // Animation pour le fragment sortant (retour)
                    )
                    .replace(R.id.main, new GuideDutilisation()) // Remplace par la page GuideDutilisation
                    .addToBackStack(null) // Permet de revenir en arrière
                    .commit(); });


        tipsButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,  // Animation pour le fragment entrant
                            R.anim.slide_out_left, // Animation pour le fragment sortant
                            R.anim.slide_in_left,  // Animation pour le fragment entrant (retour)
                            R.anim.slide_out_right // Animation pour le fragment sortant (retour)
                    )
                    .replace(R.id.main, new Tips()) // Remplace par la page GuideDutilisation
                    .addToBackStack(null) // Permet de revenir en arrière
                    .commit(); });

        dangerButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,  // Animation pour le fragment entrant
                            R.anim.slide_out_left, // Animation pour le fragment sortant
                            R.anim.slide_in_left,  // Animation pour le fragment entrant (retour)
                            R.anim.slide_out_right // Animation pour le fragment sortant (retour)
                    )
                    .replace(R.id.main, new Danger()) // Remplace par la page GuideDutilisation
                    .addToBackStack(null) // Permet de revenir en arrière
                    .commit(); });

        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }
}