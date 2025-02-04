package com.example.empoct;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Random;

public class NotificationFragment extends DialogFragment {

    private String[] messages = {
            "Félicitations ! Continue comme ça ! 🎉",
            "Chaque pas compte, reste motivé ! 💪",
            "Prends soin de toi, tu es sur la bonne voie ! 💙",
            "Ne lâche rien, tu es plus fort que tu ne le penses ! 🚀",
            "Bravo ! Ton engagement est exemplaire ! 🏆",
            "Chaque jour est une nouvelle victoire ! 🏅",
            "Garde confiance en toi, tu vas y arriver ! ✨",
            "Tu es en train de changer ta vie, continue ! 🔥",
            "Le progrès est progressif, patience et persévérance ! ⏳",
            "Aujourd'hui est une belle journée pour avancer ! 🌞"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        ImageView imageView = view.findViewById(R.id.notification_image);
        TextView textView = view.findViewById(R.id.notification_text);
        Button okButton = view.findViewById(R.id.notification_ok_button);

        // Sélectionner un message aléatoire
        Random random = new Random();
        String randomMessage = messages[random.nextInt(messages.length)];
        textView.setText(randomMessage);

        // Fermer la notification au clic sur OK
        okButton.setOnClickListener(v -> dismiss());

        return view;
    }
}
