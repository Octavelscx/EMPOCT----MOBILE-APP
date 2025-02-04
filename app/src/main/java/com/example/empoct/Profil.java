package com.example.empoct;

import android.content.Context;
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
import java.io.FileOutputStream;
import java.io.IOException;

public class Profil extends Fragment {

    private TextView firstNameTextView, lastNameTextView;
    private EditText usernameEditText, passwordEditText;
    private EditText oldPasswordEditText, newPasswordEditText;
    private Button changeUsernameButton, changePasswordButton, saveNewPasswordButton, backButton;
    private static final String FILE_NAME = "users.json";

    public Profil() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firstNameTextView = view.findViewById(R.id.firstNameTextView);
        lastNameTextView = view.findViewById(R.id.lastNameTextView);
        usernameEditText = view.findViewById(R.id.usernameEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        oldPasswordEditText = view.findViewById(R.id.oldPasswordEditText);
        newPasswordEditText = view.findViewById(R.id.newPasswordEditText);
        changeUsernameButton = view.findViewById(R.id.changeUsernameButton);
        changePasswordButton = view.findViewById(R.id.changePasswordButton);
        saveNewPasswordButton = view.findViewById(R.id.saveNewPasswordButton);
        backButton = view.findViewById(R.id.backButton);

        loadUserProfile();
        changeUsernameButton.setOnClickListener(v -> enableUsernameEditing(true));

        changePasswordButton.setOnClickListener(v -> enablePasswordChange(true));

        saveNewPasswordButton.setOnClickListener(v -> saveNewPassword());

        backButton.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });
    }

    private void loadUserProfile() {
        try {
            JSONArray usersArray = readUsersFromFile();
            if (usersArray.length() > 0) {
                JSONObject lastUser = usersArray.getJSONObject(usersArray.length() - 1);
                firstNameTextView.setText(lastUser.optString("firstName", "Inconnu"));
                lastNameTextView.setText(lastUser.optString("lastName", "Inconnu"));
                usernameEditText.setText(lastUser.optString("username", ""));
                passwordEditText.setText("**********");
            } else {
                Toast.makeText(getActivity(), "Aucun utilisateur enregistré", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Erreur de chargement du profil", Toast.LENGTH_SHORT).show();
        }
    }

    private void enableUsernameEditing(boolean enable) {
        usernameEditText.setEnabled(enable);
        if (enable) {
            changeUsernameButton.setText("Enregistrer Nom d'utilisateur");
            changeUsernameButton.setOnClickListener(v -> saveNewUsername());
        } else {
            changeUsernameButton.setText("Modifier Nom d'utilisateur");
        }
    }
    private void saveNewPassword() {
        try {
            JSONArray usersArray = readUsersFromFile();
            if (usersArray.length() == 0) {
                Toast.makeText(getActivity(), "Aucun utilisateur trouvé", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject lastUser = usersArray.getJSONObject(usersArray.length() - 1);
            String oldPassword = lastUser.optString("password", ""); // Récupère l'ancien mot de passe

            // Vérifier si l'ancien mot de passe est correct
            if (!oldPasswordEditText.getText().toString().equals(oldPassword)) {
                Toast.makeText(getActivity(), "Ancien mot de passe incorrect", Toast.LENGTH_SHORT).show();
                return;
            }

            // Mettre à jour le mot de passe avec la nouvelle valeur
            lastUser.put("password", newPasswordEditText.getText().toString().trim());
            saveUsersToFile(usersArray);
            Toast.makeText(getActivity(), "Mot de passe mis à jour avec succès", Toast.LENGTH_SHORT).show();

            // Réinitialiser l'affichage
            oldPasswordEditText.setText("");
            newPasswordEditText.setText("");
            enablePasswordChange(false);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Erreur lors de la mise à jour du mot de passe", Toast.LENGTH_SHORT).show();
        }
    }
    private void saveNewUsername() {
        try {
            JSONArray usersArray = readUsersFromFile();
            JSONObject lastUser = usersArray.getJSONObject(usersArray.length() - 1);
            lastUser.put("username", usernameEditText.getText().toString().trim());
            saveUsersToFile(usersArray);
            Toast.makeText(getActivity(), "Nom d'utilisateur mis à jour", Toast.LENGTH_SHORT).show();
            enableUsernameEditing(false);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
    private JSONArray readUsersFromFile() throws IOException, JSONException {
        if (getActivity() == null) return new JSONArray(); // Vérifie que l'activité est bien attachée

        try {
            FileInputStream fis = getActivity().openFileInput(FILE_NAME);
            byte[] data = new byte[fis.available()];
            fis.read(data);
            fis.close();
            return new JSONArray(new String(data));
        } catch (IOException | JSONException e) {
            return new JSONArray(); // Retourne un tableau vide si le fichier n'existe pas
        }
    }
    private void saveUsersToFile(JSONArray usersArray) throws IOException {
        if (getActivity() == null) return;

        FileOutputStream fos = getActivity().openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
        fos.write(usersArray.toString().getBytes());
        fos.close();
    }
    private void enablePasswordChange(boolean enable) {
        oldPasswordEditText.setVisibility(enable ? View.VISIBLE : View.GONE);
        newPasswordEditText.setVisibility(enable ? View.VISIBLE : View.GONE);
        saveNewPasswordButton.setVisibility(enable ? View.VISIBLE : View.GONE);
    }
}

