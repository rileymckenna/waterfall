package com.emyyn.riley.nypmedicationsmonitor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Riley on 4/16/2016.
 */
public class MedicationAdapter<M> extends ArrayAdapter<Medication> {
    public MedicationAdapter(Context context, ArrayList<Medication> medications){
        super(context,0, medications);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Medication med = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_med_list, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvDosage = (TextView) convertView.findViewById(R.id.tvDosage);
        // Populate the data into the template view using the data object
        //tvName.setText("text");
       // tvDosage.setText(med.dosageInstructions);
        // Return the completed view to render on screen
        return convertView;
    }

}
