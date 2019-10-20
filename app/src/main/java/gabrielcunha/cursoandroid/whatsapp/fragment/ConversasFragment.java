package gabrielcunha.cursoandroid.whatsapp.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import gabrielcunha.cursoandroid.whatsapp.R;
import gabrielcunha.cursoandroid.whatsapp.activity.ChatActivity;
import gabrielcunha.cursoandroid.whatsapp.adapter.AdapterConversas;
import gabrielcunha.cursoandroid.whatsapp.config.ConfiguracaoFirebase;
import gabrielcunha.cursoandroid.whatsapp.helper.RecyclerItemClickListener;
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
    private String identificadorUsuario;


    public ConversasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        recyclerConVersas = view.findViewById(R.id.recyclerConversas);
        identificadorUsuario = UsuarioFirebase.getIdUsuario();
        conversasRef = ConfiguracaoFirebase.getFirebaseDatabase().child("conversas").child(identificadorUsuario);

        //Configura o adapter

        adapterConversas = new AdapterConversas(conversas, getActivity());


        //Configura o RecyclerView

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerConVersas.setLayoutManager(layoutManager);
        recyclerConVersas.setHasFixedSize(true);
        recyclerConVersas.setAdapter(adapterConversas);

        //Configurar evento de clique

        recyclerConVersas.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerConVersas,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        List<Conversa>listaConversasAtualizadas = adapterConversas.getConversas();
                        Conversa conversaSelecionada = listaConversasAtualizadas.get(position);

                        if (conversaSelecionada.getIsGroup().equals("true")) {

                            Intent i = new Intent(getActivity(), ChatActivity.class);
                            i.putExtra("chatGrupo", conversaSelecionada.getGrupo());
                            startActivity(i);

                        } else {

                            Intent i = new Intent(getActivity(), ChatActivity.class);
                            i.putExtra("chatContato", conversaSelecionada.getUsuarioExibicao());
                            startActivity(i);
                        }

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

        return view;
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


    public void pesquisarConversas(String newText) {

        List<Conversa> listaConversasBusca = new ArrayList<>();

        for (Conversa conversa : conversas) {

            if (conversa.getUsuarioExibicao() != null) {
                String nome = conversa.getUsuarioExibicao().getNome().toLowerCase();
                String ultimaMensagem = conversa.getUltimaMensagem().toLowerCase();

                if (nome.contains(newText) || ultimaMensagem.contains(newText)) {
                    listaConversasBusca.add(conversa);
                }
            } else {
                if (conversa.getUsuarioExibicao() != null) {
                    String nome = conversa.getGrupo().getNome().toLowerCase();
                    String ultimaMensagem = conversa.getUltimaMensagem().toLowerCase();

                    if (nome.contains(newText) || ultimaMensagem.contains(newText)) {
                        listaConversasBusca.add(conversa);
                    }
                }
            }

            }
            adapterConversas = new AdapterConversas(listaConversasBusca, getActivity());
            recyclerConVersas.setAdapter(adapterConversas);
            adapterConversas.notifyDataSetChanged();

        }

        public void recarregarConversas () {
            adapterConversas = new AdapterConversas(conversas, getActivity());
            recyclerConVersas.setAdapter(adapterConversas);
            adapterConversas.notifyDataSetChanged();
        }

        public void recuperarConversas () {

            valueEventListenerConversas = conversasRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    conversas.clear();

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
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
