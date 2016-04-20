package com.emyyn.riley.nypmedicationsmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Riley on 4/15/2016.
 */
public class DetailActivity extends AppCompatActivity {

    private Medication mMedication = null;
    private Toolbar toolbar;

    public DetailActivity() {
    }

    public DetailActivity(Medication medication) {
        mMedication = medication;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            setTitle("MedicationsName");
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {

        private Medication mMedication;


        public DetailFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_details, container, false);
            Log.i("Details", "New Details");
            // The detail Activity called via intent.  Inspect the intent for forecast data.
            Intent intent = getActivity().getIntent();
            if (intent != null) {
                mMedication = (Medication) intent.getSerializableExtra("med");
                getActivity().setTitle(mMedication.getShortTitle());
                viewHolder(rootView, mMedication);


                // ((TextView) rootView.findViewById(R.id.detail_text)).setText(forecastStr);
            }

            return rootView;
        }

        private void viewHolder(View v, Medication m) {

            TextView instructions = (TextView) v.findViewById(R.id.details_instruction_text);
            TextView quantity = (TextView) v.findViewById(R.id.details_quantity);
            TextView period = (TextView) v.findViewById(R.id.details_period);
            TextView startEnd = (TextView) v.findViewById(R.id.details_date);

            Button alerts = (Button) v.findViewById(R.id.details_alerts);
            Log.i("Details", "New View Holder");
            if (m != null) {
                Log.i("Details", m.toString());
                instructions.setText(m.getDosageInstructions());
                quantity.setText(m.getQuantity());
                period.setText(m.getPeriod() + m.getPeriodUnits());
                startEnd.setText("Started Medication: " + m.getStart().toString() + "Expiration Date: " + m.getEnd());
            } else {
                instructions.setText("No instructions");
            }

        }
    }
}