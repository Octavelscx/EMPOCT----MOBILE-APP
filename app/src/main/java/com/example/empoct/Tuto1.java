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

public class Tuto1 extends Fragment {

    // Déclaration des vues
    private TextView infoTextView1;
    private TextView infoTextView2;
    private TextView infoTextView3;
    private TextView infoTextView4;
    private ImageView imageView;
    private Button button7;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Charger le layout du fragment
        View rootView = inflater.inflate(R.layout.fragment_tuto1, container, false);

        // Initialisation des vues
        infoTextView1 = rootView.findViewById(R.id.infoTextView1);
        infoTextView2 = rootView.findViewById(R.id.infoTextView2);
        infoTextView3 = rootView.findViewById(R.id.infoTextView3);
        infoTextView4 = rootView.findViewById(R.id.infoTextView4);
        imageView = rootView.findViewById(R.id.imageView);
        button7 = rootView.findViewById(R.id.button7);

        // Configurer l'action du bouton "Suivant"
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Naviguer ou afficher un message lors du clic sur "Suivant"
                Toast.makeText(getActivity(), "Bouton Suivant cliqué", Toast.LENGTH_SHORT).show();
                // Passer au fragment Tuto2
                Tuto2 tuto2 = new Tuto2();
                getParentFragmentManager().beginTransaction()
                        .setCustomAnimations(
                                R.anim.slide_in_right,  // Animation pour le fragment entrant
                                R.anim.slide_out_left, // Animation pour le fragment sortant
                                R.anim.slide_in_left,  // Animation pour le fragment entrant (retour)
                                R.anim.slide_out_right // Animation pour le fragment sortant (retour)
                        )
                        .replace(R.id.main, tuto2) // Remplace Tuto1 par Tuto2
                        .addToBackStack(null) // Ajoute Tuto1 dans la pile pour pouvoir revenir
                        .commit();
                // Exemple de navigation vers un autre fragment (si nécessaire)
                // getParentFragmentManager().beginTransaction()
                //     .replace(R.id.fragment_container, new NextFragment())
                //     .addToBackStack(null)
                //     .commit();
            }
        });

        return rootView;
    }
}
