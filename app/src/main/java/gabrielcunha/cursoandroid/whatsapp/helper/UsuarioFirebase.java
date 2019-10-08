package gabrielcunha.cursoandroid.whatsapp.helper;

import com.google.firebase.auth.FirebaseAuth;

import gabrielcunha.cursoandroid.whatsapp.config.ConfiguracaoFirebase;

public class UsuarioFirebase {

    public static String getIdUsuario() {


        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        String email = usuario.getCurrentUser().getEmail();
        String idEmail = Base64Custom.codificarBase64(email);

        return idEmail;
    }
}
