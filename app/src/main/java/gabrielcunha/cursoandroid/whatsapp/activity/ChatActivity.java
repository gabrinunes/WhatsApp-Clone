package gabrielcunha.cursoandroid.whatsapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import gabrielcunha.cursoandroid.whatsapp.R;
import gabrielcunha.cursoandroid.whatsapp.adapter.AdapterMensagens;
import gabrielcunha.cursoandroid.whatsapp.config.ConfiguracaoFirebase;
import gabrielcunha.cursoandroid.whatsapp.helper.Base64Custom;
import gabrielcunha.cursoandroid.whatsapp.helper.UsuarioFirebase;
import gabrielcunha.cursoandroid.whatsapp.model.Conversa;
import gabrielcunha.cursoandroid.whatsapp.model.Grupo;
import gabrielcunha.cursoandroid.whatsapp.model.Mensagem;
import gabrielcunha.cursoandroid.whatsapp.model.Usuario;

public class ChatActivity extends AppCompatActivity {

    private CircleImageView circleImageViewFoto;
    private TextView textViewNome;
    private EditText editMensagem;
    private ImageView imageCamera;
    private static final int SELECAO_CAMERA = 100;
    private Usuario usuarioDestinatario;
    private Usuario usuarioRemetente = UsuarioFirebase.getDadosUsuarioLogado();
    private AdapterMensagens adapterMensagens;
    private List<Mensagem> mensagens = new ArrayList<>();
    private SimpleDateFormat formataData;
    private SimpleDateFormat formataHora;
    private String dataFormatada;
    private String horaAtual;
    Date data = new Date();
    Calendar cal;
    private DatabaseReference firebaseRef;
    private DatabaseReference mensagensRef;
    private StorageReference storageReference;
    private ChildEventListener childEventListenerMensagens;

    //identificador usuarios remetente e destinatario
    private String idUsuarioRemetente;
    private String idUsuarioDestinatario;
    private Grupo grupo;

    private RecyclerView recyclerMensagens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        inicializarComponentes();

        //Configurar data
        formataData = new SimpleDateFormat("dd-MM-yyyy");
        formataHora = new SimpleDateFormat("HH:mm");

        dataFormatada = formataData.format(data);
        cal = Calendar.getInstance();
        cal.setTime(data);
        Date dataAtual = cal.getTime();
        horaAtual = formataHora.format(dataAtual);



        //Recuperar dados do usuário destinatario
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            if (bundle.containsKey("chatGrupo")) {

                grupo = (Grupo) bundle.getSerializable("chatGrupo");
                idUsuarioDestinatario = grupo.getId();
                textViewNome.setText(grupo.getNome());

                String foto = grupo.getFoto();
                if (foto != null) {
                    Picasso.get()
                            .load(foto)
                            .into(circleImageViewFoto);
                } else {
                    circleImageViewFoto.setImageResource(R.drawable.padrao);
                }

            } else {
                /*********/
                usuarioDestinatario = (Usuario) bundle.getSerializable("chatContato");
                textViewNome.setText(usuarioDestinatario.getNome());

                String foto = usuarioDestinatario.getFoto();
                if (foto != null) {
                    Picasso.get()
                            .load(foto)
                            .into(circleImageViewFoto);
                } else {
                    circleImageViewFoto.setImageResource(R.drawable.padrao);
                }

                //recuperar dados usuario destinatario

                idUsuarioDestinatario = Base64Custom.codificarBase64(usuarioDestinatario.getEmail());
                /***********/
            }
        }

        //Configuração do adapter
        adapterMensagens = new AdapterMensagens(mensagens, this);
        //Configuração do RecyclerView

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerMensagens.setLayoutManager(layoutManager);
        recyclerMensagens.setHasFixedSize(true);
        recyclerMensagens.setAdapter(adapterMensagens);


        mensagensRef = firebaseRef.child("mensagens")
                .child(idUsuarioRemetente)
                .child(idUsuarioDestinatario);

        storageReference = ConfiguracaoFirebase.getFirebaseStorage();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_CAMERA);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECAO_CAMERA) {
            Bitmap image = null;

            try {
                switch (requestCode) {
                    case SELECAO_CAMERA:
                        image = (Bitmap) data.getExtras().get("data");
                        break;
                }
                if (image != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //Criar nome da imagem
                    String nomeImagem = UUID.randomUUID().toString();

                    StorageReference imageRef = storageReference
                            .child("imagens")
                            .child("fotos")
                            .child(idUsuarioRemetente)
                            .child(nomeImagem);

                    UploadTask uploadTask = imageRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            String url = taskSnapshot.getDownloadUrl().toString();

                            Mensagem mensagem = new Mensagem();
                            mensagem.setIdUsuario(idUsuarioRemetente);
                            mensagem.setMensagem("imagem.jpeg");
                            mensagem.setImagem(url);

                            //Salvando para o remetente
                            salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);
                            //Salvando para o destinatario
                            salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void inicializarComponentes() {
        circleImageViewFoto = findViewById(R.id.circleImageFotoChat);
        textViewNome = findViewById(R.id.textViewNomeChat);
        editMensagem = findViewById(R.id.editMensagem);
        recyclerMensagens = findViewById(R.id.recyclerMensagens);
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        imageCamera = findViewById(R.id.imageCamera);
        usuarioRemetente = UsuarioFirebase.getDadosUsuarioLogado();
        //Recupera dados do usuario remetente
        idUsuarioRemetente = UsuarioFirebase.getIdUsuario();
    }

    private void exibirMensagem(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }


    public void enviarMensagem(View view) {

        String textoMensagem = editMensagem.getText().toString();

        if (!textoMensagem.isEmpty()) {

            if (usuarioDestinatario != null) {

                Mensagem msg = new Mensagem();
                msg.setIdUsuario(idUsuarioRemetente);
                msg.setMensagem(textoMensagem);
                msg.setHora(horaAtual);

                //Salvar a mensagem para o remetente
                salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, msg);

                //Salvar a mensagem para o destinatario
                salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, msg);

                //Salvar conversa remetente
                salvarConversa(idUsuarioRemetente,idUsuarioDestinatario,usuarioDestinatario,msg, false);
                //Salvar conver destinatario
                salvarConversa(idUsuarioDestinatario,idUsuarioRemetente,usuarioRemetente,msg, false);

            } else {

                for (Usuario membro : grupo.getMembros()) {

                    String idRemetenteGrupo = Base64Custom.codificarBase64(membro.getEmail());
                    String idUsuarioLogadoGrupo = UsuarioFirebase.getIdUsuario();

                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario(idUsuarioLogadoGrupo);
                    mensagem.setMensagem(textoMensagem);
                    mensagem.setNome(usuarioRemetente.getNome());

                    //salvar mensagem para o membro
                    salvarMensagem(idRemetenteGrupo, idUsuarioDestinatario, mensagem);
                    //Salvar conversa
                    salvarConversa(idRemetenteGrupo,idUsuarioDestinatario,usuarioDestinatario,mensagem, true);
                }
            }

        } else {
            exibirMensagem("Digite uma mensagem para enviar!");
        }
    }

    private void salvarConversa(String idRemetente,String idDestinatario,Usuario usuarioExibicao,Mensagem msg, boolean isGroup) {


        //Salvar conversa remetente
        Conversa conversaRemetente = new Conversa();
        conversaRemetente.setIdRemetente(idRemetente);
        conversaRemetente.setIdDestinatario(idDestinatario);
        conversaRemetente.setUltimaMensagem(msg.getMensagem());

        if (isGroup) {//Conversa grupo
            conversaRemetente.setIsGroup("true");
            conversaRemetente.setGrupo(grupo);
        } else {//Conversa normal
            conversaRemetente.setUsuarioExibicao(usuarioExibicao);
            conversaRemetente.setIsGroup("false");
        }

        conversaRemetente.salvar();

    }

    private void salvarMensagem(String idRemetente, String idDestinatario, Mensagem msg) {

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference mensagemRef = firebaseRef.child("mensagens");

        mensagemRef.child(idRemetente)
                .child(idDestinatario)
                .push()
                .setValue(msg);

        //Limpar texto
        editMensagem.setText("");

    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarMensagens();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mensagens.clear();
        mensagensRef.removeEventListener(childEventListenerMensagens);
    }

    private void recuperarMensagens() {

        childEventListenerMensagens = mensagensRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Mensagem mensagem = dataSnapshot.getValue(Mensagem.class);
                mensagens.add(mensagem);
                adapterMensagens.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
