package com.askonthego.service;

import com.askonthego.domain.Participant;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class OnlineRegistrationService {

  private RestUserService restUserService;

  public OnlineRegistrationService(RestUserService restUserService) {
    this.restUserService = restUserService;
  }

  public void register(final String participantId, final String password, final Callback<Token> callback) {
    final Credentials credentials = new Credentials(participantId, password);
    restUserService.createUser(credentials, new Callback<Void>() {
      @Override
      public void success(Void ignore, Response response) {
        authenticate(restUserService, credentials, participantId, callback);
      }

      @Override
      public void failure(RetrofitError error) {
        callback.failure(error);
      }
    });
  }

  private void authenticate(RestUserService restUserService, Credentials credentials, final String participantId, final Callback<Token> callback) {
    restUserService.authenticate(credentials, new Callback<Token>() {
      @Override
      public void success(Token token, Response response) {
        Participant participant = new Participant();
        participant.setId(participantId);
        Participant.setActiveParticipant(participant);
        callback.success(token, response);
      }

      @Override
      public void failure(RetrofitError error) {
        callback.failure(error);
      }
    });
  }
}
