package com.olelllka.stories_service.repository;

import com.olelllka.stories_service.domain.entity.StoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryRepository extends MongoRepository<StoryEntity, String> {

    @Query("{ 'userId': ?0 }")
    Page<StoryEntity> findStoryByUserId(String id, Pageable pageable);

}