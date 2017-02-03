# AndroidSurveyFramework

An Android EMA (Ecological Momentary Assessment) survey app. This app relies on the Node.js server project here: https://github.com/josh7up/SurveyRestApi

<img src="https://github.com/josh7up/AndroidSurveyFramework/blob/gh-pages/images/WelcomeScreen.png" width="250">
<img src="https://github.com/josh7up/AndroidSurveyFramework/blob/gh-pages/images/RadioQuestion.png" width="250">
<img src="https://github.com/josh7up/AndroidSurveyFramework/blob/gh-pages/images/ScaleQuestion.png" width="250">
<img src="https://github.com/josh7up/AndroidSurveyFramework/blob/gh-pages/images/SkipQuestion.png" width="250">

In order to run the app, you will need to change the *app/build.gradle* file's API_BASE_URL variable to point to your running Node.js server:

    buildConfigField "String", "API_BASE_URL", "\"https://my.survey-rest-api.ip/\""
