package com.example.jaf50.survey.domain;

import android.content.Context;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

public class Participant {

  private static final String CURRENT_PARTICIPANT_KEY = "currentParticipant";
  private static Gson gson = new Gson();

  private String id;

  private String password;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public static void setCurrentParticipant(Context context, Participant participant) {
    PreferenceManager.getDefaultSharedPreferences(context).edit().putString(CURRENT_PARTICIPANT_KEY, gson.toJson(participant)).commit();
  }

  public static Participant getCurrentParticipant(Context context) {
    String currentParticipantJson = PreferenceManager.getDefaultSharedPreferences(context).getString(CURRENT_PARTICIPANT_KEY, null);
    return gson.fromJson(currentParticipantJson, Participant.class);
  }
}
