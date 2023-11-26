package com.example.interlang;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import dev.langchain4j.agent.tool.Tool;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Range;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.SimpleQuantity;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointUse;
import org.hl7.fhir.r4.model.Goal;
import org.hl7.fhir.r4.model.Observation;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;

import org.apache.http.Header;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;

@Service
public class FhirTools {
    private static final FhirContext fhirContext = FhirContext.forR4();
    private final IGenericClient client;

    public FhirTools(@Value("${fhir.server.url}") String fhirServerUrl,
            @Value("${fhir.api.key}") String apiKey) {
        // Create headers for the API key
        Header header = new BasicHeader("x-api-key", apiKey);
        ArrayList<Header> headers = new ArrayList<>();
        headers.add(header);

        // Create an HTTP client with the headers
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setDefaultHeaders(headers);
        CloseableHttpClient httpClient = builder.build();

        // Set the HTTP client to the FhirContext
        fhirContext.getRestfulClientFactory().setHttpClient(httpClient);

        this.client = fhirContext.newRestfulGenericClient(fhirServerUrl);
    }

    @Tool("Fetches a FHIR Patient resource using a GET request by ID")
    Patient getPatientById(String patientId) {
        return client.read()
                .resource(Patient.class)
                .withId(patientId)
                .execute();
    }

    @Tool("Creates a new patient with a family name and given name")
    String createPatient(String familyName, String givenName) {
        Patient patient = new Patient();
        patient.addName().setFamily(familyName).addGiven(givenName);

        MethodOutcome outcome = client.create().resource(patient).execute();
        return outcome.getId().getIdPart();
    }

    @Tool("Searches for a patient by family and given name")
    Patient searchPatientByName(String familyName, String givenName) {
        Bundle results = client.search().forResource(Patient.class)
                .where(Patient.FAMILY.matches().value(familyName))
                .and(Patient.GIVEN.matches().value(givenName))
                .returnBundle(Bundle.class).execute();

        if (!results.getEntry().isEmpty()) {
            return (Patient) results.getEntryFirstRep().getResource();
        }
        return null;
    }

    @Tool("Updates patient's contact information")
    void updatePatientContactInfo(String patientId, String phoneNumber) {
        Patient patient = client.read().resource(Patient.class).withId(patientId).execute();
        patient.addTelecom().setSystem(ContactPointSystem.PHONE).setUse(ContactPointUse.HOME).setValue(phoneNumber);

        client.update().resource(patient).execute();
    }

    @Tool("Creates an observation for a patient")
    String createObservation(String patientId, double value, String unit) {
        Observation observation = new Observation();
        observation.setStatus(Observation.ObservationStatus.FINAL);
        observation.setSubject(new Reference("Patient/" + patientId));
        observation.setValue(new Quantity().setValue(value).setUnit(unit));

        MethodOutcome outcome = client.create().resource(observation).execute();
        return outcome.getId().getIdPart();
    }

    @Tool("Fetches goal data for a patient with Id")
    Bundle getGoalData(String patientId) {
        return client.search()
                .forResource(Goal.class)
                .where(Goal.PATIENT.hasId(patientId))
                .returnBundle(Bundle.class)
                .execute();
    }

    @Tool("Inserts a single goal with goalKey and goalValue for a patient with an ID")
    int insertSingleGoalData(String patientId, String goalKey, String goalValue) {
        Bundle bundle = new Bundle();
        bundle.setType(Bundle.BundleType.BATCH);

        Goal goal = new Goal();
        goal.setLifecycleStatus(Goal.GoalLifecycleStatus.ACTIVE);
        goal.setSubject(new Reference("Patient/" + patientId));

        String[] rangeValues = goalValue.split("-");
        Goal.GoalTargetComponent target = new Goal.GoalTargetComponent();

        String unit = determineUnit(goalKey);
        SimpleQuantity low = (SimpleQuantity) new SimpleQuantity().setValue(Double.parseDouble(rangeValues[0]))
                .setUnit(unit);
        SimpleQuantity high = (SimpleQuantity) new SimpleQuantity().setValue(Double.parseDouble(rangeValues[1]))
                .setUnit(unit);

        target.setDetail(new Range().setLow(low).setHigh(high));
        target.setMeasure(new CodeableConcept(new Coding("http://loinc.org", "3141-9", "")));
        goal.addTarget(target);

        goal.setDescription(new CodeableConcept().setText(goalKey));

        Bundle.BundleEntryComponent entryComponent = bundle.addEntry();
        entryComponent.setResource(goal);
        entryComponent.getRequest().setMethod(Bundle.HTTPVerb.POST).setUrl("Goal");

        Bundle response = client.transaction().withBundle(bundle).execute();

        int count = 0;
        for (Bundle.BundleEntryComponent respEntry : response.getEntry()) {
            if (respEntry.getResponse().getStatus().startsWith("201")) {
                count++;
            }
        }
        return count;
    }

    private String determineUnit(String goalKey) {
        switch (goalKey) {
            case "sleep":
                return "hrs";
            case "steps":
                return "cnt";
            case "heartRate":
                return "bpm";
            default:
                return "%"; // Default unit
        }
    }
}
