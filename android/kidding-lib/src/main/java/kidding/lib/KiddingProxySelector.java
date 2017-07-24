package kidding.lib;


import android.text.TextUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.List;

/**
 * 定制的ProxySelector
 *
 * @author xukailun on 2017/7/20.
 */
public class KiddingProxySelector extends ProxySelector {

    private static KiddingProxySelector INSTANCE = new KiddingProxySelector();

    public static KiddingProxySelector instance() {
        return INSTANCE;
    }

    private KiddingProxySelector() {
        // singleton
    }

    @Override
    public List<Proxy> select(URI uri) {

        // 优先选择Kidding设置的代理
        String cProxy = Kidding.instance().getCurrentProxy();
        if (!TextUtils.isEmpty(cProxy)) {
            InetSocketAddress sa = new InetSocketAddress(cProxy, 8888);
            Proxy proxy = new Proxy(Proxy.Type.HTTP, sa);

            return Collections.singletonList(proxy);
        }

        return ProxySelector.getDefault().select(uri);
    }

    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        // do nothing
    }
}
