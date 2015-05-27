package com.example.jaf50.survey.util;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtils {

  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

  public static void d(Class<?> toLog, String text) {
    Log.d(toLog.getName(), text);
    writeToFile(toLog, text);
  }

  public static void e(Class<?> toLog, String text) {
    e(toLog, text, null);
  }

  public static void e(Class<?> toLog, String text, Throwable t) {
    Log.e(toLog.getName(), text, t);
    if (t != null) {
      text += t.toString();
    }
    writeToFile(toLog, text);
  }

  private static void writeToFile(Class<?> toLog, String text) {
    PrintWriter writer = null;
    try {
      String storageRoot = Environment.getExternalStorageDirectory().toString();
      File surveyFolder = new File(storageRoot + "/survey");
      surveyFolder.mkdirs();
      writer = new PrintWriter(new BufferedWriter(new FileWriter(new File(surveyFolder, "log.txt"), true)));
      StringBuilder builder = new StringBuilder().append(dateFormat.format(new Date())).append(" - ").append(toLog.getName()).append(": ").append(text);
      writer.println(builder.toString());
    } catch (Exception e) {
      Log.e("LogUtils", "Error writing to log file", e);
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }
}
