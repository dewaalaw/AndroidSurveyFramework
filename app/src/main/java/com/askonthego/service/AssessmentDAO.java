package com.askonthego.service;

import android.util.Log;

import com.askonthego.domain.Assessment;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.Reducer;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.View;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AssessmentDAO {

    private static final String ASSESSMENT_PROPERTY = "assessment";
    private static final String SYNCED_PROPERTY = "synced";
    private static final String ASSESSMENTS_VIEW_NAME = "assessments";

    private Database database;
    private ObjectMapper objectMapper = new ObjectMapper();

    public AssessmentDAO(Database database) {
        this.database = database;
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<Assessment> getUnsyncedAssessments() throws StorageException {
        try {
            View assessmentsView = database.getView(ASSESSMENTS_VIEW_NAME);
            assessmentsView.setMapReduce(new Mapper() {
                @Override
                public void map(Map<String, Object> document, Emitter emitter) {
                    Map<String, Object> map = (Map<String, Object>) document.get(ASSESSMENT_PROPERTY);
                    boolean synced = (boolean) map.get(SYNCED_PROPERTY);
                    if (!synced) {
                        emitter.emit(ASSESSMENT_PROPERTY, map);
                    }
                }
            }, new ReducerAdapter(), "1");

            Query query = assessmentsView.createQuery();
            query.setMapOnly(true);
            QueryEnumerator result = null;
            result = query.run();

            Log.d(getClass().getName(), "Retrieved " + result.getCount() + " records!!!!");

            ArrayList<Assessment> assessments = new ArrayList<>();
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Assessment convertedAssessment = objectMapper.convertValue(row.getValue(), Assessment.class);
                convertedAssessment.setDocumentId(row.getDocumentId());
                assessments.add(convertedAssessment);
            }

            return assessments;
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            throw new StorageException("Error retrieving unsynced assessments", e);
        }
    }

    private class ReducerAdapter implements Reducer {
        @Override
        public Object reduce(List<Object> keys, List<Object> values, boolean rereduce) {
            return new Integer(values.size());
        }
    }

    public void save(Assessment assessment) throws StorageException {
        try {
            if (assessment.getDocumentId() != null) {
                updateDocument(assessment);
            } else {
                createDocument(database, assessment);
            }
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    private void createDocument(Database database, final Assessment assessment) throws CouchbaseLiteException {
        Document document = database.createDocument();
        Map<String, Object> map = new HashMap<>();
        map.put(ASSESSMENT_PROPERTY, assessment);
        document.putProperties(map);
        assessment.setDocumentId(document.getId());
    }

    public void updateDocument(final Assessment assessment) throws CouchbaseLiteException {
        Document document = database.getDocument(assessment.getDocumentId());
        document.update(new Document.DocumentUpdater() {
            @Override
            public boolean update(UnsavedRevision newRevision) {
                Map<String, Object> properties = newRevision.getUserProperties();
                properties.put(ASSESSMENT_PROPERTY, assessment);
                newRevision.setUserProperties(properties);
                return true;
            }
        });
    }
}
