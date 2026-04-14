package com.epam.gym.workload.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

import java.util.Set;

@Configuration
public class ConversionConfiguration {

    @Bean
    public ConversionService conversionService(Set<Converter<?, ?>> converters) {
        var factory = new ConversionServiceFactoryBean();
        factory.setConverters(converters);
        factory.afterPropertiesSet();
        return factory.getObject();
    }
}
