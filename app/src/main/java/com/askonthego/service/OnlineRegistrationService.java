package com.askonthego.service;

import com.askonthego.domain.Participant;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

public class OnlineRegistrationService {

  private static final String REGISTRATION_URL = ServiceConstants.API_BASE_URL + "/participant";

  public void register(final String participantId, final String password, final JsonHttpResponseHandler responseHandler) {
    AsyncHttpClient client = new AsyncHttpClient();
    RequestParams requestParams = new RequestParams();
    requestParams.put("participantId", participantId);
    requestParams.put("password", password);
    client.post(REGISTRATION_URL, requestParams, new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        Participant participant = new Participant();
        participant.setId(participantId);
        Participant.setActiveParticipant(participant);

        responseHandler.onSuccess(statusCode, headers, response);
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        responseHandler.onFailure(statusCode, headers, throwable, errorResponse);
      }
    });
  }
}
