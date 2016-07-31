package com.askonthego.domain;

import com.askonthego.util.LogUtils;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Participant")
public class Participant extends ParseObject {

  public String getId() {
    return getString("id");
  }

  public void setId(String id) {
    put("id", id);
  }

  private boolean isActive() {
    return getBoolean("active");
  }

  private void setActive(boolean active) {
    put("active", active);
  }

  public static void setActiveParticipant(Participant participant) {
    participant.setActive(true);
    // Inactivate the currently active participant (if applicable).
    ParseQuery<Participant> query = ParseQuery.getQuery("Participant");
    try {
      List<Participant> participants = query.fromLocalDatastore()
          .whereEqualTo("active", true)
          .find();
      for (Participant otherParticipants : participants) {
        otherParticipants.setActive(false);
        otherParticipants.pinInBackground();
      }
    } catch (ParseException e) {
      LogUtils.e(Participant.class, "Error setting active participant.", e);
    }
    participant.pinInBackground();
  }

  public static Participant getActiveParticipant() {
    ParseQuery<Participant> query = ParseQuery.getQuery("Participant");
    List<Participant> participants = new ArrayList<>();
    try {
      participants = query.fromLocalDatastore()
                          .whereEqualTo("active", true)
                          .find();
      return !participants.isEmpty() ? participants.get(0) : null;
    } catch (ParseException e) {
      LogUtils.e(Participant.class, "Error retrieving active participant.", e);
    } catch (ClassCastException e) {
      LogUtils.e(Participant.class, "Tried converting object to Participant: " + participants.get(0), e);
      throw e;
    }
    return null;
  }
}