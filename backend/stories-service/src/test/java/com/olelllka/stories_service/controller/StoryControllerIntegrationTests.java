package com.olelllka.stories_service.controller;

import com.olelllka.stories_service.TestDataUtil;
import com.olelllka.stories_service.TestcontainersConfiguration;
import com.olelllka.stories_service.domain.dto.CreateStoryDto;
import com.olelllka.stories_service.domain.entity.StoryEntity;
import com.olelllka.stories_service.service.StoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
public class StoryControllerIntegrationTests {

    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8.0");

    private MockMvc mockMvc;
    private StoryService service;
    private ObjectMapper objectMapper;

    @Autowired
    public StoryControllerIntegrationTests(MockMvc mockMvc, StoryService service) {
        this.mockMvc = mockMvc;
        this.service = service;
        this.objectMapper = new ObjectMapper();
    }

    static {
        mongoDBContainer.start();
    }

    @Test
    public void testThatGetAllStoriesForUserReturnsHttp200Ok() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/stories/users/123"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testThatGetSpecificStoryReturnsHttp404NotFoundIfDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/stories/1234"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testThatGetSpecificStoryReturnsHttp200OkIfExists() throws Exception {
        StoryEntity story = service.createStory("1234", TestDataUtil.createStoryEntity());
        mockMvc.perform(MockMvcRequestBuilders.get("/stories/" + story.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testThatCreateStoryForUserReturnsHttp400BadRequestIfIncorrectJson() throws Exception {
        CreateStoryDto createStoryDto = CreateStoryDto.builder().image("").build();
        String json = objectMapper.writeValueAsString(createStoryDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/stories/users/1234")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testThatCreateStoryForUserReturnsHttp201CreatedIfSuccessful() throws Exception {
        CreateStoryDto createStoryDto = CreateStoryDto.builder().image("New Image url").build();
        String json = objectMapper.writeValueAsString(createStoryDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/stories/users/1234")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.image").value("New Image url"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value("1234"));
    }

    @Test
    public void testThatUpdateStoryReturnsHttp400BadRequestIfIncorrectJson() throws Exception {
        CreateStoryDto createStoryDto = CreateStoryDto.builder().image("").build();
        String json = objectMapper.writeValueAsString(createStoryDto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/stories/1234")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testThatUpdateStoryReturnsHttp404IfStoryDoesNotExist() throws Exception {
        CreateStoryDto createStoryDto = CreateStoryDto.builder().image("Updated url").build();
        String json = objectMapper.writeValueAsString(createStoryDto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/stories/1234")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testThatUpdateStoryReturnsHttp200IfSuccessful() throws Exception {
        StoryEntity entity = service.createStory("1234", TestDataUtil.createStoryEntity());
        CreateStoryDto createStoryDto = CreateStoryDto.builder().image("Updated url").build();
        String json = objectMapper.writeValueAsString(createStoryDto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/stories/" + entity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.image").value("Updated url"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(true));
    }

    @Test
    public void testThatDeleteStoryReturnsHttp204NoContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/stories/1234"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
