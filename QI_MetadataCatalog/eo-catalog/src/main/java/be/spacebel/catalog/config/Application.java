package be.spacebel.catalog.config;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;

import be.spacebel.catalog.utils.Constants;
import be.spacebel.catalog.utils.WebService;


@EnableAutoConfiguration(exclude = { SolrAutoConfiguration.class, ErrorMvcAutoConfiguration.class})
@ComponentScan(basePackages = "be.spacebel.catalog")
@EnableCaching
public class Application extends SpringBootServletInitializer {
	
	@Value("${solr.url:http://localhost:8983/solr/}")
	private String solrUrl;

	@Value("${geonames.user}")
	private String geonameUser;

	@Value("${http.proxy.server}")
	private String httpProxyServer;

	@Value("${http.proxy.port}")
	private Integer httpProxyPort;
	
	@Value("${zk.url:#{null}}")
	private Optional<String> zkUrl; //using this value will result in using the cloud solr client

	@Value("${cache.osdd.expiration:120}")
	private Integer osddCacheExpiration;

	@Value("${cache.searchable.expiration:55}")
	private Integer searchableCacheExpiration;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(Application.class, args);
	}
	
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
	{
	    return application.sources(new Class[] { Application.class });
	}

	@PostConstruct
	public void setupGeonames(){
		WebService.setUserName(geonameUser);
		if(org.apache.commons.lang.StringUtils.isNotBlank(httpProxyServer)){
			java.net.SocketAddress sa = new java.net.InetSocketAddress(httpProxyServer, httpProxyPort);
			java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, sa);
			WebService.setProxy(proxy);
		}
	}

	@Bean
	public CacheManager cacheManager(Ticker ticker) {
		CaffeineCache osddOfSeriesCache = buildCache("osddOfSeries", ticker, osddCacheExpiration);
		CaffeineCache defaultOSDDCache = buildCache("defaultOSDD", ticker, osddCacheExpiration);
		CaffeineCache searchableSeriesCache = buildCache("searchableSeries", ticker, searchableCacheExpiration);
		SimpleCacheManager manager = new SimpleCacheManager();
		manager.setCaches(Arrays.asList(osddOfSeriesCache, defaultOSDDCache, searchableSeriesCache));
		return manager;
	}

	private CaffeineCache buildCache(String name, Ticker ticker, int minutesToExpire) {
		return new CaffeineCache(name, Caffeine.newBuilder()
				.expireAfterWrite(minutesToExpire, TimeUnit.MINUTES)
				.maximumSize(100)
				.ticker(ticker)
				.build());
	}

	@Bean
	public Ticker ticker() {
		return Ticker.systemTicker();
	}

	@Bean("dataset")
	public SolrClient datasetSolrClient() {
		return this.createSolrClient(Constants.DATASET);
	}
	
	@Bean("series")
	public SolrClient seriesSolrClient() {
		return this.createSolrClient(Constants.SERIES);
	}
	
	private SolrClient createSolrClient(String collection) {
		if(zkUrl.isPresent()) {
			CloudSolrClient.Builder cloudBuilder = new CloudSolrClient.Builder().withZkHost(zkUrl.get());
			CloudSolrClient client = cloudBuilder.build();
			client.setDefaultCollection(collection);
			return client;
		} else {
			HttpSolrClient.Builder builder = 
					new HttpSolrClient.Builder(StringUtils.appendIfMissing(solrUrl, "/") + collection);
			return builder.build();
		}
	}
}
