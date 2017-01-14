package com.askonthego.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.askonthego.domain.AssessmentResponse;
import com.askonthego.domain.Participant;
import com.couchbase.lite.Database;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.Reducer;
import com.couchbase.lite.View;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class ParticipantDAO {

    private static final String ACTIVE_PARTICIPANT_KEY = "active_participant";
    private ObjectMapper objectMapper = new ObjectMapper();
    private SharedPreferences sharedPreferences;

    public ParticipantDAO(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public void setActiveParticipant(Participant participant) {
        try {
            String json = objectMapper.writeValueAsString(participant);
            sharedPreferences.edit().putString(ACTIVE_PARTICIPANT_KEY, json).apply();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public Participant getActiveParticipant() {
        String json = sharedPreferences.getString(ACTIVE_PARTICIPANT_KEY, null);
        if (json != null) {
            try {
                return objectMapper.readValue(json, Participant.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
