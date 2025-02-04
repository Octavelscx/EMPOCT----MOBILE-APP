package com.example.empoct;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;


public class Tuto2 extends Fragment {

    // Déclaration des vues
    private TextView infoTextView1;
    private TextView infoTextView2;
    private TextView infoTextView3;
    private TextView infoTextView4;
    private Button button7;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Charger le layout du fragment
        View rootView = inflater.inflate(R.layout.fragment_tuto2, container, false);

        // Initialisation des vues
        infoTextView1 = rootView.findViewById(R.id.infoTextView1);
        infoTextView2 = rootView.findViewById(R.id.infoTextView2);
        infoTextView3 = rootView.findViewById(R.id.infoTextView3);
        infoTextView4 = rootView.findViewById(R.id.infoTextView4);
        button7 = rootView.findViewById(R.id.button7);

        // Configurer l'action du bouton "Terminé"
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Afficher un message Toast
                Toast.makeText(getActivity(), "Tutoriel terminé !", Toast.LENGTH_SHORT).show();

                // Vérifier si le fichier users.json existe directement ici
                File file = new File(getActivity().getFilesDir(), "users.json");

                if (file.exists()) {
                    // Si le fichier existe, naviguer directement vers l'authentification
                    AuthentificationFragment authentificationFragment = new AuthentificationFragment();
                    getParentFragmentManager().beginTransaction()
                            .setCustomAnimations(
                                    R.anim.slide_in_right,  // Animation pour le fragment entrant
                                    R.anim.slide_out_left,  // Animation pour le fragment sortant
                                    R.anim.slide_in_left,   // Animation pour le fragment entrant (retour)
                                    R.anim.slide_out_right  // Animation pour le fragment sortant (retour)
                            )
                            .replace(R.id.main, authentificationFragment) // Remplacer le fragment actuel par l'authentification
                            .addToBackStack(null)  // Ajouter à la pile arrière pour pouvoir revenir
                            .commit();
                } else {
                    // Si le fichier n'existe pas, afficher la page d'inscription
                    InscriptionFragment inscriptionFragment = new InscriptionFragment();
                    getParentFragmentManager().beginTransaction()
                            .setCustomAnimations(
                                    R.anim.slide_in_right,  // Animation pour le fragment entrant
                                    R.anim.slide_out_left,  // Animation pour le fragment sortant
                                    R.anim.slide_in_left,   // Animation pour le fragment entrant (retour)
                                    R.anim.slide_out_right  // Animation pour le fragment sortant (retour)
                            )
                            .replace(R.id.main, inscriptionFragment) // Remplacer par l'inscription
                            .addToBackStack(null)  // Ajouter à la pile arrière pour pouvoir revenir
                            .commit();
                }
            }
        });


        return rootView;
    }
}
