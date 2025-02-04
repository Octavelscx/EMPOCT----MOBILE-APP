package com.example.empoct;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class InscriptionFragment extends Fragment {

    private EditText firstNameEditText, lastNameEditText, usernameEditText, passwordEditText;
    private EditText securityQuestion1EditText, securityQuestion2EditText, securityQuestion3EditText;
    private Button registerButton;

    private static final String FILE_NAME = "users.json";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inscription, container, false);

        // Initialiser les vues
        firstNameEditText = view.findViewById(R.id.firstNameEditText);
        lastNameEditText = view.findViewById(R.id.lastNameEditText);
        usernameEditText = view.findViewById(R.id.usernameEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        securityQuestion1EditText = view.findViewById(R.id.securityQuestion1EditText);
        securityQuestion2EditText = view.findViewById(R.id.securityQuestion2EditText);
        securityQuestion3EditText = view.findViewById(R.id.securityQuestion3EditText);
        registerButton = view.findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> registerUser());

        return view;
    }

    private void registerUser() {
        // Récupérer les valeurs des champs
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String securityQuestion1 = securityQuestion1EditText.getText().toString().trim();
        String securityQuestion2 = securityQuestion2EditText.getText().toString().trim();
        String securityQuestion3 = securityQuestion3EditText.getText().toString().trim();

        // Vérification si tous les champs sont remplis
        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()
                || securityQuestion1.isEmpty() || securityQuestion2.isEmpty() || securityQuestion3.isEmpty()) {
            Toast.makeText(getActivity(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Lire les utilisateurs existants
            JSONArray usersArray = readUsersFromFile();

            // Vérifier si l'utilisateur existe déjà
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject user = usersArray.getJSONObject(i);
                if (user.getString("username").equals(username)) {
                    Toast.makeText(getActivity(), "Cet identifiant existe déjà", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Ajouter le nouvel utilisateur
            JSONObject newUser = new JSONObject();
            newUser.put("firstName", firstName);
            newUser.put("lastName", lastName);
            newUser.put("username", username);
            newUser.put("password", password);

            // Ajouter les réponses aux questions de sécurité
            newUser.put("securityQuestion1", securityQuestion1);
            newUser.put("securityQuestion2", securityQuestion2);
            newUser.put("securityQuestion3", securityQuestion3);

            // Ajouter l'utilisateur au tableau des utilisateurs
            usersArray.put(newUser);

            // Enregistrer les utilisateurs dans le fichier JSON
            saveUsersToFile(usersArray);

            // Afficher un message de succès
            Toast.makeText(getActivity(), "Inscription réussie !", Toast.LENGTH_SHORT).show();
            AuthentificationFragment authentificationFragment = new AuthentificationFragment();
            getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_right,  // Animation pour le fragment entrant
                            R.anim.slide_out_left, // Animation pour le fragment sortant
                            R.anim.slide_in_left,  // Animation pour le fragment entrant (retour)
                            R.anim.slide_out_right // Animation pour le fragment sortant (retour)
                    )
                    .replace(R.id.main, authentificationFragment) // Remplace Tuto1 par Tuto2
                    .addToBackStack(null) // Ajoute Tuto1 dans la pile pour pouvoir revenir
                    .commit();

            // Réinitialiser les champs
            resetFields();

        } catch (JSONException | IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Erreur lors de l'inscription", Toast.LENGTH_SHORT).show();
            Log.e("Inscription", "Erreur lors de l'inscription: " + e.getMessage());
        }
    }

    // Fonction pour réinitialiser les champs de saisie après une inscription réussie
    private void resetFields() {
        firstNameEditText.setText("");
        lastNameEditText.setText("");
        usernameEditText.setText("");
        passwordEditText.setText("");
        securityQuestion1EditText.setText("");
        securityQuestion2EditText.setText("");
        securityQuestion3EditText.setText("");
    }

    // Fonction pour lire les utilisateurs à partir du fichier JSON
    private JSONArray readUsersFromFile() throws IOException, JSONException {
        try {
            FileInputStream fis = getActivity().openFileInput(FILE_NAME);
            byte[] data = new byte[fis.available()];
            fis.read(data);
            fis.close();

            String json = new String(data);
            return new JSONArray(json);
        } catch (IOException | JSONException e) {
            // Si le fichier n'existe pas ou une erreur se produit, on retourne un tableau vide
            Log.d("Inscription", "Fichier non trouvé ou erreur de lecture. Création d'un tableau vide.");
            return new JSONArray();
        }
    }

    // Fonction pour enregistrer les utilisateurs dans le fichier JSON
    private void saveUsersToFile(JSONArray usersArray) throws IOException {
        FileOutputStream fos = getActivity().openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
        fos.write(usersArray.toString().getBytes());
        fos.close();
        Log.d("Inscription", "Utilisateurs enregistrés dans le fichier JSON.");
    }
}
