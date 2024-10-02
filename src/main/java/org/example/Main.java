package org.example;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}

/*
import java.time.Instant;
import java.util.*;
        import java.util.stream.Collectors;

public class DocumentManager {
    private final Map<String, Document> documents = new HashMap<>();

    public Document save(Document document) {
        if (document.getId() == null) {
            document = document.toBuilder()
                    .id(UUID.randomUUID().toString())
                    .build();
        }

        if (document.getCreated() == null) {
            document = document.toBuilder()
                    .created(Instant.now())
                    .build();
        }

        documents.put(document.getId(), document);
        return document;
    }

    public List<Document> search(SearchRequest request) {
        if (request == null) {
            return new ArrayList<>(documents.values());
        }

        return documents.values().stream()
                .filter(doc -> matchesTitlePrefixes(doc, request.getTitlePrefixes()))
                .filter(doc -> matchesContainsContents(doc, request.getContainsContents()))
                .filter(doc -> matchesAuthorIds(doc, request.getAuthorIds()))
                .filter(doc -> isWithinTimeRange(doc, request.getCreatedFrom(), request.getCreatedTo()))
                .collect(Collectors.toList());
    }

    public Optional<Document> findById(String id) {
        return Optional.ofNullable(documents.get(id));
    }

    private boolean matchesTitlePrefixes(Document doc, List<String> titlePrefixes) {
        if (titlePrefixes == null || titlePrefixes.isEmpty()) {
            return true;
        }
        return titlePrefixes.stream()
                .anyMatch(prefix -> doc.getTitle() != null && doc.getTitle().startsWith(prefix));
    }

    private boolean matchesContainsContents(Document doc, List<String> containsContents) {
        if (containsContents == null || containsContents.isEmpty()) {
            return true;
        }
        return containsContents.stream()
                .anyMatch(content -> doc.getContent() != null && doc.getContent().contains(content));
    }

    private boolean matchesAuthorIds(Document doc, List<String> authorIds) {
        if (authorIds == null || authorIds.isEmpty()) {
            return true;
        }
        return doc.getAuthor() != null &&
                authorIds.contains(doc.getAuthor().getId());
    }

    private boolean isWithinTimeRange(Document doc, Instant from, Instant to) {
        if (doc.getCreated() == null) {
            return false;
        }
        if (from != null && doc.getCreated().isBefore(from)) {
            return false;
        }
        return to == null || !doc.getCreated().isAfter(to);
    }
}*/
