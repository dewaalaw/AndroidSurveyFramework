package com.askonthego.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.askonthego.response.Response;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;

public class CheckboxGroupComponent extends LinearLayout implements ISurveyComponent {

  @Setter private String responseId;

  private List<CheckboxComponent> checkboxComponents = new ArrayList<CheckboxComponent>();

  public CheckboxGroupComponent(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ViewGroup getView() {
    return this;
  }

  public void addComponent(final CheckboxComponent checkboxComponent) {
    addView(checkboxComponent);
    checkboxComponents.add(checkboxComponent);

    checkboxComponent.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        uncheckOpposingGroup(checkboxComponent);
      }
    });
  }

  private void uncheckOpposingGroup(CheckboxComponent checkboxComponent) {
    for (CheckboxComponent component : checkboxComponents) {
      if (component.isMutuallyExclusive() != checkboxComponent.isMutuallyExclusive()) {
        component.setChecked(false);
      }
    }
  }

  @Override
  public boolean acceptsResponse() {
    return true;
  }

  public Response getResponse() {
    Response response = new Response(responseId);
    for (CheckboxComponent checkboxComponent : checkboxComponents) {
      if (checkboxComponent.isChecked()) {
        response.addValue(checkboxComponent.getValue());
      }
    }
    return response;
  }
}
