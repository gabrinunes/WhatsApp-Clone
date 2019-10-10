package gabrielcunha.cursoandroid.whatsapp.activity;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import gabrielcunha.cursoandroid.whatsapp.R;
import gabrielcunha.cursoandroid.whatsapp.model.Usuario;

public class ChatActivity extends AppCompatActivity {

    private CircleImageView circleImageViewFoto;
    private TextView textViewNome;
    private Usuario usuarioDestinatario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        inicializarComponentes();

        //Recuperar dados do usuário destinatario
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){

            usuarioDestinatario = (Usuario) bundle.getSerializable("chatContato");
            textViewNome.setText(usuarioDestinatario.getNome());

            String foto = usuarioDestinatario.getFoto();
            if(foto!=null){
                Picasso.get()
                        .load(foto)
                        .into(circleImageViewFoto);
            }else{
                circleImageViewFoto.setImageResource(R.drawable.padrao);
            }
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void inicializarComponentes() {
        circleImageViewFoto = findViewById(R.id.circleImageFotoChat);
        textViewNome = findViewById(R.id.textViewNomeChat);
    }

}
