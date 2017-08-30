# URL Locale [![CircleCI](https://circleci.com/gh/transferwise/url-locale/tree/master.svg?style=shield)](https://circleci.com/gh/transferwise/url-locale/tree/master)

URL locale auto-configurer for Spring Boot.

## Installation

Just add the following configuration to your `build.gradle` file

```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    compile 'com.github.transferwise:url-locale:1.0.0'
}
```