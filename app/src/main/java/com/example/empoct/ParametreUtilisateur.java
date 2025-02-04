package com.example.empoct;

import android.app.AlertDialog;
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

/**
 * Fragment pour la section Paramètres de l'application.
 */
public class ParametreUtilisateur extends Fragment {

    public ParametreUtilisateur() {
        // Constructeur public vide requis
    }

    public static ParametreUtilisateur newInstance(String param1, String param2) {
        ParametreUtilisateur fragment = new ParametreUtilisateur();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString("param1");
            String mParam2 = getArguments().getString("param2");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_parametre_utilisateur, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Références aux éléments du layout
        TextView profile = view.findViewById(R.id.profile);
        TextView measurementPlanning = view.findViewById(R.id.measurement_planning);
        TextView about = view.findViewById(R.id.about);
        TextView logout = view.findViewById(R.id.logout);
        Button backButton = view.findViewById(R.id.back_button);

        // Gestion des clics pour chaque élément
        profile.setOnClickListener(v -> {
            Profil profilFragment = new Profil();
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,  // Animation pour le fragment entrant
                            R.anim.slide_out_left, // Animation pour le fragment sortant
                            R.anim.slide_in_left,  // Animation pour le fragment entrant (retour)
                            R.anim.slide_out_right // Animation pour le fragment sortant (retour)
                    )
                    .replace(R.id.main, profilFragment)
                    .addToBackStack(null)
                    .commit();
        });

        measurementPlanning.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,  // Animation pour le fragment entrant
                            R.anim.slide_out_left, // Animation pour le fragment sortant
                            R.anim.slide_in_left,  // Animation pour le fragment entrant (retour)
                            R.anim.slide_out_right // Animation pour le fragment sortant (retour)
                    )
                    .replace(R.id.main, new PlanningDeMesure())
                    .addToBackStack(null)
                    .commit();
        });

        about.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,  // Animation pour le fragment entrant
                            R.anim.slide_out_left, // Animation pour le fragment sortant
                            R.anim.slide_in_left,  // Animation pour le fragment entrant (retour)
                            R.anim.slide_out_right // Animation pour le fragment sortant (retour)
                    )
                    .replace(R.id.main, new APropos())
                    .addToBackStack(null)
                    .commit();
        });

        // Affichage de la boîte de dialogue de déconnexion
        logout.setOnClickListener(v -> showLogoutDialog());

        // Gestion du clic sur le bouton "Retour"
        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    // Méthode pour afficher la boîte de dialogue de déconnexion
    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Déconnexion");
        builder.setMessage("Êtes-vous sûr de vouloir vous déconnecter de l'application ?");

        // Option "Oui" → Redirige vers la page de connexion
        builder.setPositiveButton("Oui", (dialog, which) -> {
            Toast.makeText(getContext(), "Déconnexion réussie", Toast.LENGTH_SHORT).show();

            // Redirige vers la page de connexion
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_left,  // Animation pour le fragment entrant (retour)
                            R.anim.slide_out_right, // Animation pour le fragment sortant (retour)
                            R.anim.slide_in_right,  // Animation pour le fragment entrant
                            R.anim.slide_out_left // Animation pour le fragment sortant
                    )
                    .replace(R.id.main, new AuthentificationFragment()) // Remplace par la page de connexion
                    .commit();
        });

        // Option "Non" → Ferme la boîte de dialogue et reste sur la page "Paramètres"
        builder.setNegativeButton("Non", (dialog, which) -> dialog.dismiss());

        // Affiche la boîte de dialogue
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}