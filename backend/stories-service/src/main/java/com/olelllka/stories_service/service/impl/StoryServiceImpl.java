package com.olelllka.stories_service.service.impl;

import com.olelllka.stories_service.domain.entity.StoryEntity;
import com.olelllka.stories_service.repository.StoryRepository;
import com.olelllka.stories_service.rest.exception.NotFoundException;
import com.olelllka.stories_service.service.StoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class StoryServiceImpl implements StoryService {

    @Autowired
    private StoryRepository repository;

    @Override
    public Page<StoryEntity> getStoriesForUser(String id, Pageable pageable) {
        return repository.findStoryByUserId(id, pageable);
    }

    @Override
    public StoryEntity getSpecificStory(String storyId) {
        return repository.findById(storyId).orElseThrow(() -> new NotFoundException("Story with such id was not found."));
    }

    @Override
    public StoryEntity createStory(String userId, StoryEntity entity) {
        entity.setAvailable(true);
        entity.setUserId(userId);
        return repository.save(entity);
    }

    @Override
    public StoryEntity updateSpecificStory(String storyId, StoryEntity entity) {
        return repository.findById(storyId).map(story -> {
            Optional.ofNullable(entity.getImage()).ifPresent(story::setImage);
            entity.setAvailable(true);
            entity.setCreatedAt(new Date());
            return repository.save(story);
        }).orElseThrow(() -> new NotFoundException("Story with such id was not found."));
    }

    @Override
    public void deleteSpecificStory(String storyId) {
        repository.deleteById(storyId);
    }
}
