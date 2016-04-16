package com.emyyn.riley.nypmedicationsmonitor;

/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * This class parses XML feeds from stackoverflow.com.
 * Given an InputStream representation of a feed, it returns a List of entries,
 * where each list element represents a single entry (post) in the XML feed.
 */
public class MyJsonParser {

    private ArrayList<Medication> medicationArrayList;

    public ArrayList<Medication> parseJSON (String jsonStr) throws JSONException, IOException, ParseException {
        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "entry";
        final String OWM_resource = "quantity";
        final String OWM_GENDER = "gender";
        final String OWM_BIRTHDATE = "birthDate";
        final String NAME = "name"; //array
        //final String OWM_DESCRIPTION = "main";

        JSONObject entryJson = new JSONObject(jsonStr);
        JSONArray patientArray = entryJson.getJSONArray(OWM_LIST);
        medicationArrayList = new ArrayList<Medication>(patientArray.length());

        //String[] resultStrs = new String[patientArray.length()];

        for (int i = 0; i < patientArray.length(); i++) {

            // Get the JSON object representing the first medication
            JSONObject medicationObject = patientArray.getJSONObject(i);
            JSONObject resourceObject = medicationObject.getJSONObject("resource");

            Medication medication = Medication.fromJson(resourceObject);
            if (medication != null) {
                medicationArrayList.add(medication);
            }


        }
        for (Medication m : medicationArrayList)
        {
            Log.i("Medication", m.getDosageInstructions());
        }
        return medicationArrayList;
    }




    private String getJSONStringFromArray(JSONObject jsonObj, String array, String str) throws JSONException {
        String rtnString = null;
        JSONArray jsonArray = jsonObj.getJSONArray(array);
        for (int n = 0; n < jsonArray.length(); n++) {
            rtnString = jsonArray.getJSONObject(n).getString(str);
            //Log.i("JSONObj", "Json Str: " + rtnString);
        }
        return rtnString;
    }
    public static Date parseDate(String dateTimeStr)
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

}


