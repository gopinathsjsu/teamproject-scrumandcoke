package com.scrumandcoke.movietheaterclub.repository;

import com.scrumandcoke.movietheaterclub.entity.TheaterScreenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TheaterScreenRepository extends JpaRepository<TheaterScreenEntity, Integer>{

    List<TheaterScreenEntity> findByLocationEntity_Id(Integer id);

}
