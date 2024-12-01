package com.olelllka.stories_service.service.impl;

import com.olelllka.stories_service.domain.entity.StoryEntity;
import com.olelllka.stories_service.repository.StoryRepository;
import com.olelllka.stories_service.service.StoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class StoryServiceImpl implements StoryService {

    @Autowired
    private StoryRepository repository;

    @Override
    public Page<StoryEntity> getStoriesForUser(String id, Pageable pageable) {
        return repository.findStoryByUserId(id, pageable);
    }
}
