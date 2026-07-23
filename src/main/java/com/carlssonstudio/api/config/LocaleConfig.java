package com.carlssonstudio.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

/**
 * Resolves the request locale from the Accept-Language header — this is a
 * stateless REST API, so there's no session/cookie to persist a choice.
 * Defaults to English when the header is absent or requests a locale we
 * don't ship; "id" (Bahasa Indonesia) is the only other supported locale
 * today, used by the Next.js /start-a-project questionnaire.
 */
@Configuration
public class LocaleConfig {

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.ENGLISH);
        resolver.setSupportedLocales(List.of(Locale.ENGLISH, Locale.forLanguageTag("id")));
        return resolver;
    }
}
