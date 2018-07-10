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
    compile 'com.github.transferwise.url-locale:url-locale-starter:3.0.0'
}
```

If you want to use the servlet filters by your own avoiding auto-configuration, just include the `url-locale-core` dependency

```gradle
compile 'com.github.transferwise.url-locale:url-locale-core:3.0.0'
```

## Usage

The package includes all the necessary auto-wiring for Spring Boot, so there is no need to do any extra work apart from the configuration.

### Available locales

You can inject the locale mapping in any service or controller you might need it.

```java
@Autowired
private Map<String, Locale> localeMapping;
```

### Available request parameters

You also have the locale and the locale mapping available in requests.

#### Spring controllers


```java
@Controller
@RequestMapping("/{locale:[a-z]{2}}")
class MyController {
    @GetMapping("/do-something")
    public String doSomething(@RequestAttribute("urlLocaleMapping") String urlLocaleMapping) {
        if (urlLocaleMapping.equals("gb")) {
            return doSomethingForUK();
        }
        
        return "view";
    }
}
```

#### Thymeleaf templates

```html
<div th:if="${locale == 'gb'}">
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

* The `fallback` is the default locale it would be inferred when there is no mapping present in the URL.
* The `mapping` is a key-value configuration for the locale mappings you'll want to offer in your app. 
