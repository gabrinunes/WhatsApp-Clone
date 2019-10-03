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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import gabrielcunha.cursoandroid.whatsapp.R;
import gabrielcunha.cursoandroid.whatsapp.config.ConfiguracaoFirebase;
import gabrielcunha.cursoandroid.whatsapp.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText editNome, editEmail, editSenha;
    private Button buttonCadastrar;
    private FirebaseAuth autenticacao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        inicializarComponentes();
    }

    private void inicializarComponentes() {

        editNome = findViewById(R.id.editNome);
        editSenha = findViewById(R.id.editSenha);
        editEmail = findViewById(R.id.editEmail);
        buttonCadastrar = findViewById(R.id.buttonCadastrar);
    }

     public void cadastrarUsuario(Usuario usuario){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
               if(task.isSuccessful()){
                  exibirMensagem("Sucesso ao cadastrar usuário");
               }else{
                   String excecao="";
                   try {
                       throw task.getException();
                   }catch(FirebaseAuthWeakPasswordException e) {
                       excecao = "Digite uma senha mais forte";
                   }catch (FirebaseAuthInvalidCredentialsException e) {
                       excecao = "Por favor, digite um e-mail váilod";
                   }catch (FirebaseAuthUserCollisionException e){
                       excecao = "Esta conta já foi cadastrada";
                   }catch (Exception e){
                       excecao ="Erro ao cadastrar usuário: " + e.getMessage();
                       e.printStackTrace();
                   }
                   exibirMensagem(excecao);
               }
            }
        });
     }


    public void validarCadastroUsuario(View view) {

        //Recuperar textos dos campos
        String textNome = editNome.getText().toString();
        String textEmail = editEmail.getText().toString();
        String textSenha = editSenha.getText().toString();

        if (!textNome.isEmpty()) {
            if (!textEmail.isEmpty()) {
                if (!textSenha.isEmpty()) {
                     Usuario usuario = new Usuario();
                     usuario.setNome(textNome);
                     usuario.setEmail(textEmail);
                     usuario.setSenha(textSenha);

                     cadastrarUsuario(usuario);
                } else {
                    exibirMensagem("Preencha a senha!");
                }
            } else {
                exibirMensagem("Preencha o email!");
            }
        } else {
            exibirMensagem("Preencha o nome!");
        }
    }

    private void exibirMensagem(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }


}
