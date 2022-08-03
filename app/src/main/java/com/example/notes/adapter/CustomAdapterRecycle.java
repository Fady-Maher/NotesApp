package com.example.notes;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomAdapterRecycle extends RecyclerView.Adapter<CustomAdapterRecycle.Customviewholder> {
    private Context context;
    private ArrayList<information> infos;

    public CustomAdapterRecycle(Context c,ArrayList<information> info){
        context=c;
        infos=info;
    }


    @NonNull
    @Override
    public Customviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.image_item,parent,false);
        return new Customviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Customviewholder holder, int position) {
        information inform=infos.get(position);
        holder.note.setText(inform.getNote());
        holder.title.setText(inform.getTitle());
        holder.time.setText(inform.getTime());
        Picasso.with(context)
                .load(inform.getImageid())
                .fit()
                .placeholder(R.mipmap.ic_launcher).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return infos.size();
    }

    public class Customviewholder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView title;
        public TextView note;
        public TextView time;


        public Customviewholder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.image_item_view);
            title=itemView.findViewById(R.id.title_item_view);
            note=itemView.findViewById(R.id.note_item_view);
            time=itemView.findViewById(R.id.time_item_view);

        }
    }

}
