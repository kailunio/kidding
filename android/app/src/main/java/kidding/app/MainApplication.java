package kidding.app;

import android.app.Application;

import kidding.lib.Kidding;

/**
 * @author xukailun on 2017/7/21.
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Kidding.instance().start(this);
    }
}
