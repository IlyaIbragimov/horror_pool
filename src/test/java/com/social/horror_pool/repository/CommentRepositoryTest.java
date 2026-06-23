package com.social.horror_pool.repository;

import com.social.horror_pool.model.Comment;
import com.social.horror_pool.model.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void save_WithTwoThousandCharacterContent_PersistsComment() {
        Movie movie = new Movie();
        movie.setTitle("Long Comment Test Movie");
        this.entityManager.persistAndFlush(movie);

        String content = "a".repeat(2000);

        Comment comment = new Comment();
        comment.setMovie(movie);
        comment.setCommentContent(content);

        Comment savedComment = this.commentRepository.saveAndFlush(comment);

        this.entityManager.clear();

        Comment persistedComment = this.commentRepository.findById(savedComment.getCommentId()).orElseThrow();
        assertEquals(content, persistedComment.getCommentContent());
    }
}