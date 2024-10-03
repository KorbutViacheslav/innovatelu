package org.example;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {
    /**
     * Storage where we store documents
     */
    private final Map<String, Document> documentsStorage = new HashMap<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if (document.getId() == null || document.getId().isEmpty()) {
            document.setId(UUID.randomUUID().toString());
            document.setCreated(Instant.now());
        }

        Document existingDocument = documentsStorage.get(document.getId());
        if (existingDocument != null) {
            document.setCreated(existingDocument.getCreated());
        }

        documentsStorage.put(document.getId(), document);
        return document;

    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        if (request == null) {
            return new ArrayList<>(documentsStorage.values());
        }

        return documentsStorage.values().stream()
                .filter(doc -> matchesTitlePrefixes(doc, request.getTitlePrefixes()))
                .filter(doc -> matchesContainsContents(doc, request.getContainsContents()))
                .filter(doc -> matchesAuthorIds(doc, request.getAuthorIds()))
                .filter(doc -> isWithinTimeRange(doc, request.getCreatedFrom(), request.getCreatedTo()))
                .toList();
    }

    /**
     * Checks if the document's title starts with one of the specified prefixes.
     *
     * @param doc           - the document being checked.
     * @param titlePrefixes - a list of prefixes for the document title.
     * @return true if the prefix list is empty or the document's title starts with one of them.
     * Otherwise, returns false.
     */
    private boolean matchesTitlePrefixes(Document doc, List<String> titlePrefixes) {
        if (titlePrefixes == null || titlePrefixes.isEmpty()) {
            return true;
        }
        return titlePrefixes.stream()
                .anyMatch(prefix -> doc.getTitle() != null && doc.getTitle().startsWith(prefix));
    }

    /**
     * Checks if the document's content contains any of the specified substrings.
     *
     * @param doc              - the document being checked.
     * @param containsContents - a list of substrings that should be present in the document's content.
     * @return true if the content contains at least one of the substrings or if the list is empty or null.
     */
    private boolean matchesContainsContents(Document doc, List<String> containsContents) {
        if (containsContents == null || containsContents.isEmpty()) {
            return true;
        }
        return containsContents.stream()
                .anyMatch(content -> doc.getContent() != null && doc.getContent().contains(content));
    }

    /**
     * Checks if the document's author matches one of the specified author IDs.
     *
     * @param doc       - the document being checked.
     * @param authorIds - a list of author IDs.
     * @return true if the document's author is in the list of author IDs or if the list is empty or null.
     */
    private boolean matchesAuthorIds(Document doc, List<String> authorIds) {
        if (authorIds == null || authorIds.isEmpty()) {
            return true;
        }
        return doc.getAuthor() != null &&
                authorIds.contains(doc.getAuthor().getId());
    }

    /**
     * Checks if the document was created within the specified time range.
     *
     * @param doc         - the document being checked.
     * @param createdFrom - the start of the time range (inclusive).
     * @param createdTo   - the end of the time range (inclusive).
     * @return true if the document was created within the specified range or if both dates are null.
     */
    private boolean isWithinTimeRange(Document doc, Instant createdFrom, Instant createdTo) {
        if (createdFrom != null && doc.getCreated().isBefore(createdFrom))
            return false;
        else if (createdTo != null && doc.getCreated().isAfter(createdTo))
            return false;
        else return true;
    }


    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        return Optional.ofNullable(documentsStorage.get(id));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}