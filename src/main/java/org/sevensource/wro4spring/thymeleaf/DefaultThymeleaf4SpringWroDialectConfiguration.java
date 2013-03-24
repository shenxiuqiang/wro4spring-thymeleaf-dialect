package org.sevensource.wro4spring.thymeleaf;

import java.util.Set;

import javax.inject.Inject;

import org.sevensource.thymeleaf4spring.DefaultThymeleafConfiguration;
import org.sevensource.wro4spring.thymeleaf.dialect.Wro4jDialect;
import org.sevensource.wro4spring.wro4j.WroContextSupport;
import org.thymeleaf.dialect.IDialect;

/**
 * Adds {@link Wro4jDialect} to Thymeleafs configuration. Heavily relies upon
 * beans provided by {@link AbstractWro4Spring4ThymeleafConfiguration}
 * 
 * @see Wro4jDialectConfiguration
 * @see AbstractWro4Spring4ThymeleafConfiguration
 * @author pgaschuetz
 * 
 */
public abstract class DefaultThymeleaf4SpringWroDialectConfiguration extends
		DefaultThymeleafConfiguration {

	/**
	 * instantiated by {@link AbstractWro4Spring4ThymeleafConfiguration}<br>
	 * {@link Wro4jDialect} gathers configuration settings, such as the path
	 * prefix from {@link Wro4jDialectConfiguration}
	 */
	@Inject
	private Wro4jDialectConfiguration wro4jDialectConfiguration;
	
	@Inject
	private WroContextSupport wroContextSupport;

	@Override
	protected Set<IDialect> getAdditionalDialects() {
		Set<IDialect> dialects = super.getAdditionalDialects();
		dialects.add(wro4jDialect());
		return dialects;
	}

	protected Wro4jDialect wro4jDialect() {
		Wro4jDialect dialect = new Wro4jDialect(wro4jDialectConfiguration, wroContextSupport);
		return dialect;
	}
}
