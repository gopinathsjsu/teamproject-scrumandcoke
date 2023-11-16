package com.scrumandcoke.movietheaterclub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scrumandcoke.movietheaterclub.model.TheaterScreenEntity;

@Repository
public interface TheaterScreenRepository extends JpaRepository<TheaterScreenEntity, Integer>{

}