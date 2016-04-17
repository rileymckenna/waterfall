package com.emyyn.riley.nypmedicationsmonitor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

            final View rootView = inflater.inflate(R.layout.fragment_med_list, container, false);
            ListView listView = (ListView) rootView.findViewById(R.id.listview_medications);
            listView.setAdapter(mAppointmentAdapter);

            return rootView;
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


