package kidding.lib;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import org.jetbrains.annotations.NotNull;

/**
 * Preference助手
 *
 * @author xukailun on 2017/7/20.
 */
/**package*/ class PreferenceHelper {

    public static final String PREFERENCE_FILE_NAME = "kidding_pref";

    private SharedPreferences sharedPreferences = null;

    private static PreferenceHelper INSTANCE;

    public static PreferenceHelper instance() {
        if (INSTANCE == null) {
            throw new RuntimeException("PreferenceHelper not init");
        }
        return INSTANCE;
    }

    public static void init(@NotNull Application app) {
        INSTANCE = new PreferenceHelper(app);
    }

    private PreferenceHelper(@NotNull Application app){
        sharedPreferences = app.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
    }

    public SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null) {
            throw new RuntimeException("SharedPreferences not init");
        }
        return sharedPreferences;
    }

    public String getString(String key) {
        return getSharedPreferences().getString(key, "");
    }

}
