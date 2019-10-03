package gabrielcunha.cursoandroid.whatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import gabrielcunha.cursoandroid.whatsapp.R;
import gabrielcunha.cursoandroid.whatsapp.config.ConfiguracaoFirebase;
import gabrielcunha.cursoandroid.whatsapp.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private Button buttonLogar;
    private TextInputEditText editSenha,editEmail;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inicializarComponentes();
    }

    private void inicializarComponentes() {
        editSenha = findViewById(R.id.editLoginSenha);
        editEmail = findViewById(R.id.editlLoginEmail);

    }
    public void abrirTelaCadastro(View view){
        startActivity(new Intent(LoginActivity.this,CadastroActivity.class));
    }
    public void abrirTelaPrincipal(){
        startActivity(new Intent(LoginActivity.this,MainActivity.class));
    }

    public void LoginUsuario(Usuario usuario){
           autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
           autenticacao.signInWithEmailAndPassword(
                   usuario.getEmail(),
                   usuario.getSenha()
           ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
               @Override
               public void onComplete(@NonNull Task<AuthResult> task) {
                   if(task.isSuccessful()){
                        abrirTelaPrincipal();
                   }else{

                       String excecao="";
                       try {
                           throw task.getException();
                       }catch(FirebaseAuthInvalidUserException e) {
                           excecao = "Usuário não está cadastrado.";
                       }catch (FirebaseAuthInvalidCredentialsException e){
                           excecao = "E-mail e senha não correspondem a um usuário cadastrado";
                       }catch (Exception e){
                           excecao ="Erro ao cadastrar usuário: " + e.getMessage();
                           e.printStackTrace();
                       }

                       exibirMensagem(excecao);

                   }
               }
           });
    }

    public void ValidarLogin(View view){
        String textEmail = editEmail.getText().toString();
        String textSenha = editSenha.getText().toString();

        if(!textEmail.isEmpty()){
           if(!textSenha.isEmpty()){
               Usuario usuario = new Usuario();
               usuario.setEmail(textEmail);
               usuario.setSenha(textSenha);
               LoginUsuario(usuario);
           }else {
               exibirMensagem("Preencha a senha!");
           }
        }else{
           exibirMensagem("preencha o e-mail!");
        }
    }
    private void exibirMensagem(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }
}
