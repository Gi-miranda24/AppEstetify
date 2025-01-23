package com.example.estetify;

import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthVerification {
    public static void verificarAutenticacao(Fragment fragment, FragmentManager fragmentManager) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Verificar se está conectado
        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(fragment.getContext(), MainInicio.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            fragment.startActivity(intent);
            return;
        }

        // Verificar se a conta existe
        db.collection("usuarios")
            .document(auth.getCurrentUser().getUid())
            .get()
            .addOnCompleteListener(task -> {
                if (!task.isSuccessful() || !task.getResult().exists()) {
                    // Se a conta não existe no Firestore
                    auth.signOut(); // Desconectar
                    Intent intent = new Intent(fragment.getContext(), MainInicio.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    fragment.startActivity(intent);
                }
            });
    }
}
