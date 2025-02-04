package com.example.empoct;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;

public class WelcomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Associer le fragment au fichier XML
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        // Récupérer les éléments de la vue
        ImageView imageView = view.findViewById(R.id.imageView);
        TextView infoTextView1 = view.findViewById(R.id.infoTextView1);
        Button button6 = view.findViewById(R.id.button6);
        Button button7 = view.findViewById(R.id.button7);

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Naviguer ou afficher un message lors du clic sur "Suivant"
                Toast.makeText(getActivity(), "Démarrer", Toast.LENGTH_SHORT).show();
                // Passer au fragment Tuto1
                Tuto1 tuto1 = new Tuto1();
                getParentFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in_right,  // Animation pour le fragment entrant
                                R.anim.slide_out_left, // Animation pour le fragment sortant
                                R.anim.slide_in_left,  // Animation pour le fragment entrant (retour)
                                R.anim.slide_out_right // Animation pour le fragment sortant (retour)
                        )
                        .replace(R.id.main, tuto1) // Remplace Tuto1 par Tuto2
                        .addToBackStack(null) // Ajoute Tuto1 dans la pile pour pouvoir revenir
                        .commit();
                // Exemple de navigation vers un autre fragment (si nécessaire)
                // getParentFragmentManager().beginTransaction()
                //     .replace(R.id.fragment_container, new NextFragment())
                //     .addToBackStack(null)
                //     .commit();
            }
        });

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

        return view;
    }
}
