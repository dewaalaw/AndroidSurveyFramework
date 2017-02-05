package com.askonthego.service;

import android.content.Context;

import com.askonthego.R;

import java.io.InputStream;

public class ResourceService {

    public InputStream getAlarmInputStream(Context context) {
        return context.getResources().openRawResource(R.raw.demo_alarm_schedule);
    }
}
