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
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Riley on 4/14/2016.
 */
public class MedicationFragment extends Fragment{

    private DisplayAdapter adapter;

    public MedicationFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    private ArrayAdapter<String> mForecastAdapter;
    private static final String ARG_SECTION_NUMBER = "section_number";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            Log.i("Refresh", "Refreshed");
            weatherTask.execute();
            updatePatient();
            return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mForecastAdapter =
                new ArrayAdapter<String>(
                        getActivity(), // The current context (this activity)
                        R.layout.list_item_details, // The name of the layout ID.
                        R.id.tv_title, // The ID of the textview to populate.
                        new ArrayList<String>());

        final View rootView = inflater.inflate(R.layout.fragment_item_list, container, false);
        return rootView;
    }

    public static List<Patient> getData(){
        List<Patient> data = new ArrayList<>();
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


    private void updatePatient() {
        FetchWeatherTask weatherTask = new FetchWeatherTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = "321231";
        weatherTask.execute(location);
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

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

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

        /**
         * Prepare the weather high/lows for presentation.
         */

        private String getJSONStringFromArray(JSONObject jsonObj, String array, String str) throws JSONException {
            String rtnString = null;
            JSONArray jsonArray = jsonObj.getJSONArray(array);
            for (int n = 0; n < jsonArray.length(); n++) {
                rtnString = jsonArray.getJSONObject(n).getString(str);
                //Log.i("JSONObj", "Json Str: " + rtnString);
            }
            return rtnString;
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getWeatherDataFromJson(String jsonStr, int numDays)
                throws JSONException, ParseException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "entry";
            final String OWM_resource = "resource";
            final String OWM_GENDER = "gender";
            final String OWM_BIRTHDATE = "birthDate";
            final String NAME = "name"; //array
            //final String OWM_DESCRIPTION = "main";

            JSONObject entryJson = new JSONObject(jsonStr);
            JSONArray patientArray = entryJson.getJSONArray(OWM_LIST);


            String[] resultStrs = new String[patientArray.length()];

            for (int i = 0; i < patientArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String dob = null;
                String names = null;
                String gender;
                String id;
                String str1;
                String str2;

                // Get the JSON object representing the day
                JSONObject patientObject = patientArray.getJSONObject(i);
                //log.i("JSONObj", "patObj: " + patientObject.toString());
                JSONObject resourceObject = patientObject.getJSONObject("resource");

                // description is in a child array called "weather", which is 1 element long.
                JSONArray nameArray = resourceObject.getJSONArray(NAME);
                for (int n = 0; n < nameArray.length(); n++) {

                    JSONObject nameObject = nameArray.getJSONObject(n);
                    str1 = (nameObject.getString("family"));
                    str2 = (nameObject.getString("given"));

                    //String str1 = getJSONStringFromArray(nameObject, "family", "family");
                    //String str2 = getJSONStringFromArray(nameObject, "given", "given");
                    names = stripBracket(str1) + ", " + stripBracket(str2);

                    // Temperatures are in a child object called "temp".  Try not to name variables
                    // "temp" when working with temperature.  It confuses everybody.
                    //JSONObject genderObject = resourceObject.getJSONObject(OWM_GENDER);
                    gender = resourceObject.getString(OWM_GENDER);
                    id = resourceObject.getString("id");
                    // JSONObject dobOject = resourceObject.getJSONObject(OWM_BIRTHDATE);
                    dob = resourceObject.getString(OWM_BIRTHDATE);
                    Date parsedDob = parseDate(dob);
                    int age = getAge(parsedDob);

                    resultStrs[i] = id;
                }

                for (String s : resultStrs) {
                    //Log.v(LOG_TAG, "Patient entry: " + s);
                }

            }
            return resultStrs;
        }


        @Override
        protected String[] doInBackground(String... params) {

            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
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

                URL url = new URL("https://navhealth.herokuapp.com/api/fhir/Patient/");
                //Log.v(LOG_TAG, "Built URI: https://navhealth.herokuapp.com/api/fhir/Patient/");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
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
                forecastJsonStr = buffer.toString();

                //  Log.v(LOG_TAG, "Forecast string: " + forecastJsonStr);
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getWeatherDataFromJson(forecastJsonStr, numDays);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mForecastAdapter.clear();
                for (String dayForecastStr : result) {
                    mForecastAdapter.add(dayForecastStr);
                }
            }
        }
    }
}
