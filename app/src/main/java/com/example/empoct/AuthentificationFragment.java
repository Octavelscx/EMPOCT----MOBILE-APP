package com.example.empoct;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;

import androidx.activity.OnBackPressedCallback;

public class AuthentificationFragment extends Fragment {

    // Déclaration des variables d'interface utilisateur
    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private TextView forgotPasswordTextView;

    private static final String FILE_NAME = "users.json"; // Nom du fichier contenant les utilisateurs

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authentification, container, false);

        // Initialisation des vues
        usernameEditText = view.findViewById(R.id.usernameEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        loginButton = view.findViewById(R.id.loginButton);
        forgotPasswordTextView = view.findViewById(R.id.forgotPasswordTextView);

        // Gestion de la connexion
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getActivity(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            } else {
                authenticateUser(username, password);
            }
        });

        // Redirection vers la page de récupération du mot de passe
        forgotPasswordTextView.setOnClickListener(v -> navigateToForgotPasswordFragment());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Désactiver le bouton retour sur ce fragment
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        // Ne rien faire pour désactiver le bouton retour
                    }
                }
        );
    }

    // Méthode pour authentifier un utilisateur
    private void authenticateUser(String username, String password) {
        try {
            JSONArray usersArray = readUsersFromFile();

            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject user = usersArray.getJSONObject(i);

                if (user.getString("username").equals(username) &&
                        user.getString("password").equals(password)) {
                    Toast.makeText(getActivity(), "Connexion réussie !", Toast.LENGTH_SHORT).show();
                    MainPageConso mainPageConso = new MainPageConso();
                    getParentFragmentManager().beginTransaction()
                            .setCustomAnimations(
                                    R.anim.slide_in_right,  // Animation pour le fragment entrant
                                    R.anim.slide_out_left,  // Animation pour le fragment sortant
                                    R.anim.slide_in_left,   // Animation pour le fragment entrant (retour)
                                    R.anim.slide_out_right  // Animation pour le fragment sortant (retour)
                            )
                            .replace(R.id.main, mainPageConso)
                            .addToBackStack(null)
                            .commit();

                    // Ajouter la notification après l'affichage de MainPageConso
                    getParentFragmentManager().executePendingTransactions(); // S'assure que la transaction est terminée

                    getParentFragmentManager().beginTransaction()
                            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out) // Animation douce
                            .add(R.id.main, new NotificationFragment()) // Superpose la notification
                            .commit();

                    return;
                }
            }

            // Affiche un message si les identifiants sont incorrects
            Toast.makeText(getActivity(), "Identifiant ou mot de passe incorrect", Toast.LENGTH_SHORT).show();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Erreur lors de l'authentification", Toast.LENGTH_SHORT).show();
        }
    }

    // Lecture des utilisateurs depuis un fichier JSON
    private JSONArray readUsersFromFile() throws IOException, JSONException {
        FileInputStream fis = getActivity().openFileInput(FILE_NAME);
        byte[] data = new byte[fis.available()];
        fis.read(data);
        fis.close();

        String json = new String(data);
        return new JSONArray(json);
    }

    // Navigation vers la page de récupération du mot de passe
    private void navigateToForgotPasswordFragment() {
        Fragment forgotPasswordFragment = new ForgotPasswordFragment();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main, forgotPasswordFragment)
                .addToBackStack(null)
                .commit();
    }
}
