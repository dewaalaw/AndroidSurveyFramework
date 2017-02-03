# AndroidSurveyFramework

An Android EMA (Ecological Momentary Assessment) survey app. This app relies on the Node.js server project here: https://github.com/josh7up/SurveyRestApi

In order to run the app, you will need to change the *app/build.gradle* file's API_BASE_URL variable to point to your running Node.js server:

    buildConfigField "String", "API_BASE_URL", "\"https://my.survey-rest-api.ip/\""
