package com.askonthego.service;

import com.askonthego.domain.Assessment;
import com.askonthego.sdk.BetterFindCallback;
import com.askonthego.util.LogUtils;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AssessmentService {

  private RestAssessmentService restAssessmentService;
  private Preferences preferences;

  public AssessmentService(Preferences preferences, RestAssessmentService restAssessmentService) {
    this.preferences = preferences;
    this.restAssessmentService = restAssessmentService;
  }

  public synchronized void uploadUnsyncedAssessments(final Callback<Void> responseHandler) {
    ParseQuery<Assessment> query = ParseQuery.getQuery("Assessment");
    query.fromLocalDatastore()
         .whereEqualTo("synced", false)
         .include("responses")
         .findInBackground(new BetterFindCallback<Assessment>() {
           @Override
           public void onSuccess(List<Assessment> assessments) {
             for (Assessment assessment : assessments) {
               postAssessment(assessment, responseHandler);
             }
           }

           @Override
           protected void onFailure(ParseException e) {
             LogUtils.e(AssessmentService.class, "Error retrieving un-synced assessments.", e);
           }
         });
  }

  public void save(Assessment assessment, Callback<Void> callback) {
    assessment.pinInBackground();
    postAssessment(assessment, callback);
  }

  private synchronized void postAssessment(final Assessment assessment, final Callback<Void> callback) {
    LogUtils.d(AssessmentService.class, "Syncing assessment for participant " + assessment.getParticipant().getId() + ", survey " + assessment.getSurveyName() + ", startDate " + assessment.getAssessmentStartDate());
    restAssessmentService.postAssessment(preferences.getApiToken(), assessment, new Callback<Void>() {
      @Override
      public void success(Void aVoid, Response response) {
        assessment.setSynced(true);
        assessment.pinInBackground();
        callback.success(aVoid, response);
      }

      @Override
      public void failure(RetrofitError error) {
        callback.failure(error);
      }
    });
  }
}
