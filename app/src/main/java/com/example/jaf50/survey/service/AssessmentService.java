package com.example.jaf50.survey.service;

import android.content.Context;
import android.util.Log;

import com.example.jaf50.survey.domain.Assessment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;

public class AssessmentService {

  private static final String ASSESSMENT_SAVE_URL = "http://104.236.108.215:8080/api/assessments";

  public void save(Assessment assessment, Context context, AsyncHttpResponseHandler responseHandler) {
    try {
      assessment.pinInBackground();

      AsyncHttpClient client = new AsyncHttpClient();
      String json = DomainSerializationService.toJson(assessment);
      StringEntity entity = new StringEntity(json);
      entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
      client.post(context, ASSESSMENT_SAVE_URL, entity, "application/json", responseHandler);
    } catch (JSONException e) {
      Log.e(AssessmentService.class.getName(), "Error posting assessment: ", e);
    } catch (UnsupportedEncodingException e) {
      Log.e(AssessmentService.class.getName(), "Error posting assessment: ", e);
    }
  }
}
