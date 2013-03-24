package org.sevensource.wro4spring.thymeleaf.dialect.processor;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.sevensource.wro4spring.thymeleaf.Wro4jDialectConfiguration;
import org.sevensource.wro4spring.wro4j.WroContextSupport;
import org.sevensource.wro4spring.wro4j.WroContextSupport.ContextTemplate;
import org.sevensource.wro4spring.wro4j.development.GroupPerFileModelTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.attr.AbstractUnescapedTextChildModifierAttrProcessor;

import ro.isdc.wro.config.Context;
import ro.isdc.wro.http.support.ServletContextAttributeHelper;
import ro.isdc.wro.manager.WroManager;
import ro.isdc.wro.model.WroModel;
import ro.isdc.wro.model.WroModelInspector;
import ro.isdc.wro.model.group.Group;
import ro.isdc.wro.model.resource.Resource;
import ro.isdc.wro.model.resource.ResourceType;

public abstract class AbstractWro4jAttrProcessor extends
		AbstractUnescapedTextChildModifierAttrProcessor {

	private static final int PRECEDENCE = 900;

	private final static int ASSUMED_URI_LENGTH_PER_RESOURCE = 75;

	private final static Logger logger = LoggerFactory
			.getLogger(AbstractWro4jAttrProcessor.class);

	private Wro4jDialectConfiguration wro4jDialectConfiguration;
	private WroContextSupport wroContextSupport;

	private final ResourceType resourceType = getWro4jResourceType();

	private ServletContextAttributeHelper servletContextAttributeHelper;

	public AbstractWro4jAttrProcessor(
			Wro4jDialectConfiguration wro4jDialectConfiguration,
			WroContextSupport wroContextSupport,
			IAttributeNameProcessorMatcher matcher) {
		super(matcher);
		this.wro4jDialectConfiguration = wro4jDialectConfiguration;
		this.wroContextSupport = wroContextSupport;
	}

	@Override
	public int getPrecedence() {
		return PRECEDENCE;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	protected final String getText(final Arguments arguments,
			final Element element, final String attributeName) {

		initialize(arguments);

		final StringBuilder buffer = new StringBuilder(
				ASSUMED_URI_LENGTH_PER_RESOURCE);

		IWebContext context = (IWebContext) arguments.getContext();

		final HttpServletRequest request = context.getHttpServletRequest();
		final HttpServletResponse response = context.getHttpServletResponse();
		wroContextSupport.doInContext(request, response, new ContextTemplate() {

			@Override
			public void execute() {
				final String requestedGroupname = element
						.getAttributeValue(attributeName);
				final Group requestedGroup = getGroup(requestedGroupname);

				final List<String> groupNames = new ArrayList<String>();

				if (!wro4jDialectConfiguration.isDevelopment()) {
					groupNames.add(requestedGroup.getName());
				} else {
					// in development mode, add a group for each resource of the
					// requested group
					for (Resource resource : requestedGroup.getResources()) {
						if (resourceType.equals(resource.getType())) {
							String fileGroupname = GroupPerFileModelTransformer
									.filenameToGroupname(resource.getUri());
							groupNames.add(fileGroupname);
						}
					}
				}

				for (String renderGroupName : groupNames) {
					String uri = getWroManager().encodeVersionIntoGroupPath(
							renderGroupName, resourceType,
							wro4jDialectConfiguration.isDevelopment());
					buffer.append(getRenderedAttribute(encodeUri(uri), true));
				}
			}
		});

		return buffer.toString();
	}

	/**
	 * initialize the {@link ServletContextAttributeHelper} if it is not set
	 * yet.
	 * 
	 * @param p_arguments
	 */
	private Object initLock = new Object();

	private void initialize(Arguments p_arguments) {
		if (servletContextAttributeHelper == null) {
			synchronized (initLock) {

				if (logger.isDebugEnabled()) {
					logger.debug("Initializing ServletContextAttributeHelper and FilterConfig");
				}

				ServletContext servletContext;

				if (p_arguments.getContext() instanceof IWebContext) {
					servletContext = ((IWebContext) p_arguments.getContext())
							.getServletContext();
				} else {
					throw new IllegalArgumentException(
							"Thymeleaf Context is not of type IWebContext, but "
									+ p_arguments.getContext().getClass()
											.getName());
				}

				if (servletContextAttributeHelper == null) {
					servletContextAttributeHelper = new ServletContextAttributeHelper(
							servletContext);
				}

			}
		}
	}

	/**
	 * Builds a URL accoring to the {@link Wro4jDialectConfiguration}
	 * 
	 * @param uri
	 *            a resources URI as returned from wro4j
	 * @return the resources full URL including domain, context path and prefix
	 * @see Wro4jDialectConfiguration#getCdnDomain()
	 * @see Wro4jDialectConfiguration#getContextPath()
	 * @see Wro4jDialectConfiguration#getUriPrefix()
	 */
	protected String encodeUri(String uri) {
		StringBuilder encoded = new StringBuilder(
				ASSUMED_URI_LENGTH_PER_RESOURCE);

		if (!StringUtils.isEmpty(wro4jDialectConfiguration.getCdnDomain())) {
			encoded.append("//").append(
					wro4jDialectConfiguration.getCdnDomain());
		}

		if (!StringUtils.isEmpty(wro4jDialectConfiguration.getContextPath())) {
			encoded.append(wro4jDialectConfiguration.getContextPath());
		}

		if (!StringUtils.isEmpty(wro4jDialectConfiguration.getUriPrefix())) {
			encoded.append(wro4jDialectConfiguration.getUriPrefix());
		}

		encoded.append(uri);

		return encoded.toString();
	}

	/**
	 * Find a wro4j {@link Group} by its name
	 * 
	 * @param groupname
	 *            the requested {@link Group}
	 * @return
	 */
	private Group getGroup(String groupname) {
		WroModel model = getWroManager().getModelFactory().create();

		final WroModelInspector modelInspector = new WroModelInspector(model);
		final Group group = modelInspector.getGroupByName(groupname);

		if (group == null) {
			IllegalArgumentException iae = new IllegalArgumentException(
					"Invalid groupname '" + groupname + "'");
			logger.error("Cannot find group", iae);
			throw iae;
		} else {
			return group;
		}
	}

	private WroManager getWroManager() {
		return servletContextAttributeHelper.getManagerFactory().create();
	}

	/**
	 * Get the HTML for the requested URI
	 * 
	 * @param uri
	 * @param isDevelopment
	 * @return
	 */
	protected abstract String getRenderedAttribute(String uri,
			boolean isDevelopment);

	/**
	 * Specify the {@link ResourceType} of this implementation
	 * 
	 * @return
	 */
	protected abstract ResourceType getWro4jResourceType();
}