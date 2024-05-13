package com.vodafone.springboot.crud.adapters.api.mapper;

import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.vodafone.springboot.crud.adapters.api.dto.SensorEventDto;
import com.vodafone.springboot.crud.adapters.api.dto.SensorEventRqDto;
import com.vodafone.springboot.crud.domain.model.SensorEventModel;

@Mapper(componentModel = "spring")
public abstract class SensorEventMapper {

    public abstract SensorEventDto mapRqDtoToDto(SensorEventRqDto request);

    public abstract SensorEventModel mapDtoToModel(SensorEventDto request);

    public abstract SensorEventDto mapModelToRto(SensorEventModel model);

    public List<SensorEventDto> mapListModelsToListDto(List<SensorEventModel> modelList) {
        List<SensorEventDto> list = new ArrayList<>(modelList != null ? modelList.size() : 0);
        for (Object m : (modelList != null) ? modelList : List.of()) {
            list.add(mapModelToRto((SensorEventModel) m));
        }
        return list;
    }

    OffsetDateTime mapToOffsetDateTime(final LocalDateTime localDateTime) {
        return Objects.nonNull(localDateTime)
                ? OffsetDateTime.of(localDateTime, ZoneOffset.UTC)
                : null;
    }

    LocalDateTime mapToLocalDateTime(final OffsetDateTime offsetDateTime) {
        return Objects.nonNull(offsetDateTime)
                ? offsetDateTime.toLocalDateTime()
                : null;
    }

}
