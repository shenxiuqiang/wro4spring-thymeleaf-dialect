package org.sevensource.wro4spring.thymeleaf;

import javax.servlet.http.HttpServletRequest;

import org.sevensource.wro4spring.config.DefaultAbstractWro4SpringConfiguration;
import org.sevensource.wro4spring.wro4j.development.GroupPerFileGroupExtractor;
import org.springframework.context.annotation.Bean;

import ro.isdc.wro.model.group.GroupExtractor;

/**
 * Extension of the default Wro4Spring
 * {@link DefaultAbstractWro4SpringConfiguration} configuration, which
 * <ul>
 * <li>Creates a {@link Wro4jDialectConfiguration}
 * <li>
 * Configures an enhanced {@link GroupPerFileGroupExtractor}, that strips the
 * {@link Wro4jDialectConfiguration#getUriPrefix()} from the incoming request
 * uri.
 * </ul>
 * 
 * @author pgaschuetz
 * 
 */
public abstract class AbstractWro4Spring4ThymeleafConfiguration extends
		DefaultAbstractWro4SpringConfiguration {

	@Bean
	public Wro4jDialectConfiguration wro4jDialectConfiguration() {
		Wro4jDialectConfiguration configuration = new Wro4jDialectConfiguration();
		configuration.setDevelopment(isDevelopment());
		configuration.setUriPrefix("/static/bundles/");
		return configuration;
	}

	@Override
	@Bean
	public GroupExtractor groupExtractor() {
		if (isDevelopment()) {
			return new GroupPerFileGroupExtractor() {

				private Wro4jDialectConfiguration wro4jDialectConfiguration = wro4jDialectConfiguration();

				@Override
				protected String getUri(HttpServletRequest request) {
					String uri = super.getUri(request);
					if (uri.startsWith(wro4jDialectConfiguration.getUriPrefix())) {
						uri = uri.substring(wro4jDialectConfiguration
								.getUriPrefix().length());
					}
					return uri;
				}
			};
		} else {
			return super.groupExtractor();
		}
	}

}
