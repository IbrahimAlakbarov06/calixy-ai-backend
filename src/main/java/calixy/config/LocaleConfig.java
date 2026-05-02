package calixy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

@Configuration
public class LocaleConfig implements WebMvcConfigurer {

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setSupportedLocales(List.of(
                Locale.forLanguageTag("en"),
                Locale.forLanguageTag("az"),
                Locale.forLanguageTag("ru"),
                Locale.forLanguageTag("tr")
        ));
        resolver.setDefaultLocale(Locale.ENGLISH);
        return resolver;
    }
}