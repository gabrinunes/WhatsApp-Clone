package gabrielcunha.cursoandroid.whatsapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import gabrielcunha.cursoandroid.whatsapp.R;

public class LoginActivity extends AppCompatActivity {

    private Button buttonLogar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inicializarComponentes();
    }

    private void inicializarComponentes() {

    }
    public void abrirTelaCadastro(View view){
        startActivity(new Intent(LoginActivity.this,CadastroActivity.class));
    }
}
