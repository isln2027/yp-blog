package org.isln.blog.repository.post;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import lombok.RequiredArgsConstructor;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import org.isln.blog.exceptions.ObjectNotFound;
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

    private static final String POSTS_TABLE = "posts";
    private static final String POST_COUNT_ALIAS = "post_count";
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String TEXT = "text";
    private static final String TAGS = "tags";
    private static final String LIKE_COUNT = "like_count";
    private static final List<String> ALL_COLUMNS = List.of(ID, TITLE, TEXT, TAGS, LIKE_COUNT);

    @Override
    public List<Post> find(Integer pageNumber, Integer pageSize, PostRequestParameters parameters) {
        long offset = (long) pageNumber * pageSize;
        QueryParameters queryParameters = queryWithSearch(parameters);
        String query = queryParameters.query() + " ORDER BY %s LIMIT ? OFFSET ?".formatted(ID);
        List<Object> arguments = new ArrayList<>(queryParameters.arguments());
        arguments.add(pageSize);
        arguments.add(offset);
        Object[] argumentArray = arguments.toArray(new Object[0]);
        return jdbcTemplate.query(query, this::map, argumentArray);
    }

    @Override
    public Post findById(Long id) {
        // todo process non-existent posts?
        return jdbcTemplate.query(queryById(ALL_COLUMNS), this::map, id)
                .stream()
                .findAny()
                .orElseThrow(() -> new ObjectNotFound("Post with id '" + id + " not found"));
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
                "UPDATE %s SET %s = ?, %s = ?, %s = ? FORMAT JSON WHERE %s = ?".formatted(POSTS_TABLE, TITLE, TEXT, TAGS, ID),
                post.getTitle(), post.getText(), jsonMapper.writeValueAsString(post.getTags()), post.getId()
        );
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update("DELETE FROM %s where id = ?".formatted(POSTS_TABLE), id);
    }

    @Override
    public void delete(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        jdbcTemplate.update(
                "DELETE FROM %s WHERE id IN (%s)"
                        .formatted(POSTS_TABLE, String.join(", ", Collections.nCopies(ids.size(), "?"))),
                ids.toArray()
        );
    }

    @Override
    public Long count(PostRequestParameters parameters) {
        String countQuery = "SELECT count(1) as %s FROM %s".formatted(POST_COUNT_ALIAS, POSTS_TABLE);
        QueryParameters queryParameters = addWhereExpressions(parameters, countQuery);
        return jdbcTemplate.queryForObject(
                queryParameters.query(),
                (rs, index) -> rs.getLong(POST_COUNT_ALIAS),
                queryParameters.arguments().toArray(new Object[0])
        );
    }

    @Override
    public Integer incrementLikeCount(Long id) {
        jdbcTemplate.update(
                "UPDATE %s SET %s = %s + 1 WHERE id = ?".formatted(POSTS_TABLE, LIKE_COUNT, LIKE_COUNT),
                id
        );
        return jdbcTemplate.queryForObject(
                queryById(List.of(LIKE_COUNT)),
                (rs, row) -> rs.getInt(LIKE_COUNT),
                id
        );
    }

    private Post map(ResultSet resultSet, int rowNumber) throws SQLException {
        return new Post()
                .setId(resultSet.getLong("id"))
                .setTitle(resultSet.getString("title"))
                .setText(resultSet.getString("text"))
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

    private static QueryParameters queryWithSearch(PostRequestParameters parameters) {
        String baseQuery = basicSelect();
        return addWhereExpressions(parameters, baseQuery);
    }

    private static QueryParameters addWhereExpressions(PostRequestParameters parameters, String baseQuery) {
        List<String> whereExpressions = new ArrayList<>();
        List<Object> arguments = new ArrayList<>();
        if (parameters.hasQuery()) {
            whereExpressions.add("title LIKE ?");
            arguments.add("%" + parameters.query() + "%");
        }
        if (parameters.hasTags()) {
            // todo в H2 нет нормальных методов для работы с json колонкой, исправить после перехода
            parameters.tags().forEach(tag -> {
                        whereExpressions.add("POSITION(? IN CAST(tags AS VARCHAR)) > 0");
                        arguments.add(tag);
                    }
            );
        }
        if (whereExpressions.isEmpty()) {
            return new QueryParameters(baseQuery, arguments);
        } else {
            String query = baseQuery + "WHERE " + String.join(" AND ", whereExpressions) + "\n";
            return new QueryParameters(query, arguments);
        }
    }

    private static String basicSelect() {
        return """
                SELECT %s
                FROM %s
                """.formatted(String.join(", ", JdbcNativePostRepository.ALL_COLUMNS), POSTS_TABLE);
    }

    private static String queryById(Collection<String> columns) {
        return """
                SELECT %s
                FROM %s
                WHERE %s = ?
                """.formatted(String.join(", ", columns), POSTS_TABLE, ID);
    }

    private record QueryParameters(String query, List<Object> arguments) {
    }
}
