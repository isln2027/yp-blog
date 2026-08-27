package org.isln.blog.repository.post;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import lombok.RequiredArgsConstructor;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import org.isln.blog.exceptions.RepositoryException;
import org.isln.blog.model.Post;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcNativePostRepository implements PostRepository {
    private final JdbcTemplate jdbcTemplate;
    private final JsonMapper jsonMapper;

    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String TEXT = "text";
    private static final String TAGS = "tags";
    private static final String COMMENT_COUNT = "comment_count";
    private static final String LIKE_COUNT = "like_count";
    private static final List<String> BASE_COLUMNS = List.of(ID, TITLE, TEXT, TAGS, COMMENT_COUNT, LIKE_COUNT);

    @Override
    public List<Post> find(PostRequestParameters parameters) {
        String query = pagingQuery();
        int limit = parameters.pageSize();
        int offset = parameters.pageNumber() * limit;
        // todo implement search
        return jdbcTemplate.query(query, this::map, limit, offset);
    }

    @Override
    public Post findById(long id) {
        return jdbcTemplate.queryForObject(queryById(), this::map, id);
    }

    @Override
    public Long create(Post post) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> prepareCreationStatement(post, connection), keyHolder);
        return keyHolder.getKeyAs(Long.class);
    }

    @Override
    public void update(Post post) {
        if (post.getId() == null) {
            throw new RepositoryException("Id is not provided for update operation");
        }
        jdbcTemplate.update(
                "UPDATE posts SET %s = ?, %s = ?, %s = ? FORMAT JSON WHERE %s = ?".formatted(TITLE, TEXT, TAGS, ID),
                post.getTitle(), post.getText(), jsonMapper.writeValueAsString(post.getTags()), post.getId()
        );
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update("DELETE FROM posts where id = ?", id);
    }

    private Post map(ResultSet resultSet, int rowNumber) throws SQLException {
        return new Post()
                .setId(resultSet.getLong("id"))
                .setTitle(resultSet.getString("title"))
                .setText(resultSet.getString("text"))
                .setCommentCount(resultSet.getInt("comment_count"))
                .setLikeCount(resultSet.getInt("like_count"))
                .setTags(
                        jsonMapper.readValue(
                                resultSet.getString("tags"),
                                new TypeReference<>() {
                                })
                );
    }

    private PreparedStatement prepareCreationStatement(Post post, Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO posts(%s) values(?, ?, ? FORMAT JSON)"
                        .formatted(String.join(", ", List.of(TITLE, TEXT, TAGS))),
                new String[]{ID}
        );
        ps.setString(1, post.getTitle());
        ps.setString(2, post.getText());
        ps.setString(3, jsonMapper.writeValueAsString(post.getTags()));
        return ps;
    }

    private static String pagingQuery() {
        return """
                SELECT %s
                FROM posts
                ORDER BY id
                LIMIT ? OFFSET ?""".formatted(String.join(", ", BASE_COLUMNS));
    }

    private static String queryById() {
        return """
                SELECT %s
                FROM posts
                WHERE id = ?
                """.formatted(String.join(", ", BASE_COLUMNS));
    }
}
