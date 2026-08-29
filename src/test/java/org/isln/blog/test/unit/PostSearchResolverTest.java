package org.isln.blog.test.unit;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import org.isln.blog.service.PostQueryResolver;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class PostSearchResolverTest {
    @ParameterizedTest
    @CsvSource({
            "'query', 'query'",
            "'#tag query', 'query'",
            "'query #tag', query",
            "'   #tag   query   ', 'query'",
            "'#tag1 #tag2 query foo bar', 'query foo bar'",
            "'#tag1 query #tag2 foo #tag3 bar', 'query foo bar'",
    })
    public void extractSearchQueryTest(String input, String expectedQuery) {
        String query = PostQueryResolver.getSearchQuery(input);

        assertThat(query).isEqualTo(expectedQuery);
    }

    @ParameterizedTest
    @ValueSource(strings = {"#tag", "#tag1 #tag2", ""})
    public void extractEmptyQueryTest(String input) {
        String query = PostQueryResolver.getSearchQuery(input);

        assertThat(query).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("provideTags")
    public void extractTagsTest(String input, Set<String> expectedTags) {
        Set<String> tags = PostQueryResolver.getTags(input);

        assertThat(tags).hasSameElementsAs(expectedTags);
    }

    public static Stream<Arguments> provideTags() {
        return Stream.of(
                Arguments.of("#tag", Set.of("#tag")),
                Arguments.of("#tag1 #tag2 ", Set.of("#tag1", "#tag2")),
                Arguments.of("#tag1     foo    #tag2    ", Set.of("#tag1", "#tag2")),
                Arguments.of("foo bar", Collections.emptySet()),
                Arguments.of(null, Collections.emptySet())
        );
    }
}
