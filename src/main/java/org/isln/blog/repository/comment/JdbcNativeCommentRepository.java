package org.isln.blog.repository.comment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.isln.blog.exceptions.ObjectNotFound;
import org.isln.blog.exceptions.RepositoryException;
import org.isln.blog.model.Comment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcNativeCommentRepository implements CommentRepository {
    public static final String COMMENTS_TABLE = "comments";
    public static final String ID = "id";
    public static final String TEXT = "text";
    public static final String POST_ID = "post_id";
    public static final List<String> ALL_COLUMNS = List.of(ID, TEXT, POST_ID);

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Long create(Comment comment) {
        if (comment.getPostId() == null) {
            throw new IllegalArgumentException("Post id must be provided to create a comment");
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> prepareCreationStatement(comment, connection), keyHolder);
        return keyHolder.getKeyAs(Long.class);
    }

    @Override
    public void update(Comment comment) {
        if (comment.getId() == null) {
            throw new RepositoryException("Id is not provided for update operation");
        }
        jdbcTemplate.update("UPDATE %s SET %s = ? WHERE %s = ?".formatted(COMMENTS_TABLE, TEXT, ID), comment.getText(), comment.getId());
    }

    @Override
    public Comment findById(Long id) {
        return jdbcTemplate.query(
                        "SELECT %s FROM %s WHERE %s = ?"
                                .formatted(String.join(", ", ALL_COLUMNS), COMMENTS_TABLE, ID),
                        this::map,
                        id
                )
                .stream()
                .findAny()
                .orElseThrow(() -> new ObjectNotFound("Post with id '" + id + " not found"));

    }

    @Override
    public List<Comment> findByPostId(Long postId) {
        return jdbcTemplate.query(
                "SELECT %s FROM %s WHERE %s = ?"
                        .formatted(String.join(", ", ALL_COLUMNS),
                                COMMENTS_TABLE,
                                POST_ID
                        ),
                this::map,
                postId
        );
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM %s where %s = ?".formatted(COMMENTS_TABLE, ID), id);
    }

    @Override
    public void delete(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        jdbcTemplate.update(
                "DELETE FROM %s WHERE %s IN (%s)"
                        .formatted(COMMENTS_TABLE, ID, String.join(", ", Collections.nCopies(ids.size(), "?"))),
                ids.toArray()
        );
    }

    private PreparedStatement prepareCreationStatement(Comment comment, Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO %s(%s) values(?, ?)"
                        .formatted(COMMENTS_TABLE, String.join(", ", List.of(TEXT, POST_ID))), new String[]{ID});
        ps.setString(1, comment.getText());
        ps.setLong(2, comment.getPostId());
        return ps;
    }

    private Comment map(ResultSet resultSet, int rowNumber) throws SQLException {
        return new Comment()
                .setId(resultSet.getLong(ID))
                .setText(resultSet.getString(TEXT))
                .setPostId(resultSet.getLong(POST_ID));
    }
}
