package com.example.firstjava;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReservationRecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;

    private List<ReservationItem> items;

    public ReservationRecyclerAdapter(List<ReservationItem> items) {
        this.items = items;
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation_item, parent, false);
            return new ListItemViewHolder(v);
        }
        else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation_header, parent, false);
            return new ListHeaderViewHolder(v);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
        final ReservationItem item = items.get(position);
        if (item.type == TYPE_ITEM) {
            ListItemViewHolder itemViewHolder = (ListItemViewHolder)holder;
            itemViewHolder.item_title.setText(item.text);
        }
        else if (item.type == TYPE_HEADER) {
            ListHeaderViewHolder itemViewHolder = (ListHeaderViewHolder)holder;
            itemViewHolder.header_title.setText(item.text);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return this.items.get(position).type;
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    private static class ListHeaderViewHolder extends RecyclerAdapter.ViewHolder {
        public TextView header_title;

        public ListHeaderViewHolder(View itemView) {
            super(itemView);
            header_title = (TextView) itemView.findViewById(R.id.header_title);
        }
    }

    private static class ListItemViewHolder extends RecyclerAdapter.ViewHolder {
        public TextView item_title;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            item_title = (TextView) itemView.findViewById(R.id.item_title);
        }
    }
}
