//package com.example.jaf50.survey;
//
//import android.test.AndroidTestCase;
//
//import com.example.jaf50.survey.domain.Assessment;
//import com.example.jaf50.survey.domain.AssessmentResponse;
//import com.example.jaf50.survey.domain.Participant;
//import com.example.jaf50.survey.domain.Survey;
//import com.example.jaf50.survey.domain.Value;
//import com.example.jaf50.survey.response.TimeResponse;
//import com.example.jaf50.survey.service.SerializationService;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonParser;
//import com.parse.Parse;
//import com.parse.ParseObject;
//
//import java.io.IOException;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Arrays;
//import java.util.Date;
//
//public class TestSerializationService extends AndroidTestCase {
//
//  private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//
//  public void testSerializeAssessment() throws IOException, ParseException {
//    Survey survey = new Survey().setName("My Survey");
//    survey.save();
//
//    AssessmentResponse assessmentResponse1 = new AssessmentResponse().setResponseId("var1").setResponseDate(simpleDateFormat.parse("2015-02-03 12:54:55")).addValue(new Value().setValue("val1"));
//    AssessmentResponse assessmentResponse2 = new AssessmentResponse().setResponseId("var2").setResponseDate(simpleDateFormat.parse("2015-02-03 12:54:57")).addValue(new Value().setValue("val2"));
//    Assessment assessment = new Assessment().setSurvey(survey).setResponses(assessmentResponse1, assessmentResponse2).setParticipant(new Participant().setAssignedId("123"));
//
//    SerializationService serializationService = new SerializationService();
//
//    String expectedJson = "{" +
//        "    \"participant\": {" +
//        "        \"assignedId\": \"123\"" +
//        "    }," +
//        "    \"responses\": [" +
//        "        {" +
//        "            \"responseDate\": \"2015-02-03T00:54:55Z\"," +
//        "            \"responseId\": \"var1\"," +
//        "            \"values\": [" +
//        "                {" +
//        "                    \"value\": \"val1\"" +
//        "                }" +
//        "            ]" +
//        "        }," +
//        "        {" +
//        "            \"responseDate\": \"2015-02-03T00:54:57Z\"," +
//        "            \"responseId\": \"var2\"," +
//        "            \"values\": [" +
//        "                {" +
//        "                    \"value\": \"val2\"" +
//        "                }" +
//        "            ]" +
//        "        }" +
//        "    ]," +
//        "    \"survey\": {" +
//        "        \"name\": \"My Survey\"" +
//        "    }" +
//        "}";
//
//    JsonParser parser = new JsonParser();
//    JsonElement expectedElement = parser.parse(expectedJson);
//    JsonElement actualElement = parser.parse(serializationService.serialize(assessment));
//
//    assertEquals(expectedElement, actualElement);
//  }
//
//  public void testSerializeDateResponse() throws IOException, ParseException {
//    Survey survey = new Survey().setName("My Survey");
//    survey.save();
//
//    AssessmentResponse assessmentResponse1 = new AssessmentResponse().setResponseId("var1").setResponseDate(simpleDateFormat.parse("2015-02-03 12:54:55")).addValue(new Value().setValue("2015-02-02 12:00:00"));
//    Assessment assessment = new Assessment().setSurvey(survey).setResponses(assessmentResponse1);
//
//    SerializationService serializationService = new SerializationService();
//
//    String expectedJson = "{" +
//        "    \"responses\": [" +
//        "        {" +
//        "            \"responseDate\": \"2015-02-03T00:54:55Z\"," +
//        "            \"responseId\": \"var1\"," +
//        "            \"values\": [" +
//        "                {" +
//        "                    \"value\": \"2015-02-02 12:00:00\"" +
//        "                }" +
//        "            ]" +
//        "        }" +
//        "    ]," +
//        "    \"survey\": {" +
//        "        \"name\": \"My Survey\"" +
//        "    }" +
//        "}";
//
//    JsonParser parser = new JsonParser();
//    JsonElement expectedElement = parser.parse(expectedJson);
//    JsonElement actualElement = parser.parse(serializationService.serialize(assessment));
//
//    assertEquals(expectedElement, actualElement);
//  }
//
//  public void testSerializeTimeResponse() throws IOException, ParseException {
//    Survey survey = new Survey().setName("My Survey");
//    survey.save();
//
//    // The output time ignores the year, month, and day portion of the date.
//    String value = new TimeResponse(simpleDateFormat.parse("2015-02-02 12:00:00")).toString();
//
//    AssessmentResponse assessmentResponse1 = new AssessmentResponse().setResponseId("var1").setResponseDate(simpleDateFormat.parse("2015-02-03 12:54:55")).addValue(new Value().setValue(value));
//    Assessment assessment = new Assessment().setSurvey(survey).setResponses(assessmentResponse1);
//
//    SerializationService serializationService = new SerializationService();
//
//    String expectedJson = "{" +
//        "    \"responses\": [" +
//        "        {" +
//        "            \"responseDate\": \"2015-02-03T00:54:55Z\"," +
//        "            \"responseId\": \"var1\"," +
//        "            \"values\": [" +
//        "                {" +
//        "                    \"value\": \"12:00 AM\"" +
//        "                }" +
//        "            ]" +
//        "        }" +
//        "    ]," +
//        "    \"survey\": {" +
//        "        \"name\": \"My Survey\"" +
//        "    }" +
//        "}";
//
//    JsonParser parser = new JsonParser();
//    JsonElement expectedElement = parser.parse(expectedJson);
//    JsonElement actualElement = parser.parse(serializationService.serialize(assessment));
//
//    assertEquals(expectedElement, actualElement);
//  }
//
//
//
//
//  public void test() {
//    final String APPLICATION_ID = "M8rcJZvA3poUvvJofyS4t5K0vtpHVLg3biM1NgVK";
//    final String CLIENT_KEY = "JPImlAeJvXtmuaTH1mmzcqe87zuOtXaUvgJTzERX";
//
//    Parse.enableLocalDatastore(getContext());
//    Parse.initialize(getContext(), APPLICATION_ID, CLIENT_KEY);
//
//    ParseObject survey = new ParseObject("Survey");
//    survey.put("name", "My Survey");
//
//    ParseObject participant = new ParseObject("Participant");
//    participant.put("assignedId", "123");
//
//    ParseObject assessmentResponse1 = new ParseObject("AssessmentResponse");
//    assessmentResponse1.put("responseId", "var1");
//    assessmentResponse1.put("responseDate", new Date());
//    assessmentResponse1.add("value", "val1");
//
//    ParseObject assessmentResponse2 = new ParseObject("AssessmentResponse");
//    assessmentResponse2.put("responseId", "var2");
//    assessmentResponse2.put("responseDate", new Date());
//    assessmentResponse2.add("value", "val2");
//
//    ParseObject assessment = new ParseObject("Assessment");
//    assessment.put("participant", participant);
//    assessment.put("survey", survey);
//    assessment.put("description", "Survey Description");
//    assessment.put("responses", Arrays.asList(assessmentResponse1, assessmentResponse2));
//
//    SerializationService serializationService = new SerializationService();
//    assertEquals("", serializationService.serialize(assessment));
//  }
//}
