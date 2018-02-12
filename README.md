# URL Locale [![CircleCI](https://circleci.com/gh/transferwise/url-locale/tree/master.svg?style=shield)](https://circleci.com/gh/transferwise/url-locale/tree/master) [![GitHub release](https://jitpack.io/v/transferwise/url-locale.svg)](https://github.com/transferwise/url-locale/releases/latest)

URL locale servlet filters and auto-configuration to setup your application locale through the URL.

## Installation

Just add the following configuration to your `build.gradle` file

```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    compile 'com.github.transferwise.url-locale:url-locale-starter:2.1.4'
}
```

If you want to use the servlet filters by your own avoiding auto-configuration, just include the `url-locale-core` dependency

```gradle
compile 'com.github.transferwise.url-locale:url-locale-core:2.1.4'
```

## Usage

The package includes all the necessary auto-wiring for Spring Boot, so there is no need to do any extra work apart from the configuration.

### Localized links

If you are using a template engine like Thymeleaf, links on the form `@{/home}` will be automatically translated to `/gb/home` based on the locale of the request.

### Localized routes

There is no need to handle localized URLs in your application, your controllers will be agnostic of the presence of a locale in the path.

### Available locales

You can inject the locale mapping in any service or controller you might need it.

```java
@Autowired
private Map<String, Locale> localeMapping;
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

* The `fallback` is the default locale it would be inferred when there is no mapping present in the URL.
* The `mapping` is a key-value configuration for the locale mappings you'll want to offer in your app. 
