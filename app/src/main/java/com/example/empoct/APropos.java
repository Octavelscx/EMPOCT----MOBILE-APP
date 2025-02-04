package com.example.empoct;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class APropos extends Fragment {

    public APropos() {
        // Constructeur vide requis pour les fragments
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate le layout correspondant au fragment "A Propos"
        return inflater.inflate(R.layout.fragment_a_propos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Récupération des boutons depuis le layout
        Button guideButton = view.findViewById(R.id.guideButton);
        Button tipsButton = view.findViewById(R.id.tipsButton);
        Button dangerButton = view.findViewById(R.id.dangerButton);
        Button backButton = view.findViewById(R.id.backButton);

        // Gestion du clic sur le bouton "Guide d'utilisation"
        guideButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,  // Animation pour le fragment entrant
                            R.anim.slide_out_left, // Animation pour le fragment sortant
                            R.anim.slide_in_left,  // Animation pour le fragment entrant (retour)
                            R.anim.slide_out_right // Animation pour le fragment sortant (retour)
                    )
                    .replace(R.id.main, new GuideDutilisation()) // Remplace par le fragment GuideDutilisation
                    .addToBackStack(null) // Ajoute la transaction à la pile pour revenir en arrière
                    .commit();
        });

        // Gestion du clic sur le bouton "Conseils"
        tipsButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.main, new Tips()) // Remplace par le fragment Tips
                    .addToBackStack(null)
                    .commit();
        });

        // Gestion du clic sur le bouton "Dangers"
        dangerButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left,
                            R.anim.slide_in_left,
                            R.anim.slide_out_right
                    )
                    .replace(R.id.main, new Danger()) // Remplace par le fragment Danger
                    .addToBackStack(null)
                    .commit();
        });

        // Gestion du clic sur le bouton "Retour"
        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed(); // Retour à l'écran précédent
            }
        });
    }
}
