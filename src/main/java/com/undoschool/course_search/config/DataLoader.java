package com.undoschool.course_search.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.undoschool.course_search.document.CourseDocument;
import com.undoschool.course_search.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.data.elasticsearch.core.suggest.Completion;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final CourseRepository repository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {

        long count = repository.count();

        if (count > 0) {
            System.out.println("Elasticsearch already contains data. Skipping indexing.");
            return;
        }

        System.out.println("Loading sample data into Elasticsearch...");

        InputStream inputStream =
                new ClassPathResource("sample-courses.json").getInputStream();

        List<CourseDocument> courses =
                objectMapper.readValue(inputStream,
                        new TypeReference<List<CourseDocument>>() {});

        courses.forEach(course ->
                course.setSuggest(new Completion(new String[]{course.getTitle()}))
        );

        repository.saveAll(courses);

        System.out.println("Courses indexed successfully! Total indexed: " + courses.size());
    }
}