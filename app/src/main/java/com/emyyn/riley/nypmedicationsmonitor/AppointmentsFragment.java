package com.emyyn.riley.nypmedicationsmonitor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Riley on 4/15/2016.
 */
public class AppointmentsFragment  extends Fragment {

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

            List<String>patientArray = getData();
            mAppointmentAdapter =
                    new ArrayAdapter<String>(
                            getActivity(), // The current context (this activity)
                            R.layout.list_item_details, // The name of the layout ID.
                            R.id.list_item_medication_textview, // The ID of the textview to populate.
                            patientArray);

            final View rootView = inflater.inflate(R.layout.fragment_med_list, container, false);
            ListView listView = (ListView) rootView.findViewById(R.id.listview_medications);
            listView.setAdapter(mAppointmentAdapter);

            return rootView;
        }

        public static List<String> getData(){

            String[] title = {"Lusie","David","Alex","Danielle","Courtney","Tom","Greg","Martha","Robert","Bethany","Clare","Annie","Anna"};


            String[] dob = {"3/28/1990","1/14/2015","2/5/1989","5/9/1972","5/21/1991","5/17/1964","7/22/1954","6/18/1964","8/11/2006","6/26/1998","7/27/2000","11/28/1989","12/6/1987"};
            String[] id = {"654654654654654","32158497365241654","6359879841321489432","89746513214594923","97484562132165869","98762131231195674","987653213213165479451321","8979654635216584984","89746543215649421","9874651321654984621968","6549879413216846518","32134896574913198645","897961321354823158"};
            List<String> data = new ArrayList<String>(Arrays.asList(id));
            return data;
        }

    public static AppointmentsFragment newInstance (int sectionName){
        AppointmentsFragment appointmentsFragment = new AppointmentsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER,sectionName);
        appointmentsFragment.setArguments(args);
        return appointmentsFragment;
    }

        @Override
        public void onStart() {
            super.onStart();
        }

    }


