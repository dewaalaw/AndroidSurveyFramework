package com.example.jaf50.survey.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jaf50.survey.R;
import com.example.jaf50.survey.response.Response;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SliderComponent extends LinearLayout implements ISurveyComponent {

  private static final int DEFAULT_PROGRESS = 50;

  @InjectView(R.id.seekBar)
  SurveySeekBar seekBar;

  @InjectView(R.id.leftLabelTextView)
  TextView leftLabelTextView;

  @InjectView(R.id.rightLabelTextView)
  TextView rightLabelTextView;

  private String responseId;

  public SliderComponent(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.inject(this, this);
    seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
      @Override
      public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
        if (fromUser) {
          seekBar.setProgress(value);
          ((SurveySeekBar) seekBar).setSelectedValue(value);
        }
      }
    });
    seekBar.setSelectedValue(-1);
    seekBar.setProgress(DEFAULT_PROGRESS);
  }

  public void setLeftLabelText(String text) {
    this.leftLabelTextView.setText(text);
  }

  public void setRightLabelText(String text) {
    this.rightLabelTextView.setText(text);
  }

  @Override
  public boolean acceptsResponse() {
    return true;
  }

  @Override
  public Response getResponse() {
    Response response = new Response(responseId);
    if (seekBar.getSelectedValue() != SurveySeekBar.DEFAULT_VALUE) {
      response.addValue(seekBar.getSelectedValue());
    }
    return response;
  }

  @Override
  public View getView() {
    return this;
  }

  public void setResponseId(String responseId) {
    this.responseId = responseId;
  }

  public String getResponseId() {
    return responseId;
  }
}
