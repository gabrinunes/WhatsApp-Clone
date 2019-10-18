package gabrielcunha.cursoandroid.whatsapp.activity;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import gabrielcunha.cursoandroid.whatsapp.R;
import gabrielcunha.cursoandroid.whatsapp.adapter.AdapterContatos;
import gabrielcunha.cursoandroid.whatsapp.config.ConfiguracaoFirebase;
import gabrielcunha.cursoandroid.whatsapp.helper.UsuarioFirebase;
import gabrielcunha.cursoandroid.whatsapp.model.Usuario;

public class GrupoActivity extends AppCompatActivity {

    private RecyclerView recyclerMembrosSelecionados,recyclerMembros;
    private AdapterContatos adapterContatos;
    private List<Usuario> listaMembros = new ArrayList<>();
    private ValueEventListener valueEventListenerMembros;
    private DatabaseReference usuarioRef;
    private FirebaseUser usarioAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);
        inicializarComponentes();
        Toolbar toolbar = findViewById(R.id.toolbar);

        //Configurar adapter

        adapterContatos = new AdapterContatos(listaMembros,getApplicationContext());

        //Configurar RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMembros.setLayoutManager(layoutManager);
        recyclerMembros.setHasFixedSize(true);
        recyclerMembros.setAdapter(adapterContatos);

        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarContatos();
    }

    private void recuperarContatos() {
        valueEventListenerMembros = usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Usuario usuario = ds.getValue(Usuario.class);
                    String emailUsuarioAtual = usarioAtual.getEmail();
                    if (!emailUsuarioAtual.equals(usuario.getEmail())) {
                        listaMembros.add(usuario);
                    }

                }
                adapterContatos.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        usuarioRef.removeEventListener(valueEventListenerMembros);
    }

    private void inicializarComponentes() {
        recyclerMembrosSelecionados = findViewById(R.id.recyclerMembrosSelecionados);
        recyclerMembros = findViewById(R.id.recyclerMembros);
        usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios");
        usarioAtual = UsuarioFirebase.getUsuarioAtual();
    }

}
