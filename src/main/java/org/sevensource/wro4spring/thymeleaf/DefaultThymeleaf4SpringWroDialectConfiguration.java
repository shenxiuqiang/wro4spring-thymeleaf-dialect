package org.sevensource.wro4spring.thymeleaf;

import java.util.Set;

import javax.inject.Inject;

import org.sevensource.thymeleaf4spring.DefaultAbstractThymeleafTilesConfiguration;
import org.sevensource.wro4spring.IWroModelAccessor;
import org.sevensource.wro4spring.WroContextSupport;
import org.sevensource.wro4spring.WroDeliveryConfiguration;
import org.sevensource.wro4spring.thymeleaf.dialect.Wro4jDialect;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.spring3.view.ThymeleafViewResolver;

/**
 * Adds {@link Wro4jDialect} to Thymeleafs configuration. Heavily relies upon
 * beans provided by {@link AbstractWro4Spring4ThymeleafConfiguration}
 * 
 * @see WroDeliveryConfiguration
 * @see AbstractWro4Spring4ThymeleafConfiguration
 * @author pgaschuetz
 * 
 */
public abstract class DefaultThymeleaf4SpringWroDialectConfiguration extends
		DefaultAbstractThymeleafTilesConfiguration {

	@Inject
	private WroDeliveryConfiguration wro4jDialectConfiguration;
	
	@Inject
	private WroContextSupport wroContextSupport;
	
	@Inject
	private IWroModelAccessor wroModelAccessor;
	

	@Override
	protected ThymeleafViewResolver configureViewResolver(
			ThymeleafViewResolver viewResolver) {
		
		//should we do this with an interceptor?
		viewResolver.addStaticVariable("wro4j", wroModelAccessor);
		return viewResolver;
	}
	
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
