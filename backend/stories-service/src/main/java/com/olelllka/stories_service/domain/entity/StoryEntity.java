package com.olelllka.stories_service.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;
import java.util.Date;

@Document(collection = "Story")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoryEntity {

    @Id
    private String id;
    private String image;
    private String userId;
    private Integer likes;
    private Boolean available;
    private Date createdAt;
}
