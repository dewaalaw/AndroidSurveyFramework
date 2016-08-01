package com.askonthego.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.askonthego.response.Response;

import lombok.Getter;
import lombok.Setter;

public class RadioGroupComponent extends RadioGroup implements ISurveyComponent {

    @Getter @Setter private String responseId;

    public RadioGroupComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewGroup getView() {
        return this;
    }

    public void addComponent(RadioButtonComponent radioButtonComponent) {
        addView(radioButtonComponent);
    }

    @Override
    public boolean acceptsResponse() {
        return true;
    }

    public Response getResponse() {
        Response response = new Response(responseId);
        int checkedId = getCheckedRadioButtonId();
        if (checkedId != -1) {
            RadioButtonComponent radioButtonComponent = (RadioButtonComponent) findViewById(checkedId);
            response.addValue(radioButtonComponent.getValue());
        }
        return response;
    }
}
