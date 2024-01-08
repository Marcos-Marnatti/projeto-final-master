package com.example.projetofinal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projetofinal.R;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private Context context;
    private ArrayList usuario, latitude, longitude, data, distancia, velocidade;

    public Adapter(Context context, ArrayList usuario, ArrayList latitude, ArrayList longitude, ArrayList data, ArrayList distancia, ArrayList velocidade) {
        this.context = context;
        this.usuario = usuario;
        this.latitude = latitude;
        this.longitude = longitude;
        this.data = data;
        this.distancia = distancia;
        this.velocidade = velocidade;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.holders, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.usuario.setText(String.valueOf(usuario.get(position)));
        holder.latitude.setText(String.valueOf(latitude.get(position)));
        holder.longitude.setText(String.valueOf(longitude.get(position)));
        holder.data.setText(String.valueOf(data.get(position)));
        holder.distancia.setText(String.valueOf(distancia.get(position)));
        holder.velocidade.setText(String.valueOf(velocidade.get(position)));
    }

    @Override
    public int getItemCount() {
        return latitude.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView usuario, latitude, longitude, data, distancia, velocidade;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            usuario = itemView.findViewById(R.id.textusuario);
            latitude = itemView.findViewById(R.id.textlatitude);
            longitude = itemView.findViewById(R.id.textlongitude);
            data = itemView.findViewById(R.id.textdata);
            distancia = itemView.findViewById(R.id.textdistancia);
            velocidade = itemView.findViewById(R.id.textvelocidade);

        }
    }
}
