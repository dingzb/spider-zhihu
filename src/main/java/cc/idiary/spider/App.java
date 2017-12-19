package cc.idiary.spider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws IOException {
        ZhihuSpiderClient client = new ZhihuSpiderClient();
        List<ProxyEntity> proxies = new ArrayList<>();
        ProxyEntity proxy1 = new ProxyEntity();
        proxy1.setIp("180.113.66.46");
        proxy1.setPort(8123);
        ProxyEntity proxy2 = new ProxyEntity();
        proxy1.setIp("120.25.164.134");
        proxy1.setPort(8118);
        proxies.add(proxy1);
        proxies.add(proxy2);
//        client.setProxies(proxies);
        ZhihuUserSpider spider = new ZhihuUserSpider(client);
//        spider.setHandler(new MySqlUserHandler());
        spider.spide();
    }
}
