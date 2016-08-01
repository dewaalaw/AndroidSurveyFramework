package com.askonthego;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.askonthego.actions.Action;
import com.askonthego.domain.AssessmentResponse;
import com.askonthego.parser.NavigationButtonModel;
import com.askonthego.response.ResponseCriteria;
import com.askonthego.service.ResponseCollectorService;
import com.askonthego.ui.ISurveyComponent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.pristine.sheath.Sheath;
import lombok.Getter;
import lombok.Setter;

public class SurveyScreen extends LinearLayout {

    @BindView(R.id.content) LinearLayout contentLayout;

    private List<ISurveyComponent> surveyComponents = new ArrayList<>();
    private LinkedHashMap<ResponseCriteria, Action> actionMap = new LinkedHashMap<>();

    @Getter @Setter private String screenId;
    @Getter @Setter private String mainText;
    @Getter @Setter private NavigationButtonModel previousButtonModel;
    @Getter @Setter private NavigationButtonModel nextButtonModel;
    @Inject ResponseCollectorService responseCollectorService;

    public SurveyScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
        Sheath.inject(this);
        ButterKnife.bind(this);
    }

    public void addSurveyComponent(ISurveyComponent surveyComponent) {
        contentLayout.addView(surveyComponent.getView());
        surveyComponents.add(surveyComponent);
    }

    public List<AssessmentResponse> collectResponses() {
        return responseCollectorService.collectResponses(surveyComponents);
    }

    /**
     * Returns true if all responses have been entered on this screen; false otherwise.
     */
    public boolean responsesEntered() {
        List<AssessmentResponse> screenResponses = collectResponses();
        for (AssessmentResponse screenResponse : screenResponses) {
            if (screenResponse.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public Action getAction() {
        if (responsesEntered()) {
            List<AssessmentResponse> responses = collectResponses();
            for (ResponseCriteria responseCriteria : actionMap.keySet()) {
                if (responseCriteria.isSatisfied(responses)) {
                    return actionMap.get(responseCriteria);
                }
            }
        } else {
            // Return the default response criteria, if one exists.
            for (ResponseCriteria responseCriteria : actionMap.keySet()) {
                if (responseCriteria.isDefault()) {
                    return actionMap.get(responseCriteria);
                }
            }
        }
        return null;
    }

    public void addResponseCriteria(ResponseCriteria responseCriteria, Action correspondingAction) {
        actionMap.put(responseCriteria, correspondingAction);
    }
}
