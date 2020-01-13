package com.example.dropshop;


import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private List<Modal> list;

    public Adapter(List<Modal> list) {
        this.list = list;
    }

    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.single_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(Adapter.ViewHolder holder, int position) {

        holder.pd.setText(list.get(position).getProductDesc());
        holder.ci.setText(list.get(position).getCustomerId());
        holder.expiry.setText(converToDate(list.get(position).getExpiry()));
        holder.mrp.setText(String.valueOf(list.get(position).getMrp()));
        holder.bn.setText(list.get(position).getBrandName());
        holder.pc.setText(String.valueOf(list.get(position).getProductCode()));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView pd,ci,bn,mrp,expiry,pc;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            pd =itemView.findViewById(R.id.pd);
            pc =itemView.findViewById(R.id.pc);
            ci =itemView.findViewById(R.id.ci);
            expiry =itemView.findViewById(R.id.expiry);
            bn =itemView.findViewById(R.id.bn);
            mrp =itemView.findViewById(R.id.mrp);

        }
    }
    private String converToDate(long timeStamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timeStamp * 1000);
        String date = DateFormat.format("dd/MM/yyyy HH:mm:ss", cal).toString();
        return date;
    }
}
