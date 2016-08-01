package com.askonthego.service;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

public interface RestUserService {

    @POST("/users")
    void createUser(@Body Credentials credentials, Callback<Void> cb);

    @POST("/users/authenticate")
    void authenticate(@Body Credentials credentials, Callback<Token> callback);
}
