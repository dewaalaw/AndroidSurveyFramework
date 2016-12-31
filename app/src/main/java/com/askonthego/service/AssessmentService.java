package com.askonthego.service;

import android.util.Log;

import com.askonthego.domain.Assessment;
import com.askonthego.domain.AssessmentResponse;
import com.askonthego.domain.Participant;
import com.askonthego.util.LogUtils;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.Reducer;
import com.couchbase.lite.SavedRevision;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.View;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AssessmentService {

    private static final String ASSESSMENT_PROPERTY = "assessment";
    private static final String SYNCED_PROPERTY = "synced";
    private static final String ASSESSMENTS_VIEW_NAME = "assessments";

    private RestAssessmentService restAssessmentService;
    private Preferences preferences;
    private Database database;
    private ObjectMapper objectMapper = new ObjectMapper();

    public AssessmentService(Preferences preferences, RestAssessmentService restAssessmentService, Database database) {
        this.preferences = preferences;
        this.restAssessmentService = restAssessmentService;
        this.database = database;
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private List<Assessment> getUnsyncedAssessments() throws CouchbaseLiteException {
        View assessmentsView = database.getView(ASSESSMENTS_VIEW_NAME);
        assessmentsView.setMapReduce(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {
                Map<String, Object> map = (Map<String, Object>) document.get(ASSESSMENT_PROPERTY);
                boolean synced = (boolean) map.get(SYNCED_PROPERTY);
                if (!synced) {
                    emitter.emit(ASSESSMENT_PROPERTY, map);
                }
            }
        }, new Reducer() {
            @Override
            public Object reduce(List<Object> keys, List<Object> values, boolean rereduce) {
                return new Integer(values.size());
            }
        }, "1");

        Query query = assessmentsView.createQuery();
        query.setMapOnly(true);
        QueryEnumerator result = query.run();

        Log.d(getClass().getName(), "Retrieved " + result.getCount() + " records!!!!");

        ArrayList<Assessment> assessments = new ArrayList<>();
        for (Iterator<QueryRow> it = result; it.hasNext(); ) {
            QueryRow row = it.next();
            Assessment convertedAssessment = objectMapper.convertValue(row.getValue(), Assessment.class);
            convertedAssessment.setDocumentId(row.getDocumentId());
            assessments.add(convertedAssessment);
        }

        return assessments;
    }

    public synchronized void uploadUnsyncedAssessments(final Callback<Void> responseHandler) {
        try {
            List<Assessment> unsyncedAssessments = getUnsyncedAssessments();
            for (Assessment assessment : unsyncedAssessments) {
                postAssessment(assessment, responseHandler);
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public void save(Assessment assessment, Callback<Void> callback) {
        try {
            String documentId = createDocument(database, assessment);
            Log.d(getClass().getName(), "Saved document " + documentId);
            assessment.setDocumentId(documentId);
        } catch (CouchbaseLiteException e) {
            Log.e(getClass().getName(), "Error saving assessment", e);
        }
        postAssessment(assessment, callback);
    }

    private String createDocument(Database database, final Assessment assessment) throws CouchbaseLiteException {
        Document document = database.createDocument();
        Map<String, Object> map = new HashMap<>();
        map.put(ASSESSMENT_PROPERTY, assessment);
        document.putProperties(map);
        return document.getId();
    }

    private void updateDocument(Database database, final Assessment assessment) throws CouchbaseLiteException {
        Document document = database.getDocument(assessment.getDocumentId());
        document.update(new Document.DocumentUpdater() {
            @Override
            public boolean update(UnsavedRevision newRevision) {
                Map<String, Object> properties = newRevision.getUserProperties();
                properties.put(ASSESSMENT_PROPERTY, assessment);
                newRevision.setUserProperties(properties);
                return true;
            }
        });
    }

    private synchronized void postAssessment(final Assessment assessment, final Callback<Void> callback) {
        LogUtils.d(AssessmentService.class, "Syncing assessment for participant " + assessment.getParticipant().getId() + ", survey " + assessment.getSurveyName() + ", startDate " + assessment.getStartDate());
        restAssessmentService.postAssessment(preferences.getApiToken(), assessment, new Callback<Void>() {
            @Override
            public void success(Void aVoid, Response response) {
                try {
                    assessment.setSynced(true);
                    updateDocument(database, assessment);
                    /*
                     * TODO - if there is a conflict when updating the document or any other error occurs,
                     * the following callback will not be invoked. There needs to be a more generic way of providing
                     * a success/failure callback for non-http errors that can occur.
                     */
                    callback.success(aVoid, response);
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }
}
