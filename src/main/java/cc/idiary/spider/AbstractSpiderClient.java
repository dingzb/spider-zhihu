package cc.idiary.spider;

import org.apache.http.Consts;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AbstractSpiderClient {

    private final static int TIME_OUT = 1000;
    private List<ProxyEntity> proxies = new ArrayList<>();
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private Random random = new Random();

    protected HttpClient client;
    protected RequestConfig.Builder requestConfigBuilder;

    public AbstractSpiderClient() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContexts.custom()
                    .loadTrustMaterial(KeyStore.getInstance(KeyStore.getDefaultType()), new TrustStrategy() {
                        @Override
                        public boolean isTrusted(X509Certificate[] chain, String authType)
                                throws CertificateException {
                            return true;
                        }
                    }).build();
            SSLConnectionSocketFactory sslSFactory = new SSLConnectionSocketFactory(sslContext);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE).register("https", sslSFactory)
                    .build();
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(TIME_OUT).setTcpNoDelay(true).build();
            connManager.setDefaultSocketConfig(socketConfig);
            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                    .setMalformedInputAction(CodingErrorAction.IGNORE)
                    .setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Consts.UTF_8).build();
            connManager.setDefaultConnectionConfig(connectionConfig);
            connManager.setMaxTotal(500);
            connManager.setDefaultMaxPerRoute(300);
            HttpClientBuilder httpClientBuilder = HttpClients.custom()
                    .setConnectionManager(connManager)
                    .setDefaultCookieStore(new BasicCookieStore());

            client = httpClientBuilder.build();

            requestConfigBuilder = RequestConfig.custom()
                    .setSocketTimeout(1000)
                    .setConnectTimeout(1000)
                    .setConnectionRequestTimeout(1000)
                    .setCookieSpec(CookieSpecs.STANDARD);
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            e.printStackTrace();
        }
    }

    public void setProxies(List<ProxyEntity> proxies) {
        try{
            lock.writeLock().lock();
            this.proxies.clear();
            this.proxies.addAll(proxies);
        } finally {
            lock.writeLock().unlock();
        }
    }

    protected ProxyEntity getProxyRandom() {
        try {
            lock.readLock().lock();
            if(proxies == null || proxies.isEmpty()) {
                return null;
            }
            return proxies.get(random.nextInt(proxies.size()));
        } finally {
            lock.readLock().unlock();
        }
    }
}
