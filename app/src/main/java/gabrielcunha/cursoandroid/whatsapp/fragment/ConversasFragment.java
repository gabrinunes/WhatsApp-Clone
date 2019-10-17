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
import gabrielcunha.cursoandroid.whatsapp.adapter.AdapterConversas;
import gabrielcunha.cursoandroid.whatsapp.config.ConfiguracaoFirebase;
import gabrielcunha.cursoandroid.whatsapp.helper.UsuarioFirebase;
import gabrielcunha.cursoandroid.whatsapp.model.Conversa;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConversasFragment extends Fragment {

    private RecyclerView recyclerConVersas;
    private List<Conversa> conversas = new ArrayList<>();
    private AdapterConversas adapterConversas;
    private DatabaseReference conversasRef;
    private ValueEventListener valueEventListenerConversas;



    public ConversasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        recyclerConVersas = view.findViewById(R.id.recyclerConversas);
        String identificadorUsuario = UsuarioFirebase.getIdUsuario();
        conversasRef = ConfiguracaoFirebase.getFirebaseDatabase().child("conversas").child(identificadorUsuario);

        //Configura o adapter

        adapterConversas = new AdapterConversas(conversas,getActivity());


        //Configura o RecyclerView

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerConVersas.setLayoutManager(layoutManager);
        recyclerConVersas.setHasFixedSize(true);
        recyclerConVersas.setAdapter(adapterConversas);

        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarConversas();
    }

    @Override
    public void onStop() {
        super.onStop();
        conversasRef.removeEventListener(valueEventListenerConversas);
    }

    private void recuperarConversas() {

        valueEventListenerConversas = conversasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                conversas.clear();

                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Conversa conversa = ds.getValue(Conversa.class);
                    conversas.add(conversa);
                }
                adapterConversas.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
