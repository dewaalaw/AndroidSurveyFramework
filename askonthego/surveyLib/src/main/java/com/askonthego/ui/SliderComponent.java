package com.askonthego.ui;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.askonthego.R2;
import com.askonthego.response.Response;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Getter;
import lombok.Setter;

public class SliderComponent extends LinearLayout implements ISurveyComponent {

  private static final int DEFAULT_PROGRESS = 50;

  @BindView(R2.id.seekBar) SurveySeekBar seekBar;
  @BindView(R2.id.leftLabelTextView) TextView leftLabelTextView;
  @BindView(R2.id.rightLabelTextView) TextView rightLabelTextView;

  @Getter @Setter private String responseId;

  public SliderComponent(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    ButterKnife.bind(this, this);
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
    this.leftLabelTextView.setText(Html.fromHtml(text));
  }

  public void setRightLabelText(String text) {
    this.rightLabelTextView.setText(Html.fromHtml(text));
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
}
