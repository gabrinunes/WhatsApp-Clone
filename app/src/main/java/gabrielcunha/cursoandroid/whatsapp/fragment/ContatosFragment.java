package gabrielcunha.cursoandroid.whatsapp.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import gabrielcunha.cursoandroid.whatsapp.R;
import gabrielcunha.cursoandroid.whatsapp.adapter.AdapterContatos;
import gabrielcunha.cursoandroid.whatsapp.config.ConfiguracaoFirebase;
import gabrielcunha.cursoandroid.whatsapp.model.Usuario;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContatosFragment extends Fragment {

    private RecyclerView recyclerContatos;
    private AdapterContatos adapterContatos;
    private List<Usuario> usuarios = new ArrayList<>();
    private DatabaseReference usuarioRef;
    private ValueEventListener valueEventListenerContatos;


    public ContatosFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        recyclerContatos = view.findViewById(R.id.recyclerContatos);
        usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios");


        //Configurar adapter

        adapterContatos = new AdapterContatos(usuarios,getActivity());

        //Configurar RecyclerView

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());// Usar getActivity para pegar o context quando estiver usando um fragment
        recyclerContatos.setLayoutManager(layoutManager);
        recyclerContatos.setHasFixedSize(true);
        recyclerContatos.setAdapter(adapterContatos);



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperaUsuarios();
    }

    @Override
    public void onStop() {
        super.onStop();
        usuarioRef.removeEventListener(valueEventListenerContatos);
    }

    private void recuperaUsuarios() {

        valueEventListenerContatos = usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 usuarios.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Usuario usuario = ds.getValue(Usuario.class);
                    usuarios.add(usuario);
                }
                adapterContatos.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
