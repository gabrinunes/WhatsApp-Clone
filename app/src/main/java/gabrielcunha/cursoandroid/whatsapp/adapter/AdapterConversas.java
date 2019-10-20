package gabrielcunha.cursoandroid.whatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import gabrielcunha.cursoandroid.whatsapp.R;
import gabrielcunha.cursoandroid.whatsapp.model.Conversa;
import gabrielcunha.cursoandroid.whatsapp.model.Grupo;
import gabrielcunha.cursoandroid.whatsapp.model.Usuario;

public class AdapterConversas extends RecyclerView.Adapter<AdapterConversas.MyViewHolder> {

    private List<Conversa> conversas;
    private Context context;

    public AdapterConversas(List<Conversa> lista, Context context) {

        this.conversas = lista;
        this.context = context;
    }

    public List<Conversa> getConversas(){
        return this.conversas;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contatos, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Conversa conversa = conversas.get(position);
        holder.ultimaMensagem.setText(conversa.getUltimaMensagem());

        if (conversa.getIsGroup().equals("true")) {

            Grupo grupo = conversa.getGrupo();
            holder.nomePerfil.setText(grupo.getNome());
            if (grupo.getFoto() != null) {
                Uri uri = Uri.parse(grupo.getFoto());
                Picasso.get()
                        .load(uri)
                        .into(holder.imagemPerfil);
            } else {
                holder.imagemPerfil.setImageResource(R.drawable.padrao);
            }

        } else {
            Usuario usuario = conversa.getUsuarioExibicao();
            if(usuario!=null){
                holder.nomePerfil.setText(usuario.getNome());

                //Carrega imagem

                if (usuario.getFoto() != null) {
                    Uri uri = Uri.parse(usuario.getFoto());
                    Picasso.get()
                            .load(uri)
                            .into(holder.imagemPerfil);
                } else {
                    holder.imagemPerfil.setImageResource(R.drawable.padrao);
                }
            }
        }


    }

    @Override
    public int getItemCount() {
        return conversas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imagemPerfil;
        TextView nomePerfil;
        TextView ultimaMensagem;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imagemPerfil = itemView.findViewById(R.id.imagePerfil);
            nomePerfil = itemView.findViewById(R.id.NomeUsuario);
            ultimaMensagem = itemView.findViewById(R.id.Email);
        }
    }
}
