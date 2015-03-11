package com.example.jaf50.survey;

import android.test.AndroidTestCase;

import com.example.jaf50.survey.domain.Assessment;
import com.example.jaf50.survey.domain.AssessmentResponse;
import com.example.jaf50.survey.domain.Value;
import com.example.jaf50.survey.service.SerializationService;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

public class TestSerializationService extends AndroidTestCase {

  public void testSerializeAssessment() throws IOException, ParseException {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    AssessmentResponse assessmentResponse1 = new AssessmentResponse();
    assessmentResponse1.setResponseId("var1");
    assessmentResponse1.setResponseDate(simpleDateFormat.parse("2015-02-03 12:54:55"));
    assessmentResponse1.addValue(new Value().setValue("val1"));

    AssessmentResponse assessmentResponse2 = new AssessmentResponse();
    assessmentResponse2.setResponseId("var2");
    assessmentResponse2.setResponseDate(simpleDateFormat.parse("2015-02-03 12:54:57"));
    assessmentResponse2.addValue(new Value().setValue("val2"));

    Assessment assessment = new Assessment();
    assessment.setName("My Survey");
    assessment.setDescription("Description");
    assessment.setResponses(Arrays.asList(assessmentResponse1, assessmentResponse2));

    SerializationService serializationService = new SerializationService();

    String expectedJson = "{" +
        "    \"name\": \"My Survey\"," +
        "    \"responses\": [" +
        "        {" +
        "            \"responseDate\": \"2015-02-03T00:54:55Z\"," +
        "            \"responseId\": \"var1\"," +
        "            \"values\": [" +
        "                {" +
        "                    \"value\": \"val1\"" +
        "                }" +
        "            ]" +
        "        }," +
        "        {" +
        "            \"responseDate\": \"2015-02-03T00:54:57Z\"," +
        "            \"responseId\": \"var2\"," +
        "            \"values\": [" +
        "                {" +
        "                    \"value\": \"val2\"" +
        "                }" +
        "            ]" +
        "        }" +
        "    ]" +
        "}";

    JsonParser parser = new JsonParser();
    JsonElement expectedElement = parser.parse(expectedJson);
    JsonElement actualElement = parser.parse(serializationService.serialize(assessment));

    assertEquals(expectedElement, actualElement);
  }
}
