package com.scrumandcoke.movietheaterclub.repository;

import com.scrumandcoke.movietheaterclub.entity.LocationEntity;
import com.scrumandcoke.movietheaterclub.enums.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MultiplexRepository extends JpaRepository<LocationEntity, Integer> {
    List<LocationEntity> findByLocation(Location location);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Integer id);

}
