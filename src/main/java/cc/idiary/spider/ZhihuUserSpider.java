package cc.idiary.spider;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Random;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

public class ZhihuUserSpider {
    private String basicUrl = "https://www.zhihu.com/api/v4/members/{0}/followees?include=data[*].answer_count,articles_count,gender,follower_count,is_followed,is_following,badge[?(type=best_answerer)].topics&offset=0&limit=20";
    private HttpClient client;
    private ZhihuSpiderClient zhihuSpiderClient;
    private BlockingDeque<String> urlTokenQueue = new LinkedBlockingDeque<>();
    private UserHandler handler = new ConsoleUserHandler(urlTokenQueue);    //default console.
    private ExecutorService service = Executors.newFixedThreadPool(3);


    public ZhihuUserSpider(ZhihuSpiderClient spiderClient) {
        this.client = spiderClient.client;
        this.zhihuSpiderClient = spiderClient;
    }

    public void setHandler(UserHandler handler) {
        this.handler = handler;
    }

    public void spide() {
        try {
            urlTokenQueue.put("mei-tuan-dian-ping-ji-shu-tuan-dui"); //first url_token
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (true) {  //先不设置如何结束爬虫
            String urlToken = null;
            try {
                urlToken = urlTokenQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            service.execute(new _Spider(urlToken));
        }
    }

    private class _Spider implements Runnable {
        private String urlToken;

        _Spider(String urlToken) {
            this.urlToken = urlToken;
        }

        @Override
        public void run() {
            getList(urlToken);
        }

        private void getFollowees(String url) {
            HttpRequestBase request = zhihuSpiderClient.getRequest(url);
            HttpResponse response = null;
            try {
                response = client.execute(request);
                int sl = new Random().nextInt(2000);
                Thread.sleep(sl);
                HttpEntity entity = null;
                if (response != null) {
                    entity = response.getEntity();
                }
                if (entity != null) {
                    UserParser parser = UserParser.createParser(EntityUtils.toString(entity));
                    handler.handle(parser);
                    if (parser != null && !parser.isEnd()) {
                        getFollowees(parser.nextUrl());
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                request.releaseConnection();
            }
        }

        private void getList(String urlToken) {
            getFollowees(MessageFormat.format(basicUrl, urlToken));
        }

    }

}
