package com.example.estetify;

import android.content.Intent;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.firebase.auth.FirebaseAuth;

public class AuthVerification {
    public static void verificarAutenticacao(Fragment fragment, FragmentManager fragmentManager) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Verifica apenas se o usuário está logado no Authentication
        if (auth.getCurrentUser() == null) {
            Toast.makeText(fragment.getContext(), 
                "Faça login para continuar.", 
                Toast.LENGTH_LONG).show();
                
            Intent intent = new Intent(fragment.getContext(), MainInicio.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            fragment.startActivity(intent);
        }
    }
}
