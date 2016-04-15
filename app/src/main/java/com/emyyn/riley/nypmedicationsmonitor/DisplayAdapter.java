package com.emyyn.riley.nypmedicationsmonitor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Riley on 4/15/2016.
 */
public class DisplayAdapter extends RecyclerView.Adapter<DisplayAdapter.DisplayViewHolder> {

    private final LayoutInflater inflator;
    List<Patient> data = Collections.emptyList();

    public DisplayAdapter(Context context, List<Patient> p) {
       inflator = LayoutInflater.from(context);
    }

    @Override
    public DisplayViewHolder  onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = inflator.inflate(R.layout.list_item_details, parent, false);  //could be .false
        DisplayViewHolder holder = new DisplayViewHolder(root);

        return holder;
    }

    @Override
    public void onBindViewHolder(DisplayViewHolder holder, int position) {
        Patient current = data.get(position);
        holder.title.setText(current.surname);
        holder.title.setText(current.dob.toString());
        holder.title.setText(current.id);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class DisplayViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView mediumText;
        TextView smallText;

        public DisplayViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.tv_title);
            mediumText = (TextView) itemView.findViewById(R.id.tv_medium);
            smallText = (TextView) itemView.findViewById(R.id.tv_small);
        }
    }
}
