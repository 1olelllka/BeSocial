package com.olelllka.stories_service.controller;

import com.olelllka.stories_service.domain.dto.StoryDto;
import com.olelllka.stories_service.domain.entity.StoryEntity;
import com.olelllka.stories_service.mapper.StoryMapper;
import com.olelllka.stories_service.service.StoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/stories")
public class StoryController {

    @Autowired
    private StoryService service;
    @Autowired
    private StoryMapper<StoryEntity, StoryDto> mapper;

    @GetMapping("/users/{user_id}")
    public ResponseEntity<Page<StoryDto>> getAllStoriesForUser(@PathVariable String user_id,
                                                               Pageable pageable) {
        Page<StoryEntity> entities = service.getStoriesForUser(user_id, pageable);
        Page<StoryDto> result = entities.map(mapper::toDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
