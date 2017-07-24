package kidding.lib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * @author xukailun on 2017/7/20.
 */
public class AdbReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null) {
            return;
        }

        String ipv4 = extras.getString("ipv4", "");

        // 修改代理的地址
        Kidding.instance().modifyCurrentProxy(ipv4);

        // 持久化
        SharedPreferences sp = PreferenceHelper.instance().getSharedPreferences();
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putString("ipv4", ipv4);
        spEditor.apply();

        // 提示用户
        String msg;
        if (!TextUtils.isEmpty(ipv4)) {
            msg = "代理服务器已改变：" + ipv4;
        } else {
            msg = "代理服务器被清空";
        }

        Toast.makeText(context, msg, Toast.LENGTH_LONG)
                .show();
    }
}
