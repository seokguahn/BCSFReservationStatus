package com.example.firstjava;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    Context context;
    List<Item> items;
    int item_layout;

    ItemClickCallbackListener clickCallbackListener;

    public void setItemClickCallBackListener(ItemClickCallbackListener clickCallbackListener) {
        this.clickCallbackListener = clickCallbackListener;
    }

    public RecyclerAdapter(Context context, List<Item> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Item item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.location.setOnClickListener(new  View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCallbackListener.callBackMethod(item.getTitle(), item.getIntoUrl(), 0);
            }
        });
        holder.receptionStatus.setText(item.getReceptionStatus());
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickCallbackListener.callBackMethod(item.getTitle(), item.getIntoUrl(), 1);
            }
        });

        if(item.getReceptionStatus().contains("접수중") || item.getReceptionStatus().contains("예약중")) {
            holder.receptionStatus.setBackgroundColor(Color.parseColor("#e8f1fa"));
            holder.receptionStatus.setTextColor(Color.parseColor("#61b2f0"));
        }
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        androidx.appcompat.widget.AppCompatButton location;
        TextView receptionStatus;
        CardView cardview;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            location = (androidx.appcompat.widget.AppCompatButton)itemView.findViewById(R.id.location);
            receptionStatus = (TextView) itemView.findViewById(R.id.receptionStatus);
            cardview = (CardView) itemView.findViewById(R.id.cardview);
        }
    }
}
