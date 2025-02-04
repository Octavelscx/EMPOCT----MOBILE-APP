package com.example.empoct;

import android.content.Context;
import android.os.Bundle;
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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ForgotPasswordFragment extends Fragment {

    private EditText usernameEditText, securityAnswer1EditText, securityAnswer2EditText, securityAnswer3EditText, newPasswordEditText;
    private Button resetPasswordButton, saveNewPasswordButton;

    private static final String FILE_NAME = "users.json";
    private JSONObject matchedUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        usernameEditText = view.findViewById(R.id.usernameEditText);
        securityAnswer1EditText = view.findViewById(R.id.securityAnswer1EditText);
        securityAnswer2EditText = view.findViewById(R.id.securityAnswer2EditText);
        securityAnswer3EditText = view.findViewById(R.id.securityAnswer3EditText);
        newPasswordEditText = view.findViewById(R.id.newPasswordEditText);
        resetPasswordButton = view.findViewById(R.id.resetPasswordButton);
        saveNewPasswordButton = view.findViewById(R.id.saveNewPasswordButton);

        resetPasswordButton.setOnClickListener(v -> verifySecurityAnswers());
        saveNewPasswordButton.setOnClickListener(v -> saveNewPassword());

        return view;
    }

    private void verifySecurityAnswers() {
        String username = usernameEditText.getText().toString().trim();
        String answer1 = securityAnswer1EditText.getText().toString().trim();
        String answer2 = securityAnswer2EditText.getText().toString().trim();
        String answer3 = securityAnswer3EditText.getText().toString().trim();

        if (username.isEmpty() || answer1.isEmpty() || answer2.isEmpty() || answer3.isEmpty()) {
            Toast.makeText(getActivity(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONArray usersArray = readUsersFromFile();

            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject user = usersArray.getJSONObject(i);

                if (user.getString("username").equals(username)) {
                    if (user.getString("securityQuestion1").equals(answer1) &&
                            user.getString("securityQuestion2").equals(answer2) &&
                            user.getString("securityQuestion3").equals(answer3)) {

                        matchedUser = user; // Utilisateur trouvé
                        Toast.makeText(getActivity(), "Réponses correctes. Vous pouvez définir un nouveau mot de passe.", Toast.LENGTH_SHORT).show();

                        // Afficher le champ pour le nouveau mot de passe
                        newPasswordEditText.setVisibility(View.VISIBLE);
                        saveNewPasswordButton.setVisibility(View.VISIBLE);
                        return;
                    } else {
                        Toast.makeText(getActivity(), "Les réponses sont incorrectes", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }

            Toast.makeText(getActivity(), "Utilisateur non trouvé", Toast.LENGTH_SHORT).show();

        } catch (JSONException | IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Erreur lors de la vérification", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveNewPassword() {
        String newPassword = newPasswordEditText.getText().toString().trim();

        if (newPassword.isEmpty()) {
            Toast.makeText(getActivity(), "Veuillez entrer un nouveau mot de passe", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (matchedUser != null) {
                JSONArray usersArray = readUsersFromFile();

                // Trouver et mettre à jour le mot de passe de l'utilisateur dans le tableau
                for (int i = 0; i < usersArray.length(); i++) {
                    JSONObject user = usersArray.getJSONObject(i);
                    if (user.getString("username").equals(matchedUser.getString("username"))) {
                        user.put("password", newPassword); // Mettre à jour le mot de passe
                        break;
                    }
                }

                // Sauvegarder les données mises à jour
                saveUsersToFile(usersArray);

                Toast.makeText(getActivity(), "Mot de passe mis à jour avec succès", Toast.LENGTH_SHORT).show();

                // Réinitialiser l'interface
                usernameEditText.setText("");
                securityAnswer1EditText.setText("");
                securityAnswer2EditText.setText("");
                securityAnswer3EditText.setText("");
                newPasswordEditText.setText("");
                newPasswordEditText.setVisibility(View.GONE);
                saveNewPasswordButton.setVisibility(View.GONE);

                matchedUser = null; // Réinitialiser l'utilisateur trouvé

                // Retour à l'authentification
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
            }

        } catch (JSONException | IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Erreur lors de la mise à jour du mot de passe", Toast.LENGTH_SHORT).show();
        }
    }

    private JSONArray readUsersFromFile() throws IOException, JSONException {
        FileInputStream fis = getActivity().openFileInput(FILE_NAME);
        byte[] data = new byte[fis.available()];
        fis.read(data);
        fis.close();

        String json = new String(data);
        return new JSONArray(json);
    }

    private void saveUsersToFile(JSONArray usersArray) throws IOException {
        FileOutputStream fos = getActivity().openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
        fos.write(usersArray.toString().getBytes());
        fos.close();
    }
}
