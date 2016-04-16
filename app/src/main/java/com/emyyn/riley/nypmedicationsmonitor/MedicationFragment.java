package com.emyyn.riley.nypmedicationsmonitor;

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

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
            FetchMedicationTask weatherTask = new FetchMedicationTask();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            Log.i("Refresh", "Refreshed");
            weatherTask.execute();
        updateMedications();
            return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        updateMedications();
        final View rootView = inflater.inflate(R.layout.fragment_med_list, container, false);
//        ArrayList<Medication> arrayOfMedications = new ArrayList<Medication>();
//        mMedicationAdapter = new MedicationAdapter(this.getContext(), arrayOfMedications);
//
//        ListView listView = (ListView) rootView.findViewById(R.id.listview_medications);
//        listView.setAdapter(mMedicationAdapter);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                Medication meds = mMedicationAdapter.getItem(position);
//                Intent intent = new Intent(getActivity(), DetailActivity.class);
//                startActivity(intent);
//            }
//        });
        return rootView;
    }

    public static List<String> getData(){

        String[] title = {"Lusie","David","Alex","Danielle","Courtney","Tom","Greg","Martha","Robert","Bethany","Clare","Annie","Anna"};
        List<String> data = new ArrayList<String>(Arrays.asList(title));

        String[] dob = {"3/28/1990","1/14/2015","2/5/1989","5/9/1972","5/21/1991","5/17/1964","7/22/1954","6/18/1964","8/11/2006","6/26/1998","7/27/2000","11/28/1989","12/6/1987"};
        String[] id = {"654654654654654","32158497365241654","6359879841321489432","89746513214594923","97484562132165869","98762131231195674","987653213213165479451321","8979654635216584984","89746543215649421","9874651321654984621968","6549879413216846518","32134896574913198645","897961321354823158"};

        return data;
    }


    private void updateMedications() {
       FetchMedicationTask medicationTask = new FetchMedicationTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = "321231";
        medicationTask.execute(location);
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
        private InputStream buffer;

        private Date parseDate(String dateTimeStr)
                throws ParseException {

            String s = dateTimeStr.replace("Z", "+00:00");
            try {
                s = s.substring(0, 22) + s.substring(23);
            } catch (IndexOutOfBoundsException e) {
                throw new ParseException("Invalid length", 0);
            }
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(s);
            //DateFormat df = new SimpleDateFormat("MMM dd, yyyy");
            //String returnDate = df.format(date);
            return date;
        }

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

                URL url = new URL("https://open-ic.epic.com/FHIR/api/FHIR/DSTU2/MedicationOrder?patient=Tbt3KuCY0B5PSrJvCu2j-PlK.aiHsu2xUjUM8bWpetXoB&_format=json");
                Log.v(LOG_TAG, url.toString());

                inputStream = downloadUrl(url.toString());

                Log.v(LOG_TAG, "Medication string: " + inputStream);


            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                XmlParser myparser = new XmlParser();
                try {
                    entries = myparser.parseJSON(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
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
