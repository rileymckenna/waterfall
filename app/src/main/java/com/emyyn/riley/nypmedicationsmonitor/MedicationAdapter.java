package com.emyyn.riley.nypmedicationsmonitor;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import static android.text.TextUtils.concat;

/**
 * Created by Riley on 4/16/2016.
 */
public class MedicationAdapter extends ArrayAdapter<Medication> {
    private ArrayList<Medication> medicationArrayList;
    private LayoutInflater mInflator;

    public MedicationAdapter(Context context, ArrayList<Medication> medications) {
        super(context, 0, medications);
        medicationArrayList = medications;
        mInflator = LayoutInflater.from(context);
    }

    public int getCount() {
        return medicationArrayList.size();
    }

    public Medication getItem(int position) {
        return medicationArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ViewHolder holder;
        Medication med = getItem(position);
        if (convertView == null) {
            convertView = mInflator.inflate(R.layout.fragment_tab, parent, false);
            Log.i("View Builder", "List Item Name and detasdfasdfas");
            holder = new ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.list_item_name);
            holder.tvDosage = (TextView) convertView.findViewById(R.id.list_item_details);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (med != null) {
            // Check if an existing view is being reused, otherwise inflate the view
          try{
              Log.i("Med", capitalizeFirstLetter(med.getMedicationReference()) + "    " +med.getPeriodUnits() + med.getDosageInstructions());
              holder.tvName.setText(capitalizeFirstLetter(med.getShortTitle()));
             // holder.tvDosage.setText(med.getMethod());
          } catch (Exception e){
              e.printStackTrace();
          }

        } else {
            holder.tvName.setText("No medications are avaliable for this patient");
        }
//         Lookup view for data population
//         Populate the data into the template view using the data object

        //        Return the completed view to render on screen
        return convertView;
    }

    static class ViewHolder {
        TextView tvName;
        TextView tvDosage;
    }

    public String capitalizeFirstLetter ( String input){
        String temp = input;
        int end = temp.length();
        Character capital = temp.charAt(0);
        String notCapital = temp.substring(1, end);
        capital = Character.toUpperCase(capital);
        temp = (String) concat(capital.toString(), notCapital);
        return temp;
    }
}
