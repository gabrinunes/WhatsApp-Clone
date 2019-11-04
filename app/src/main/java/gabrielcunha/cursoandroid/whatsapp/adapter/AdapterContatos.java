package gabrielcunha.cursoandroid.whatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import gabrielcunha.cursoandroid.whatsapp.R;
import gabrielcunha.cursoandroid.whatsapp.model.Usuario;

public class AdapterContatos extends RecyclerView.Adapter<AdapterContatos.MyViewHolder> {

    private List<Usuario> contatos;
    private Context context;

    public AdapterContatos(List<Usuario> contatos, Context c) {

        this.contatos = contatos;
        this.context = c;
    }

    public List<Usuario> getContatos (){
        return this.contatos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contatos, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Usuario usuario = contatos.get(position);
        boolean cabecalho = usuario.getEmail().isEmpty();

        holder.nomePerfil.setText(usuario.getNome());
        holder.Email.setText(usuario.getEmail());

        //Carrega imagem

        if (usuario.getFoto() != null) {
            Uri uri = Uri.parse(usuario.getFoto());
            Glide.with(context)
                    .load(uri)
                    .into(holder.imagemPerfil);
        } else {
            if (cabecalho) {
                holder.imagemPerfil.setImageResource(R.drawable.icone_grupo);
                holder.Email.setVisibility(View.GONE);
            } else {
                holder.imagemPerfil.setImageResource(R.drawable.padrao);
            }
        }
    }

    @Override
    public int getItemCount() {
        return contatos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imagemPerfil;
        TextView nomePerfil;
        TextView Email;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imagemPerfil = itemView.findViewById(R.id.imagePerfil);
            nomePerfil = itemView.findViewById(R.id.NomeUsuario);
            Email = itemView.findViewById(R.id.Email);
        }
    }
}


