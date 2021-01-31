package be.spacebel.catalog.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import be.spacebel.catalog.controllers.CatalogController;

@Configuration
public class JerseyConfig extends ResourceConfig {

	public JerseyConfig() {
		register(CatalogController.class);
		property("jersey.config.servlet.filter.staticContentRegex", "/images/.*");
	}
}
