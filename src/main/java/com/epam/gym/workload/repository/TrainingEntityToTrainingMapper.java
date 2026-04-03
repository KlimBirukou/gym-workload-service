package com.epam.gym.workload.repository;

import com.epam.gym.workload.configuration.IMapStructConfiguration;
import com.epam.gym.workload.domain.Training;
import lombok.NonNull;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.extensions.spring.DelegatingConverter;
import org.springframework.core.convert.converter.Converter;

@Mapper(config = IMapStructConfiguration.class)
public interface TrainingEntityToTrainingMapper extends Converter<@NonNull TrainingEntity, Training> {

    @Override
    Training convert(TrainingEntity entity);

    @InheritInverseConfiguration
    @DelegatingConverter
    TrainingEntity convert(Training training);
}
