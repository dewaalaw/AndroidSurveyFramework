package com.askonthego.service;

import com.askonthego.domain.Participant;

public class LocalRegistrationService {

    private static final String LOCAL_PASSWORD = "9252";
    private ParticipantService participantService;

    public LocalRegistrationService(ParticipantService participantService) {
        this.participantService = participantService;
    }

    public boolean register(String participantId, String password) {
        if (LOCAL_PASSWORD.equals(password)) {
            Participant participant = new Participant();
            participant.setId(participantId);
            participantService.setActiveParticipant(participant);
            return true;
        }
        return false;
    }
}
