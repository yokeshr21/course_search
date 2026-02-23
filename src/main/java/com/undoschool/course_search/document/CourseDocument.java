package com.undoschool.course_search.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import org.springframework.data.elasticsearch.core.suggest.Completion;
import java.time.Instant;

@Data
@Document(indexName = "courses")
public class CourseDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Text)
    private String title;

    @CompletionField(maxInputLength = 100)
    private Completion suggest;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Keyword)
    private String type;

    @Field(type = FieldType.Keyword)
    private String gradeRange;

    @Field(type = FieldType.Integer)
    private Integer minAge;

    @Field(type = FieldType.Integer)
    private Integer maxAge;

    @Field(type = FieldType.Double)
    private Double price;

    @Field(type = FieldType.Date)
    private Instant nextSessionDate;
}