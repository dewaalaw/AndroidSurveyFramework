package com.askonthego.service;

import com.askonthego.domain.Participant;

public class LocalRegistrationService {

  private static final String LOCAL_PASSWORD = "9252";

  public boolean register(String participantId, String password) {
    if (LOCAL_PASSWORD.equals(password)) {
      Participant participant = new Participant();
      participant.setId(participantId);
      Participant.setActiveParticipant(participant);
      return true;
    }
    return false;
  }
}
