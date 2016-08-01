package com.askonthego.service;

import android.content.Context;
import android.preference.PreferenceManager;

public class Preferences {

    public static final String API_TOKEN_KEY = "API_TOKEN";
    private Context context;

    public Preferences(Context context) {
        this.context = context;
    }

    public void saveApiToken(String token) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(API_TOKEN_KEY, token).apply();
    }

    public String getApiToken() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(API_TOKEN_KEY, null);
    }
}
