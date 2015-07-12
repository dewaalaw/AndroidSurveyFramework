package com.askonthego.service;

import android.content.Context;

import com.askonthego.domain.Assessment;
import com.askonthego.sdk.BetterFindCallback;
import com.askonthego.util.LogUtils;
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

  private static final String ASSESSMENT_SAVE_URL = ServiceConstants.API_BASE_URL + "/assessments";
  private DomainSerializationService domainSerializationService;

  public AssessmentService(DomainSerializationService domainSerializationService) {
    this.domainSerializationService = domainSerializationService;
  }

  public synchronized void uploadUnsyncedAssessments(final Context context, final AsyncHttpResponseHandler responseHandler) {
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
             LogUtils.e(AssessmentService.class, "Error retrieving un-synced assessments.", e);
           }
         });
  }

  public void save(final Assessment assessment, final Context context, final AsyncHttpResponseHandler responseHandler) {
    assessment.pinInBackground();
    postAssessment(assessment, context, responseHandler);
  }

  private synchronized void postAssessment(Assessment assessment, Context context, AsyncHttpResponseHandler responseHandler) {
    ResponseHandlerDecorator responseHandlerDecorator = new ResponseHandlerDecorator(responseHandler, assessment);
    try {
      LogUtils.d(AssessmentService.class, "Syncing assessment for participant " + assessment.getParticipant().getId() + ", survey " + assessment.getSurveyName() + ", startDate " + assessment.getAssessmentStartDate());

      AsyncHttpClient client = new AsyncHttpClient();
      String json = domainSerializationService.toJson(assessment);
      StringEntity entity = new StringEntity(json);
      entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
      client.post(context, ASSESSMENT_SAVE_URL, entity, "application/json", responseHandlerDecorator);
    } catch (JSONException e) {
      LogUtils.e(AssessmentService.class, "Error posting assessment: ", e);
      responseHandlerDecorator.onFailure(500, null, "Error syncing assessment".getBytes(), e);
    } catch (UnsupportedEncodingException e) {
      LogUtils.e(AssessmentService.class, "Error posting assessment: ", e);
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
      assessment.setSynced(true);
      assessment.pinInBackground();
      parentHandler.onSuccess(statusCode, headers, responseBody);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
      parentHandler.onFailure(statusCode, headers, responseBody, error);
    }
  }
}
