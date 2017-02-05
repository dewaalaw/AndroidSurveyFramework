package com.askonthego.service;

import android.util.Log;

import com.askonthego.domain.Assessment;
import com.couchbase.lite.CouchbaseLiteException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AssessmentUploader {

    private RestAssessmentService restAssessmentService;
    private AssessmentDAO assessmentDAO;
    private Preferences preferences;
    private ObjectMapper objectMapper = new ObjectMapper();

    public AssessmentUploader(Preferences preferences, RestAssessmentService restAssessmentService, AssessmentDAO assessmentDAO) {
        this.preferences = preferences;
        this.assessmentDAO = assessmentDAO;
        this.restAssessmentService = restAssessmentService;
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public synchronized void uploadAssessments(List<Assessment> assessments, final Callback<Void> responseHandler) {
        for (Assessment assessment : assessments) {
            postAssessment(assessment, responseHandler);
        }
    }

    private synchronized void postAssessment(final Assessment assessment, final Callback<Void> callback) {
        Log.d(getClass().getName(), "Syncing assessment for participant " + assessment.getParticipant().getId() + ", survey " + assessment.getSurveyName() + ", startDate " + assessment.getStartDate());
        restAssessmentService.postAssessment(preferences.getApiToken(), assessment, new Callback<Void>() {
            @Override
            public void success(Void aVoid, Response response) {
                try {
                    assessment.setSynced(true);
                    assessmentDAO.updateDocument(assessment);
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
