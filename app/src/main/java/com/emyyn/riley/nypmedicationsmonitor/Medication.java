package com.emyyn.riley.nypmedicationsmonitor;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

import static com.emyyn.riley.nypmedicationsmonitor.MyJsonParser.parseDate;

/**
 * Created by Riley on 4/16/2016.
 */
public class Medication {
    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getExpectedSupplyDuration() {
        return expectedSupplyDuration;
    }

    public void setExpectedSupplyDuration(String medicationOrder) {
        this.expectedSupplyDuration = medicationOrder;
    }

    public String getMedicationReference() {
        return medicationReference;
    }

    public void setMedicationReference(String medicationReference) {
        this.medicationReference = medicationReference;
    }

    public String getDispenseRequest() {
        return dispenseRequest;
    }

    public void setDispenseRequest(String dispenseRequest) {
        this.dispenseRequest = dispenseRequest;
    }

    public String getDosageInstructions() {
        return dosageInstructions;
    }

    public void setDosageInstructions(String dosageInstructions) {
        this.dosageInstructions = dosageInstructions;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getTiming() {
        return timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }

    public String getDoseQuantity() {
        return doseQuantity;
    }

    public void setDoseQuantity(String doseQuantity) {
        this.doseQuantity = doseQuantity;
    }

    public  String quantity;
    public  String expectedSupplyDuration ;
    public  String medicationReference;

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getPeriodUnits() {
        return periodUnits;
    }

    public void setPeriodUnits(String periodUnits) {
        this.periodUnits = periodUnits;
    }

    public  String dispenseRequest;
    public String dosageInstructions;
    public  String route ;
    public  String method;
    public String timing;
    public int frequency;
    public int period;
    public String periodUnits;
    public  String doseQuantity;

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Date start;
    public Date end;

    public Medication(String quantity, String expectedSupplyDuration, String medicationReference, String dispenseRequest, String dosageInstructions, String route, String method, String timing, String doseQuantity, int period, int frequency, String periodUnits, Date e, Date s) {
        this.quantity = quantity;
        this.expectedSupplyDuration  = expectedSupplyDuration ;
        this.medicationReference = medicationReference;
        this.dispenseRequest = dispenseRequest;
        this.dosageInstructions = dosageInstructions;
        this.route = route;
        this.method = method;
        this.timing = timing;
        this.doseQuantity = doseQuantity;
        this.frequency = frequency;
        this.period = period;
        this.periodUnits = periodUnits;
        this.start = s;
        this.end = e;
    }

    public String getMedicationReference(String medicationReference){
        return medicationReference;
    }

    public Medication() {

    }
    public static Medication fromJson (JSONObject jsonObject) throws JSONException, ParseException {
        Medication m = new Medication();
        String tempStr;
        String d = "display";
        String t = "text";
        String v = "value";

        try
        {
            //Log.i("JSONObj", "medObj: " + jsonObject.toString());
            //JSONObject resourceObject = jsonObject.getJSONObject("resource");
            //This action is replaced with the method getObject which takes the JsonObject and returns its display value
            //tempStr = jsonObject.getJSONObject("medicationReference").getString("display");
            //Log.i("JSONObj", "medObj: " + jsonObject.getJSONObject("medicationReference").toString());
           // Log.i("JSONObj", "medOrderObj: " + tempStr);

            m.setMedicationReference(getMedicationObject(jsonObject, "medicationReference", d));
            //m.setDosageInstructions(getMedicationObject(jsonObject, "dosageInstructions", t));
            JSONObject dispenseObj = jsonObject.getJSONObject("dispenseRequest");
            m.setExpectedSupplyDuration(getMedicationObject(dispenseObj, "expectedSupplyDuration", v));
            m.setQuantity(getMedicationObject(dispenseObj, "quantity", v));
            //Returns the start and end state for the drug not to be more than 1 year
            String start = (getMedicationObject(dispenseObj, "validityPeriod", "start"));
            String end = (getMedicationObject(dispenseObj, "validityPeriod", "end"));
            m.setStart(parseDate(start));
            m.setEnd(parseDate(end));

           //Handler the Array for Dosage Instructions
            JSONArray dosageArray = jsonObject.getJSONArray("dosageInstruction");
            JSONObject dosageObject = dosageArray.getJSONObject(0);
            m.setRoute(getMedicationObject(dosageObject, "route", t));
            m.setMethod(getMedicationObject(dosageObject, "method", t));
            //Handler for the Object Timing to return the FREQUENCY, PERIOD, and PERIODUNITS
            JSONObject timingObj = dosageObject.getJSONObject("timing");
            m.setPeriodUnits(getMedicationObject(timingObj, "repeat", "periodUnits"));
            m.setFrequency(Integer.parseInt(getMedicationObject(timingObj, "repeat", "frequency")));
            int x = (int)(Double.parseDouble(getMedicationObject(timingObj, "repeat", "period")));
            m.setPeriod(x);



        }catch (JSONException e){
            e.printStackTrace();
        }
        return m;
    }

    public static String getMedicationObject(JSONObject j, String resource, String value) throws JSONException {
        String tempStr;
        tempStr = j.getJSONObject(resource).getString(value);
        return tempStr;
    }
}
