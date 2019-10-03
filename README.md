# URL Locale [![CircleCI](https://circleci.com/gh/transferwise/url-locale/tree/master.svg?style=shield)](https://circleci.com/gh/transferwise/url-locale/tree/master) [![GitHub release](https://jitpack.io/v/transferwise/url-locale.svg)](https://github.com/transferwise/url-locale/releases/latest)

Spring components with optional auto-configuration that enables resolving a **locale** (e.g. _en-GB_) for a request based on a two character **URL locale** (e.g. _gb_) at the start of the HTTP request path.

For example, given mapping configuration from url locale _gb_ to locale _en-GB_, a request to `https://youdomain.com/gb/some-path` would resolve the locale as _en-GB_.
Paths matching the URL locale pattern but without locale mappings will result in a 404.

## Installation

Just add the following configuration to your `build.gradle` file

```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    compile 'com.github.transferwise.url-locale:url-locale-starter:3.1.0'
}
```

If you want to use the servlet filters by your own avoiding auto-configuration, just include the `url-locale-core` dependency

```gradle
compile 'com.github.transferwise.url-locale:url-locale-core:3.1.0'
```

## Url-locale usage

The package includes all the necessary auto-wiring for Spring Boot, so there is no need to do any extra work apart from the configuration.

### Available locales

You can inject the url locale to locale mapping in any service or controller you might need it.

```java
@Autowired
private Map<String, Locale> urlLocaleToLocaleMapping;
```

### Available request attributes

The url locale is also available as a request parameter, named `urlLocale`.

#### Spring controllers


```java
@Controller
@RequestMapping("/{urlLocale:[a-z]{2}}")
class MyController {
    @GetMapping("/do-something")
    public String doSomething(@RequestAttribute("urlLocale") String urlLocale) {
        if (urlLocale.equals("gb")) {
            return doSomethingForUK();
        }
        
        return "view";
    }
}
```

#### Thymeleaf templates

```html
<div th:if="${urlLocale == 'gb'}">
    Some stuff for UK 
</div>
```

## Configuration

You'll need to configure the url-locale mapping. In your `application.yml`

```yaml
url-locale:
  fallback: en-gb
  mapping:
    br: pt-BR
    de: de-DE
    es: es-ES
    fr: fr-FR
    gb: en-GB
    us: en-US
```

* The `fallback` is the default locale that will be inferred when there is no url locale found in the URL.
* The `mapping` is the url locale to locale mappings you want to offer in your app. 

## Hreflang and localised link usage

### Example configuration

In a project's application.yml file:

```yaml
hreflang:
  x-default: gb
  hreflangToUrlLocale:
    fr: fr
    de-CH: ch
    de-DE: de
    zh-Hant: zh-hk
```
