package cc.idiary.spider;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

public class ZhihuSpiderClient extends AbstractSpiderClient {

    public ZhihuSpiderClient() {
        super();
    }

    public HttpRequestBase getRequest(String url) {
        HttpGet get = new HttpGet(url);
        get.setHeader(SpiderUtils.randomAgent());
        ProxyEntity proxy = getProxyRandom();
        if (proxy != null){
            requestConfigBuilder.setProxy(new HttpHost(proxy.getIp(), proxy.getPort()));
        }
        get.setConfig(requestConfigBuilder.build());
        get.setHeader("authorization", "oauth c3cef7c66a1843f8b3a9e6a1e3160e20");
        return get;
    }
}
