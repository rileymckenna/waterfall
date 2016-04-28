package com.emyyn.riley.nypmedicationsmonitor;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Riley on 4/14/2016.
 */
public class MedicationFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    private SwipeRefreshLayout swipeLayout= null;

    public MedicationFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    public static ArrayList<Medication> getmMedicationArray() {
        return mMedicationArray;
    }

    private static ArrayList<Medication> mMedicationArray;
    private MedicationAdapter mMedicationAdapter;
    private static final String ARG_SECTION_NUMBER = "section_number";

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("OnCreate", "Before the Async Task");

        Log.i("OnCreate", "After the Async Task");
        final View rootView = inflater.inflate(R.layout.fragment_tab, container, false);
        Log.i("OnCreate", "After the View");
        mMedicationArray = new ArrayList<>();
        updateMedications();
        try {
            Log.i("Sleeping", "Sleeping for 4000 ms");
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            Log.i("Medications", mMedicationArray.get(0).getDosageInstructions());
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        mMedicationAdapter = new MedicationAdapter(this.getContext(), mMedicationArray);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);

        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(Color.LTGRAY,
                Color.DKGRAY, Color.RED);

        if (mMedicationArray.size()> 0) {
            //textView.setText(capitalizeFirstLetter(mMedicationArray.get(0).getMedicationReference()));
            ListView listView = (ListView) rootView.findViewById(R.id.lv_details);
            listView.setAdapter(mMedicationAdapter);
            //mMedicationAdapter.addAll(mMedicationArray);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Medication meds = mMedicationAdapter.getItem(position);
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra("med", meds);
                    startActivity(intent);
                }
            });
        } else
        {
            Log.i("No meds", "Did not update");
            textView.setText("No Medications are avaiable at this time");
        }

        return rootView;
    }



    public void updateMedications() {
       FetchMedicationTask medicationTask = new FetchMedicationTask();
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //String location = "321231";
        medicationTask.execute("65445");
    }

    public static MedicationFragment newInstance (int sectionName){
        MedicationFragment medicationFragment = new MedicationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER,sectionName);
        medicationFragment.setArguments(args);
        return medicationFragment;
    }



    @Override
    public void onStart() {
        super.onStart();
        //updatePatient();
    }

    @Override
    public void onRefresh() {
        updateMedications();

    }

    public class FetchMedicationTask extends AsyncTask<String , Void, List<Medication>> {

        private final String LOG_TAG = FetchMedicationTask.class.getSimpleName();
        private StringBuffer buffer;

        @Override

            protected ArrayList<Medication> doInBackground(String... params) {
            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            final String FORMAT = "?_format=json";
            final String PATIENT_ID = "patient=21613";
            InputStream inputStream = null;
            //List<Medication> entries = null;
            int numDays = 7;

            try {

                URL url = new URL("http://fhirtest.uhn.ca/baseDstu2/MedicationOrder?"+PATIENT_ID+"&_format=json&_raw=true");
                Log.v(LOG_TAG, url.toString());

                inputStream = downloadUrl(url.toString());
                buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                Log.v(LOG_TAG, "Medication string: " + buffer.toString());


            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                MyJsonParser jsonParser = new MyJsonParser();
                try {
                    mMedicationArray = jsonParser.parseJSON(buffer.toString());
                    Log.i("MedFragment" , mMedicationArray.get(0).getDosageInstructions());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
            }
            return mMedicationArray;


        }
        private InputStream downloadUrl(String urlString) throws IOException {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            InputStream stream = conn.getInputStream();
            return stream;
        }
    }
}
