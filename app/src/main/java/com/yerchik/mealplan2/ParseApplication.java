package com.yerchik.mealplan2;

import android.app.Application;
import com.parse.Parse;

// This class is for initializing Parse
public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "AgLXA7Q166mXb5wsD9vfmC7iuFro2Sm4v7Q73h4Q", "TArGPnHN8aGII3rqu8ulxQIz2Zq1haKBugOGEAYP");
    }


}
