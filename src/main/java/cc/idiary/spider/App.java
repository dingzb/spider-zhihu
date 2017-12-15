package cc.idiary.spider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Hello world!
 */
public class App {
    private static String basicUrl = "https://www.zhihu.com/api/v4/members/lucas-den/followees?include=data[*].answer_count,articles_count,gender,follower_count,is_followed,is_following,badge[?(type=best_answerer)].topics&offset=0&limit=20";
    private static HttpClient client = HttpClients.createDefault();
    private static HttpGet get = new HttpGet();

    static {
        get.setHeader("authorization", "oauth c3cef7c66a1843f8b3a9e6a1e3160e20");
        get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2623.110 Safari/537.36\"");
    }

    private static ObjectMapper mapper = new ObjectMapper();


    public static void main(String[] args) throws IOException {
        final Queue<SimpleUserEntity> users = new LinkedBlockingDeque<>();
        UserHandler handler = new UserHandler() {
            @Override
            public void handle(SimpleUserEntity user) {
                users.offer(user);
            }

            @Override
            public void handle(Collection<SimpleUserEntity> users) {

            }
        };

        getFollowees(basicUrl, handler);
        while (true) {
            SimpleUserEntity userEntity = users.poll();
            System.out.println(userEntity);
            getList(userEntity.getUrlToken(), handler);
        }
    }

    private static void getFollowees(String url, UserHandler handler){
        get.setURI(URI.create(url));
        HttpResponse response = null;
        try {
            response = client.execute(get);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = mapper.readTree(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        JsonNode data = node.get("data");
        data.forEach(u -> handler.handle(toUserEntity(u)));
        JsonNode paging = node.get("paging");
        if (!paging.get("is_end").booleanValue()) {
            String nextUrl = paging.get("next").textValue();
            getFollowees(nextUrl, handler);
        }

    }

    private static void getList(String urlToken, UserHandler handler){
        getFollowees(MessageFormat.format(basicUrl, urlToken), handler);
    }

    private static SimpleUserEntity toUserEntity(JsonNode node) {
        SimpleUserEntity user = new SimpleUserEntity();
        user.setName(node.get("name").textValue());
        user.setUrlToken(node.get("url_token").textValue());
        return user;
    }
}
