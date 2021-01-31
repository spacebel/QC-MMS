package be.spacebel.eoportal.client.util;

import be.spacebel.eoportal.client.business.data.Constants;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Map;
import javax.net.ssl.SSLContext;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * Utility class to invoke HTTP URL
 *
 * @author mng
 */
public class HTTPInvoker implements Serializable {

    private final static Logger LOG = Logger.getLogger(HTTPInvoker.class);
    private static final String HTTP_GET_DETAILS_URL = "url";

    public static String invokeGET(String location, Map<String, String> details) throws IOException {
        LOG.debug("enter invokeGET(location=" + location + ")");
        /*
         * Encode the parameter values
         */
        if (StringUtils.isNotEmpty(location)) {
            String baseUrl = StringUtils.substringBefore(location, "?");
            String queryString = StringUtils.substringAfter(location, "?");
            if (StringUtils.isNotEmpty(queryString)) {
                String[] paramArr = StringUtils.split(queryString, "&");
                /*
                 * LOG.debug("QueryString before parameters values: " +
                 * queryString);
                 */
                queryString = "";

                for (String param : paramArr) {
                    String key = StringUtils.substringBefore(param, "=");
                    String value = StringUtils.substringAfter(param, "=");
                    if (StringUtils.isNotEmpty(value)) {
                        /*
                         * decode the value first
                         */
                        value = URLDecoder.decode(value, "UTF-8");
                        /*
                         * encode again the value
                         */
                        value = URLEncoder.encode(value, "UTF-8");
                    }
                    queryString += key + "=" + value + "&";
                }
                /*
                 * Remove character "&" at the end of the string
                 */
                queryString = queryString.substring(0, queryString.length() - 1);

                location = baseUrl + "?" + queryString;
            }
        }

        URL url = new URL(location);
        CloseableHttpClient httpClient = null;
        String result = null;
        try {
            // disable SNI of Java 7 on runtime to avoid exception
            // unrecognized_name
            System.setProperty("jsse.enableSNIExtension", "false");

            HttpGet httpGet = new HttpGet(location);
            int timeout = 5 * 60 * 1000;
            LOG.debug("timeout: " + timeout);

            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout)
                    .setConnectTimeout(timeout).setConnectionRequestTimeout(timeout).build();
            httpGet.setConfig(requestConfig);

            if (details != null) {
                details.put(HTTP_GET_DETAILS_URL, location);
            }

            if ("https".equalsIgnoreCase(url.getProtocol())) {
                LOG.debug("Invoke HTTPS GET: " + location);
                TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
                    @Override
                    public boolean isTrusted(X509Certificate[] certificate, String authType) {
                        /*
                         * trust all certificates
                         */
                        return true;
                    }
                };
                try {
                    PoolingHttpClientConnectionManager cm = createDefaultCM(acceptingTrustStrategy);
                    cm.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(timeout).build());

                    httpClient = HttpClients
                            .custom()
                            .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                            .setSSLContext(
                                    new SSLContextBuilder().loadTrustMaterial(
                                            acceptingTrustStrategy).build())
                            .setDefaultRequestConfig(requestConfig)
                            .setConnectionManager(cm)
                            .build();
                } catch (NoSuchAlgorithmException e) {
                    LOG.debug("HTTPS invoke exception: " + e);
                    throw new IOException(e);
                } catch (KeyStoreException e) {
                    LOG.debug("HTTPS invoke exception: " + e);
                    throw new IOException(e);
                } catch (KeyManagementException e) {
                    LOG.debug("HTTPS invoke exception: " + e);
                    throw new IOException(e);
                }
            } else {
                LOG.debug("Invoke HTTP GET: " + location);
                //httpClient = HttpClients.createDefault();
                httpClient = HttpClientBuilder.create()
                        .setDefaultRequestConfig(requestConfig)
                        .build();
            }
            CloseableHttpResponse response = httpClient.execute(httpGet);
            try {
                int status = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                String respStr = null;
                if (entity != null) {
                    respStr = EntityUtils.toString(entity);
                    EntityUtils.consume(entity);
                }
                LOG.debug("status = " + status);
                if (status >= 200 && status < 300) {
                    result = respStr;
                } else {
                    if (details != null) {
                        details.put(Constants.HTTP_GET_DETAILS_ERROR_CODE, "" + status);
                        details.put(Constants.HTTP_GET_DETAILS_ERROR_MSG, respStr);
                    }
                }
            } finally {
                if (response != null) {
                    response.close();
                }
            }
        } catch (ConnectTimeoutException e) {
            LOG.debug("Connect timeout exception: " + e);
            if (details != null) {
                details.put(Constants.HTTP_GET_DETAILS_ERROR_CODE, "408");
                details.put(Constants.HTTP_GET_DETAILS_ERROR_MSG, "Request Timeout");
            }
        } catch (UnknownHostException e) {
            LOG.debug("Unknown host exception: " + e);
            details.put(Constants.HTTP_GET_DETAILS_ERROR_CODE, "404");
            details.put(Constants.HTTP_GET_DETAILS_ERROR_MSG, "Unknown host: " + e.getMessage());
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
        }
        //log.debug("result:" + result);
        return result;
    }

    private static PoolingHttpClientConnectionManager createDefaultCM(TrustStrategy acceptingTrustStrategy)
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        // init the SSL & plain connection socket factories
        SSLContext sslCtxt = new SSLContextBuilder().loadTrustMaterial(acceptingTrustStrategy).build();
        SSLConnectionSocketFactory sslcsf = new SSLConnectionSocketFactory(sslCtxt, NoopHostnameVerifier.INSTANCE);
        ConnectionSocketFactory plainsf = new PlainConnectionSocketFactory();

        // parameters for the connection manager
        int maxTotal = 200;
        int defaultMaxPerRoute = 4;

        // need to provide a SSLConnectionSocketFactory to the connection manager in order to handle HTTPS scheme
        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", plainsf)
                .register("https", sslcsf)
                .build();

        // init the connection manager
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(reg);
        connManager.setMaxTotal(maxTotal);
        connManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        return connManager;
    }

    public static String invokePOST(String urlString, String data) throws IOException {
        InputStream is = null;
        String result = null;
        try {
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream());
            os.write(data);
            os.flush();
            os.close();
            result = IOUtils.toString(conn.getInputStream());
        } finally {
            IOUtils.closeQuietly(is);
        }

        return result;
    }
}
