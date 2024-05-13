package com.vodafone.springboot.crud.adapters.database.repository;

import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.vodafone.springboot.crud.domain.model.SensorEventModel;

@Repository
public interface SensorEventRepository extends MongoRepository<SensorEventModel, String>{

}
