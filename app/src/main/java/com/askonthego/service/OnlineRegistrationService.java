package com.askonthego.service;

import com.askonthego.domain.Participant;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class OnlineRegistrationService {

    private RestUserService restUserService;
    private ParticipantService participantService;

    public OnlineRegistrationService(RestUserService restUserService, ParticipantService participantService) {
        this.restUserService = restUserService;
        this.participantService = participantService;
    }

    public void register(final Credentials credentials, final Callback<Token> callback) {
        restUserService.createUser(credentials, new Callback<Void>() {
            @Override
            public void success(Void ignore, Response response) {
                authenticate(credentials, callback);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }

    private void authenticate(final Credentials credentials, final Callback<Token> callback) {
        restUserService.authenticate(credentials, new Callback<Token>() {
            @Override
            public void success(Token token, Response response) {
                Participant participant = new Participant();
                participant.setId(credentials.getUsername());
                participantService.setActiveParticipant(participant);
                callback.success(token, response);
            }

            @Override
            public void failure(RetrofitError error) {
                callback.failure(error);
            }
        });
    }
}
