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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

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
import java.util.Calendar;
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

    private ArrayAdapter<String> mMedicationAdapter;
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
            updatePatient();
            return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        List<String> patientArray = getData();
        mMedicationAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_details, // The name of the layout ID.
                        R.id.list_item_medication_textview, // The ID of the textview to populate.
                        patientArray);
        updatePatient();
        final View rootView = inflater.inflate(R.layout.fragment_med_list, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_medications);
        listView.setAdapter(mMedicationAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String forecast = mMedicationAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, forecast);
                startActivity(intent);
            }
        });
        return rootView;
    }

    public static List<String> getData(){

        String[] title = {"Lusie","David","Alex","Danielle","Courtney","Tom","Greg","Martha","Robert","Bethany","Clare","Annie","Anna"};
        List<String> data = new ArrayList<String>(Arrays.asList(title));

        String[] dob = {"3/28/1990","1/14/2015","2/5/1989","5/9/1972","5/21/1991","5/17/1964","7/22/1954","6/18/1964","8/11/2006","6/26/1998","7/27/2000","11/28/1989","12/6/1987"};
        String[] id = {"654654654654654","32158497365241654","6359879841321489432","89746513214594923","97484562132165869","98762131231195674","987653213213165479451321","8979654635216584984","89746543215649421","9874651321654984621968","6549879413216846518","32134896574913198645","897961321354823158"};

        return data;
    }


    private void updatePatient() {
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

    public class FetchMedicationTask extends AsyncTask<String, Void, List<XmlParser.Entry>> {

        private final String LOG_TAG = FetchMedicationTask.class.getSimpleName();

        private String stripBracket(String str) {
            String newStr = null;
            newStr = str.replaceAll("[^\\p{L}\\p{Z}]", " ");
            return newStr;
        }

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

        public int getAge(Date dateOfBirth) {

            Calendar today = Calendar.getInstance();
            Calendar birthDate = Calendar.getInstance();

            int age = 0;

            birthDate.setTime(dateOfBirth);
            if (birthDate.after(today)) {
                throw new IllegalArgumentException("Can't be born in the future");
            }

            age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

            // If birth date is greater than todays date (after 2 days adjustment of leap year) then decrement age one year
            if ((birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3) ||
                    (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH))) {
                age--;

                // If birth date and todays date are of same month and birth day of month is greater than todays day of month then decrement age
            } else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH)) &&
                    (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH))) {
                age--;
            }

            return age;
        }

        private String[] getUsableStringsFromJSON(String jsonStr, int numDays)
                throws JSONException, ParseException {

            JSONObject wholeJson = new JSONObject(jsonStr);
            JSONArray resourceArray = wholeJson.getJSONArray("entry");

            String[] resultStrs = new String[resourceArray.length()];

            for (int i = 0; i < resourceArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String dateWritten = null;
                String status = null;
                String medicationReference = null;
                String id;
                String str1;
                String str2;

                // Get the JSON object representing the day
                JSONObject patientObject = resourceArray.getJSONObject(i);
                //log.i("JSONObj", "patObj: " + patientObject.toString());
                JSONObject resourceObject = patientObject.getJSONObject("resource");

                // description is in a child array called "weather", which is 1 element long.
                JSONObject medicationObject = resourceObject.getJSONObject("medicationReference");
                medicationReference = medicationObject.getString("display");
                Log.i("Medication Resource", medicationReference);

//                for (int n = 0; n < nameArray.length(); n++) {
//
//                    JSONObject nameObject = nameArray.getJSONObject(n);
//                    str1 = (nameObject.getString("family"));
//                    str2 = (nameObject.getString("given"));
//
//                    //String str1 = getJSONStringFromArray(nameObject, "family", "family");
//                    //String str2 = getJSONStringFromArray(nameObject, "given", "given");
//                    names = stripBracket(str1) + ", " + stripBracket(str2);
//
//                    // Temperatures are in a child object called "temp".  Try not to name variables
//                    // "temp" when working with temperature.  It confuses everybody.
//                    //JSONObject genderObject = resourceObject.getJSONObject(OWM_GENDER);
//                    gender = resourceObject.getString(OWM_GENDER);
//                    id = resourceObject.getString("id");
//                    // JSONObject dobOject = resourceObject.getJSONObject(OWM_BIRTHDATE);
//                    dob = resourceObject.getString(OWM_BIRTHDATE);
//                    Date parsedDob = parseDate(dob);
//                    int age = getAge(parsedDob);
//
//                    resultStrs[i] = id;
//                }

                for (String s : resultStrs) {
                    //Log.v(LOG_TAG, "Patient entry: " + s);
                }

            }
            return resultStrs;
        }


        @Override
        protected List<XmlParser.Entry> doInBackground(String... params) {

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
            InputStream medicationJSONStr = null;
            List<XmlParser.Entry> entries = null;
            int numDays = 7;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
/*                final String FORECAST_BASE_URL =
                        "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .appendQueryParameter(APPID_PARAM, "0c6377fa7ca5f68908c6c836e7d5b181")
                        .build();*/

                //URL url = new URL(builtUri.toString());

                URL url = new URL("https://open-ic.epic.com/FHIR/api/FHIR/DSTU2/MedicationOrder?patient=Tbt3KuCY0B5PSrJvCu2j-PlK.aiHsu2xUjUM8bWpetXoB");
                Log.v(LOG_TAG, url.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();

                medicationJSONStr = inputStream;

                Log.v(LOG_TAG, "Medication string: " + medicationJSONStr);


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
                    entries = myparser.parseXml(medicationJSONStr);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
            }
            return entries;
        }


        @Override
        protected void onPostExecute(List<XmlParser.Entry> result) {
            if (result != null) {
                mMedicationAdapter.clear();
                for (XmlParser.Entry dayForecastStr : result) {
                    mMedicationAdapter.add(dayForecastStr);
                }
            }
        }
    }
}
