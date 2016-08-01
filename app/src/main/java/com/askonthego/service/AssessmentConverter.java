package com.askonthego.service;

import com.askonthego.domain.Assessment;

import org.json.JSONException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Type;

import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

public class AssessmentConverter implements Converter {

    private DomainSerializationService domainSerializationService;

    public AssessmentConverter(DomainSerializationService domainSerializationService) {
        this.domainSerializationService = domainSerializationService;
    }

    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        return null;
    }

    @Override
    public TypedOutput toBody(Object object) {
        return new AssessmentTypedOutput((Assessment) object);
    }

    private class AssessmentTypedOutput implements TypedOutput {
        private Assessment assessment;

        private AssessmentTypedOutput(Assessment assessment) {
            this.assessment = assessment;
        }

        @Override
        public String fileName() {
            return null;
        }

        @Override
        public String mimeType() {
            return "application/json";
        }

        @Override
        public long length() {
            return 0;
        }

        @Override
        public void writeTo(OutputStream out) throws IOException {
            try {
                String json = domainSerializationService.toJson(assessment);
                PrintStream ps = new PrintStream(out);
                ps.print(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
