package com.emyyn.riley.nypmedicationsmonitor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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
public class MedicationFragment extends Fragment{


    public MedicationFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    private MedicationAdapter<Medication> mMedicationAdapter;
    private static final String ARG_SECTION_NUMBER = "section_number";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
            FetchMedicationTask fetchMedication = new FetchMedicationTask();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            Log.i("Refresh", "Refreshed");
            fetchMedication.execute();
        updateMedications();
            return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("OnCreate", "Before the Async Task");
        updateMedications();
        Log.i("OnCreate", "After the Async Task");
        final View rootView = inflater.inflate(R.layout.fragment_med_list, container, false);
        Log.i("OnCreate", "After the View");
        ArrayList<Medication> arrayOfMedications = new ArrayList<Medication>();
        mMedicationAdapter = new MedicationAdapter(this.getContext(), arrayOfMedications);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_medications);
        listView.setAdapter(mMedicationAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Medication meds = mMedicationAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }



    private void updateMedications() {
       FetchMedicationTask medicationTask = new FetchMedicationTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //String location = "321231";
        medicationTask.execute();
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

    public class FetchMedicationTask extends AsyncTask<String, Void, List<Medication>> {

        private final String LOG_TAG = FetchMedicationTask.class.getSimpleName();
        private StringBuffer buffer;

        @Override
        protected List<Medication> doInBackground(String... params) {

            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            final String FORMAT = "?_format=json";
            final String PATIENT_ID = "patient=Tbt3KuCY0B5PSrJvCu2j-PlK.aiHsu2xUjUM8bWpetXoB";
            InputStream inputStream = null;
            List<Medication> entries = null;
            int numDays = 7;

            try {

                URL url = new URL("https://open-ic.epic.com/FHIR/api/FHIR/DSTU2/MedicationOrder?"+PATIENT_ID+"&_format=json");
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
                    entries = jsonParser.parseJSON(buffer.toString());
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
            return entries;


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
