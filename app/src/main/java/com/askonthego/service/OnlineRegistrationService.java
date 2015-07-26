package com.askonthego.service;

import com.askonthego.domain.Participant;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class OnlineRegistrationService {

  private RestAdapter restAdapter;

  public OnlineRegistrationService(RestAdapter restAdapter) {
    this.restAdapter = restAdapter;
  }

  public void register(final String participantId, final String password, final Callback<Token> callback) {
    final Credentials credentials = new Credentials(participantId, password);
    final UserService userService = restAdapter.create(UserService.class);
    userService.createUser(credentials, new Callback<Void>() {
      @Override
      public void success(Void ignore, Response response) {
        authenticate(userService, credentials, participantId, callback);
      }

      @Override
      public void failure(RetrofitError error) {
        callback.failure(error);
      }
    });
  }

  private void authenticate(UserService userService, Credentials credentials, final String participantId, final Callback<Token> callback) {
    userService.authenticate(credentials, new Callback<Token>() {
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
