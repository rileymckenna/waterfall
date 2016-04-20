package com.emyyn.riley.nypmedicationsmonitor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Riley on 4/15/2016.
 */
public class AppointmentsFragment  extends Fragment {

    private ArrayAdapter<String> mForecastAdapter;

    public AppointmentsFragment() {

        }

        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);

        }

        private ArrayAdapter<String> mAppointmentAdapter;
        private static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            return true;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            final View rootView = inflater.inflate(R.layout.fragment_list, container, false);
            mForecastAdapter =
                    new ArrayAdapter<String>(
                            getActivity(), // The current context (this activity)
                            R.layout.list_item_details, // The name of the layout ID.
                            R.id.textView, // The ID of the textview to populate.
                            new ArrayList<String>());
            
            TextView title = (TextView) rootView.findViewById(R.id.textView);
           // TextView contents = (TextView) rootView.findViewById(R.id.container);


            return rootView;
        }


    public static AppointmentsFragment newInstance (int sectionName){
        AppointmentsFragment appointmentsFragment = new AppointmentsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER,sectionName);
        appointmentsFragment.setArguments(args);
        return appointmentsFragment;
    }

    public static ArrayList<Patient> getData(){
        ArrayList<Patient> data = new ArrayList<>();
        String[] title = {"Lusie","David","Alex","Danielle","Courtney","Tom","Greg","Martha","Robert","Bethany","Clare","Annie","Anna"};
        String[] dob = {"3/28/1990","1/14/2015","2/5/1989","5/9/1972","5/21/1991","5/17/1964","7/22/1954","6/18/1964","8/11/2006","6/26/1998","7/27/2000","11/28/1989","12/6/1987"};
        String[] id = {"654654654654654","32158497365241654","6359879841321489432","89746513214594923","97484562132165869","98762131231195674","987653213213165479451321","8979654635216584984","89746543215649421","9874651321654984621968","6549879413216846518","32134896574913198645","897961321354823158"};
        for (int i = 0; i <title.length && i < dob.length && i < id.length; i++){
            Patient current = new Patient();
            current.given = title[i];
            current.dob = dob[i];
            current.id = id[i];
            current.surname = "Smith";
            data.add(current);
        }
        return data;
    }

        @Override
        public void onStart() {
            super.onStart();
        }

    }


