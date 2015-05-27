package com.example.jaf50.survey.service;

import android.content.Context;
import android.util.Log;

import com.example.jaf50.survey.domain.Assessment;
import com.example.jaf50.survey.parse.sdk.BetterFindCallback;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class AssessmentService {

  private static final String ASSESSMENT_SAVE_URL = "http://104.236.108.215:8080/api/assessments";

  public void uploadUnsyncedAssessments(final Context context, final AsyncHttpResponseHandler responseHandler) {
    ParseQuery<Assessment> query = ParseQuery.getQuery("Assessment");
    query.fromLocalDatastore()
         .whereEqualTo("synced", false)
         .include("responses")
         .findInBackground(new BetterFindCallback<Assessment>() {
           @Override
           public void onSuccess(List<Assessment> assessments) {
             for (Assessment assessment : assessments) {
               postAssessment(assessment, context, responseHandler);
             }
           }
           @Override
           protected void onFailure(ParseException e) {
             Log.e(AssessmentService.class.getName(), "Error retrieving un-synced assessments.", e);
           }
         });
  }

  public void save(final Assessment assessment, final Context context, final AsyncHttpResponseHandler responseHandler) {
    assessment.pinInBackground();
    postAssessment(assessment, context, responseHandler);
  }

  private void postAssessment(Assessment assessment, Context context, AsyncHttpResponseHandler responseHandler) {
    ResponseHandlerDecorator responseHandlerDecorator = new ResponseHandlerDecorator(responseHandler, assessment);
    try {
      Log.d(AssessmentService.class.getName(), "Syncing assessment for participant " + assessment.getParticipant().getUsername() + ", survey " + assessment.getSurveyName() + ", startDate " + assessment.getAssessmentStartDate());

      AsyncHttpClient client = new AsyncHttpClient();
      String json = DomainSerializationService.toJson(assessment);
      StringEntity entity = new StringEntity(json);
      entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
      client.post(context, ASSESSMENT_SAVE_URL, entity, "application/json", responseHandlerDecorator);
    } catch (JSONException e) {
      Log.e(AssessmentService.class.getName(), "Error posting assessment: ", e);
      responseHandlerDecorator.onFailure(500, null, "Error syncing assessment".getBytes(), e);
    } catch (UnsupportedEncodingException e) {
      Log.e(AssessmentService.class.getName(), "Error posting assessment: ", e);
      responseHandlerDecorator.onFailure(500, null, "Error syncing assessment".getBytes(), e);
    }
  }

  private class ResponseHandlerDecorator extends AsyncHttpResponseHandler {
    private AsyncHttpResponseHandler parentHandler;
    private Assessment assessment;

    ResponseHandlerDecorator(AsyncHttpResponseHandler parentHandler, Assessment assessment) {
      this.parentHandler = parentHandler;
      this.assessment = assessment;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
      parentHandler.onSuccess(statusCode, headers, responseBody);
      assessment.setSynced(true);
      assessment.pinInBackground();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
      parentHandler.onFailure(statusCode, headers, responseBody, error);
    }
  }
}
