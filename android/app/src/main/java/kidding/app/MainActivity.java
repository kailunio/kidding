package kidding.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import kidding.lib.KiddingProxySelector;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity {

    OkHttpClient client;
    Callback callback;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String text = tvHelloWorld.getText().toString() + "\n" + msg.obj;
            tvHelloWorld.setText(text);
        }
    };

    TextView tvHelloWorld;
    Button btnClickToReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvHelloWorld = (TextView) findViewById(R.id.tv_hello_world);
        btnClickToReq = (Button) findViewById(R.id.btn_click_to_req);
        btnClickToReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSomeWeb();
            }
        });
    }

    public void requestSomeWeb() {
        client = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .proxySelector(KiddingProxySelector.instance())
                .build();

        Request request = new Request.Builder()
//                .url("https://www.baidu.com")
                .url("http://www.oschina.net/")
                .build();

        callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.obtainMessage(0, e.toString())
                        .sendToTarget();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handler.obtainMessage(0, response.code() + " " + response.message())
                        .sendToTarget();
            }
        };
        client.newCall(request).enqueue(callback);
    }
}
