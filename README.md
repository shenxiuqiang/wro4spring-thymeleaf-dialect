# wro4spring-thymeleaf-dialect

A <a href="http://www.thymeleaf.org">Thymeleaf</a> dialect for use with
<a href="https://code.google.com/p/wro4j/">wro4j</a>.

## Overview
The dialect is based upon <a href="https://github.com/sevensource/wro4spring">wro4spring</a> and
<a href="https://github.com/sevensource/thymeleaf4spring">thymeleaf4spring</a> and makes heavy use
of the Spring framework. While I don't see a good reason to not use these dependencies - ;-) - the
dialect could be easily tweaked to be used without them.

## So, what does it do?
The dialect adds support for CSS and Javascript resources and features resource separation in development mode.

In development mode, resources are processed by wro4j, but without minification and concatenation. Therefore, you
can still use any of wro4j processors, such as LESS, CSS Variables, etc., but changes become visible instantly
and it is a lot easier to debug.

* Given the following _wro.xml_ group definition
```xml
<?xml version="1.0" encoding="UTF-8"?>
<groups xmlns="http://www.isdc.ro/wro" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.isdc.ro/wro wro.xsd">
    <group name="default">
      <css>/static/css/red.css</css>
      <css>/static/css/blue.css</css>
      <js>/static/js/libs/jquery.js</js>
      <js>/static/js/site.js</js>
    </group>
</groups>
```

* and the following Thymeleaf usage
```html
  <link rel="stylesheet" type="text/css" wro4j:style="default" />
  <script wro4j:script="default"></script>
```

* resolves in **development** mode to

```html
<link rel="stylesheet" type="text/css" href="/contextPath/static/bundles/56249f56d4...29026e2344/static/css/red.css" />
<link rel="stylesheet" type="text/css" href="/contextPath/static/bundles/a2b1848276...5b5eecb03/static/css/blue.css" />
<script src="/contextPath/static/bundles/56249f56d4b......34461a1f4fff07b7/static/js/libs/jquery.js"></script>
<script src="/contextPath/static/bundles/56249f56d4b......32421a1f426e234/static/js/site.js"></script>
```


* and in **production** mode to

```html
<link rel="stylesheet" type="text/css" href="/contextPath/static/bundles/562142432434545fbfb45fb6/default.css" />
<script src="/contextPath/static/bundles/56249f56d4b......32421a1f426e234/default.js"></script>
```

### JS Resource loader support
wro4spring-thymeleaf-dialect features easy support for Javascript resource loaders:
```html
<script th:inline="javascript">
	var files = /*[[${wro4j.js('default')}]]*/ [];
</script>
```
creates a JS array of Javascript resources in the default group.
 
## Getting started
1. configure wro4spring
2. Create a @Configuration file
```java
@Configuration
public class ThymeleafMVCConfiguration extends DefaultThymeleaf4SpringWroDialectConfiguration {
	@Override
	protected boolean isDevelopment() {
		return true;
	}
}
```
3. @Import the configuration into your DispatcherServlet application context.

**Note:** DefaultThymeleaf4SpringWroDialectConfiguration is an extension of
<a href="https://github.com/sevensource/thymeleaf4spring">thymeleaf4spring</>s @Configuration classes. See
<a href="https://github.com/sevensource/thymeleaf4spring">thymeleaf4spring</> for additional information.