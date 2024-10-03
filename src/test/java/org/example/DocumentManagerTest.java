package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.example.DocumentManager.*;

import static org.junit.jupiter.api.Assertions.*;

class DocumentManagerTest {
    private DocumentManager documentManager;
    private static final Author AUTHOR1 = Author.builder().id("author1").name("John Doe").build();
    private static final Author AUTHOR2 = Author.builder().id("author2").name("Jane Smith").build();

    @BeforeEach
    void setUp() {
        documentManager = new DocumentManager();
    }

    @Test
    void save_GeneratesIdAndCreatedTime() {
        Document doc = Document.builder()
                .title("Test Title")
                .content("Test Content")
                .author(AUTHOR1)
                .build();

        Document savedDoc = documentManager.save(doc);

        assertNotNull(savedDoc.getId());
        assertNotNull(savedDoc.getCreated());
    }

    @Test
    void save_PreservesExistingIdAndCreatedTime() {
        String existingId = "existing-id";
        Instant existingTime = Instant.now().minusSeconds(3600);
        Document doc = Document.builder()
                .id(existingId)
                .created(existingTime)
                .title("Test Title")
                .content("Test Content")
                .author(AUTHOR1)
                .build();

        Document savedDoc = documentManager.save(doc);

        assertEquals(existingId, savedDoc.getId());
        assertEquals(existingTime, savedDoc.getCreated());
    }

    @Test
    void findById_ReturnsDocumentWhenExists() {
        Document savedDoc = documentManager.save(Document.builder()
                .title("Test")
                .author(AUTHOR1)
                .build());

        Optional<Document> foundDoc = documentManager.findById(savedDoc.getId());

        assertTrue(foundDoc.isPresent());
        assertEquals(savedDoc, foundDoc.get());
    }

    @Test
    void findById_ReturnsEmptyWhenNotExists() {
        Optional<Document> foundDoc = documentManager.findById("non-existent-id");

        assertFalse(foundDoc.isPresent());
    }

    @Test
    void search_ByTitlePrefix() {
        documentManager.save(Document.builder().title("ABC Doc").author(AUTHOR1).build());
        documentManager.save(Document.builder().title("XYZ Doc").author(AUTHOR1).build());

        SearchRequest request = SearchRequest.builder()
                .titlePrefixes(Arrays.asList("ABC"))
                .build();

        List<Document> results = documentManager.search(request);

        assertEquals(1, results.size());
        assertEquals("ABC Doc", results.get(0).getTitle());
    }

    @Test
    void search_ByContent() {
        documentManager.save(Document.builder()
                .title("Doc 1")
                .content("This is some content")
                .author(AUTHOR1)
                .build());
        documentManager.save(Document.builder()
                .title("Doc 2")
                .content("This is different content")
                .author(AUTHOR1)
                .build());

        SearchRequest request = SearchRequest.builder()
                .containsContents(Arrays.asList("different"))
                .build();

        List<Document> results = documentManager.search(request);

        assertEquals(1, results.size());
        assertEquals("Doc 2", results.get(0).getTitle());
    }

    @Test
    void search_ByAuthor() {
        documentManager.save(Document.builder().title("Doc 1").author(AUTHOR1).build());
        documentManager.save(Document.builder().title("Doc 2").author(AUTHOR2).build());

        SearchRequest request = SearchRequest.builder()
                .authorIds(Arrays.asList(AUTHOR1.getId()))
                .build();

        List<Document> results = documentManager.search(request);

        assertEquals(1, results.size());
        assertEquals(AUTHOR1, results.get(0).getAuthor());
    }

/*    @Test
    void search_ByTimeRange() {
        SearchRequest searchRequest = SearchRequest.builder()
                .createdFrom(Instant.parse("2023-01-15T00:00:00Z"))
                .createdTo(Instant.parse("2023-02-15T23:59:59Z"))
                .build();

        List<DocumentManager.Document> result = documentManager.search(searchRequest);

        assertEquals(1, result.size());
        assertEquals("Document 2", result.get(0).getTitle());

    }*/

    @Test
    void search_CombinedCriteria() {
        documentManager.save(Document.builder()
                .title("ABC Old")
                .content("test content")
                .author(AUTHOR1)
                .created(Instant.now().minusSeconds(7200))
                .build());
        Document matchingDoc = documentManager.save(Document.builder()
                .title("ABC New")
                .content("different test")
                .author(AUTHOR1)
                .created(Instant.now())
                .build());
        documentManager.save(Document.builder()
                .title("XYZ New")
                .content("test content")
                .author(AUTHOR2)
                .created(Instant.now())
                .build());

        SearchRequest request = SearchRequest.builder()
                .titlePrefixes(Arrays.asList("ABC"))
                .containsContents(Arrays.asList("different"))
                .authorIds(Arrays.asList(AUTHOR1.getId()))
                .createdFrom(Instant.now().minusSeconds(3600))
                .build();

        List<Document> results = documentManager.search(request);

        assertEquals(1, results.size());
        assertEquals(matchingDoc.getId(), results.get(0).getId());
    }

    @Test
    void search_WithNullRequest_ReturnsAllDocuments() {
        Document doc1 = documentManager.save(Document.builder().title("Doc 1").author(AUTHOR1).build());
        Document doc2 = documentManager.save(Document.builder().title("Doc 2").author(AUTHOR2).build());

        List<Document> results = documentManager.search(null);

        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(doc -> doc.getId().equals(doc1.getId())));
        assertTrue(results.stream().anyMatch(doc -> doc.getId().equals(doc2.getId())));
    }
}