package com.askonthego.service;

import com.askonthego.domain.Assessment;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;

public interface RestAssessmentService {

  @POST("/assessments")
  void postAssessment(@Header("x-access-token") String apiToken, @Body Assessment assessment, Callback<Void> callback);
}
