package com.epam.gym.workload.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ConversionConfigurationTest {

    @InjectMocks
    private ConversionConfiguration testObject;

    @Test
    void conversionService_shouldReturnConversionService_whenConvertersAreEmpty() {
        var result = testObject.conversionService(Set.of());

        assertNotNull(result);
        assertInstanceOf(ConversionService.class, result);
    }

    @Test
    void conversionService_shouldSupportRegisteredConverter_whenConverterProvided() {
        var converter = new TestStringToIntegerConverter();

        var result = testObject.conversionService(Set.of(converter));

        assertTrue(result.canConvert(String.class, Integer.class));
    }

    private static class TestStringToIntegerConverter
        implements org.springframework.core.convert.converter.Converter<String, Integer> {
        @Override
        public Integer convert(String source) {
            return Integer.parseInt(source);
        }
    }
}
