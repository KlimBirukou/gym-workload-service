package com.epam.gym.workload.service.workload;

import com.epam.gym.workload.domain.workload.MonthWorkload;
import com.epam.gym.workload.domain.workload.TrainerWorkload;
import com.epam.gym.workload.domain.workload.YearWorkload;
import com.epam.gym.workload.repository.entity.MonthDocument;
import com.epam.gym.workload.repository.entity.TrainerDocument;
import com.epam.gym.workload.repository.entity.YearDocument;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface IWorkloadMapper {
    TrainerWorkload toWorkload(TrainerDocument document);
    YearWorkload toYearWorkload(YearDocument yearSummary);
    MonthWorkload toMonthWorkload(MonthDocument monthSummary);
}
