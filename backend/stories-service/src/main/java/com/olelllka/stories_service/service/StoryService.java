package com.olelllka.stories_service.service;

import com.olelllka.stories_service.domain.dto.StoryDto;
import com.olelllka.stories_service.domain.entity.StoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoryService {
    Page<StoryEntity> getStoriesForUser(String id, Pageable pageable);
}