package com.epam.gym.workload.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YearDocument {

    @Field("year")
    private int year;

    @Field("months")
    @Builder.Default
    private List<MonthDocument> months = new ArrayList<>();
}
