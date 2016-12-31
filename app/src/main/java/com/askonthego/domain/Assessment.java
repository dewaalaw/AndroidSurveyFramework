package com.askonthego.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Assessment {

    private transient String documentId;
    private transient boolean synced;
    
    private String surveyName;
    private List<AssessmentResponse> responses = new ArrayList<>();
    private Participant participant;
    private Date startDate;
    private Date endDate;
    private Date timeoutDate;

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setSynced(boolean synced) {
        this.synced = synced;
    }

    public boolean isSynced() {
        return synced;
    }

    public void setSurveyName(String surveyName) {
        this.surveyName = surveyName;
    }

    public String getSurveyName() {
        return surveyName;
    }

    public void setResponses(List<AssessmentResponse> responses) {
        this.responses = responses;
    }

    public List<AssessmentResponse> getResponses() {
        return responses;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setTimeoutDate(Date timeoutDate) {
        this.timeoutDate = timeoutDate;
    }

    public Date getTimeoutDate() {
        return timeoutDate;
    }
}
