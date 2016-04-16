package com.emyyn.riley.nypmedicationsmonitor;

/**
 * Created by Riley on 4/16/2016.
 */
public class Medication {
    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getMedicationOrder() {
        return medicationOrder;
    }

    public void setMedicationOrder(String medicationOrder) {
        this.medicationOrder = medicationOrder;
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

    public  String resource;
    public  String medicationOrder;
    public  String medicationReference;
    public  String dispenseRequest;
    public String dosageInstructions;
    public  String route ;
    public  String method;
    public  String timing;
    public  String doseQuantity;

    public Medication(String resource, String medicationOrder, String medicationReference, String dispenseRequest, String dosageInstructions, String route, String method, String timing, String doseQuantity) {
        this.resource = resource;
        this.medicationOrder = medicationOrder;
        this.medicationReference = medicationReference;
        this.dispenseRequest = dispenseRequest;
        this.dosageInstructions = dosageInstructions;
        this.route = route;
        this.method = method;
        this.timing = timing;
        this.doseQuantity = doseQuantity;
    }

    public String getMedicationReference(String medicationReference){
        return medicationReference;
    }

    public Medication() {

    }
}
