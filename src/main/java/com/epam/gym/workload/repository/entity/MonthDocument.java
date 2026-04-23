package com.epam.gym.workload.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthDocument {

    @Field("month")
    private int month;

    @Field("trainingSummaryDuration")
    @Builder.Default
    private int trainingSummaryDuration = 0;
}
