package com.undoschool.course_search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Suggestion;
import co.elastic.clients.elasticsearch._types.SortOrder;
import com.undoschool.course_search.document.CourseDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseSearchService {

    private final ElasticsearchOperations operations;
    private final ElasticsearchClient elasticsearchClient;

    // ==========================
    // 🔎 MAIN SEARCH (Production Grade)
    // ==========================
    public SearchHits<CourseDocument> search(
            String q,
            Integer minAge,
            Integer maxAge,
            String category,
            String type,
            Double minPrice,
            Double maxPrice,
            Instant startDate,
            String sort,
            int page,
            int size
    ) {

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(qb -> qb.bool(b -> {

                    // 🔥 Fuzzy full-text search
                    if (q != null && !q.isBlank()) {

                        b.should(s -> s.match(m -> m
                                .field("title")
                                .query(q)
                                .fuzziness("AUTO")
                                .boost(2.0f)
                        ));

                        b.should(s -> s.match(m -> m
                                .field("description")
                                .query(q)
                                .fuzziness("AUTO")
                        ));

                        b.minimumShouldMatch("1");
                    }

                    if (q == null || q.isBlank()) {
                        b.must(m -> m.matchAll(ma -> ma));
                    }

                    // 🎯 Exact term filters
                    if (category != null) {
                        b.filter(f -> f.term(t -> t.field("category").value(category)));
                    }

                    if (type != null) {
                        b.filter(f -> f.term(t -> t.field("type").value(type)));
                    }

                    // 💰 Price range (NUMBER RANGE)
                    if (minPrice != null || maxPrice != null) {
                        b.filter(f -> f.range(r -> r
                                .number(n -> {
                                    n.field("price");
                                    if (minPrice != null) n.gte(minPrice);
                                    if (maxPrice != null) n.lte(maxPrice);
                                    return n;
                                })
                        ));
                    }

                    // 👶 Age range
                    if (minAge != null) {
                        b.filter(f -> f.range(r -> r
                                .number(n -> n
                                        .field("maxAge")
                                        .gte(minAge.doubleValue())
                                )
                        ));
                    }

                    if (maxAge != null) {
                        b.filter(f -> f.range(r -> r
                                .number(n -> n
                                        .field("minAge")
                                        .lte(maxAge.doubleValue())
                                )
                        ));
                    }

                    // 📅 Date range (DATE RANGE)
                    if (startDate != null) {
                        b.filter(f -> f.range(r -> r
                                .date(d -> d
                                        .field("nextSessionDate")
                                        .gte(startDate.toString())
                                )
                        ));
                    }

                    return b;
                }))
                .withPageable(PageRequest.of(page, size))
                .withSort(s -> {
                    if ("priceAsc".equalsIgnoreCase(sort)) {
                        return s.field(f -> f.field("price").order(SortOrder.Asc));
                    } else if ("priceDesc".equalsIgnoreCase(sort)) {
                        return s.field(f -> f.field("price").order(SortOrder.Desc));
                    } else {
                        return s.field(f -> f.field("nextSessionDate").order(SortOrder.Asc));
                    }
                })
                .build();

        return operations.search(searchQuery, CourseDocument.class);
    }
    // ==========================
    // ✨ AUTOCOMPLETE SUGGESTER
    // ==========================
    public List<String> suggest(String prefix) throws Exception {

        SearchResponse<Void> response = elasticsearchClient.search(s -> s
                        .index("courses")
                        .suggest(su -> su
                                .suggesters("course-suggest", cs -> cs
                                        .prefix(prefix)
                                        .completion(c -> c
                                                .field("suggest")
                                                .size(10)
                                        )
                                )
                        ),
                Void.class
        );

        List<String> results = new ArrayList<>();

        List<Suggestion<Void>> suggestions =
                response.suggest().get("course-suggest");

        if (suggestions != null) {
            for (Suggestion<Void> suggestion : suggestions) {
                suggestion.completion().options()
                        .forEach(option ->
                                results.add(option.text())
                        );
            }
        }

        return results;
    }
}