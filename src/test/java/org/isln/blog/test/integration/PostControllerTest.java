package org.isln.blog.test.integration;

import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import tools.jackson.databind.json.JsonMapper;

import org.isln.blog.configuration.ApplicationConfiguration;
import org.isln.blog.configuration.DataSourceConfiguration;
import org.isln.blog.configuration.MultipartConfiguration;
import org.isln.blog.configuration.WebConfiguration;
import org.isln.blog.controller.PostController;
import org.isln.blog.model.Comment;
import org.isln.blog.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableWebMvc
@SpringJUnitConfig(
        classes = {
                ApplicationConfiguration.class,
                DataSourceConfiguration.class,
                MultipartConfiguration.class,
                WebConfiguration.class
        }
)
@WebAppConfiguration
@TestPropertySource(locations = "classpath:test-application.properties")
public class PostControllerTest {
    @Autowired
    private PostController postController;
    @Autowired
    private WebApplicationContext webContext;
    @Autowired
    private JsonMapper mapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Value("${app.max-text-length-in-paging-post-response}")
    private int maxTextLengthInPagedPostResponse;

    private MockMvc mockMvc;

    @BeforeEach
    public void prepare() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webContext).build();
        jdbcTemplate.update("DELETE FROM posts");
        jdbcTemplate.update("DELETE FROM comments");
    }

    @Test
    public void createPostTest() throws Exception {
        String title = "Title";
        String text = "Text";
        String hashtag1 = "#TAG_1";
        String hashtag2 = "#TAG_2";
        AtomicLong idHolder = new AtomicLong();

        assertPostIsCreated(title, text, hashtag1, hashtag2, idHolder);
        assertPostFoundById(idHolder.get(), title, text);
    }

    @Test
    public void longTextPostTest() throws Exception {
        String title = "Title";
        String textBeginning = "Text contains enough characters to be truncated in pages response ";
        String text = textBeginning + "F".repeat(maxTextLengthInPagedPostResponse);
        String hashtag1 = "#TAG_1";
        String hashtag2 = "#TAG_2";
        createPostWithLongText(title, text, hashtag1, hashtag2);

        assertTextIsTruncated(text.substring(0, maxTextLengthInPagedPostResponse) + "...");
    }

    @Test
    public void pagingRequestTest() throws Exception {
        int postCount = 10;
        for (int i = 0; i < postCount; i++) {
            createPost("title " + i, "text " + i, "#tag" + i, "#tag" + (i + 1));
        }
        assertFirstPageIsCorrect();
        assertSecondPageIsCorrect();
        assertLastPageIsCorrect();
        assertPostFoundByTitle();
        assertPostFoundByTag();
        assertPostFoundByTagAndTitle();
    }

    @Test
    public void likeCountTest() throws Exception {
        String title = "Title";
        String text = "Text";
        String hashtag1 = "#TAG_1";
        String hashtag2 = "#TAG_2";
        long id = createPost(title, text, hashtag1, hashtag2);

        assertLikesCountedCorrectly(id);
    }

    @Test
    public void addCommentTest() throws Exception {
        long id = createPost("", "");

        String commentText = "comment text";
        AtomicLong commentIdHolder = new AtomicLong();
        long commentId = assertCommentAdded(id, commentText, commentIdHolder);
        assertCommentFoundById(id, commentId, commentText);
        assertCommentAmountIncremented(id);
    }

    @Test
    public void addPictureTest() throws Exception {
        long id = createPost("", "");
        byte[] png = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00};

        assertPictureUploaded(id, png);
        assertImageReceived(id, png);
        assertImageDeletedOnPostDeletion(id);
    }

    private void assertCommentAmountIncremented(long id) throws Exception {
        mockMvc.perform(
                        post("/api/posts/" + id + "/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(new Comment().setPostId(id).setText("")))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
        mockMvc.perform(get("/api/posts/" + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentCount").value(2));
    }

    private void assertCommentFoundById(long id, long commentId, String commentText) throws Exception {
        mockMvc.perform(get("/api/posts/" + id + "/comments/" + commentId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.text").value(commentText));
    }

    private long assertCommentAdded(long id, String commentText, AtomicLong commentIdHolder) throws Exception {
        mockMvc.perform(
                        post("/api/posts/" + id + "/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(new Comment().setPostId(id).setText(commentText)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.text").value(commentText))
                .andDo(r -> extractCommentId(r, commentIdHolder));
        long commentId = commentIdHolder.get();
        mockMvc.perform(get("/api/posts/" + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentCount").value(1));
        return commentId;
    }

    private void extractCommentId(MvcResult r, AtomicLong commentId) throws UnsupportedEncodingException {
        commentId.set(mapper.readValue(r.getResponse().getContentAsString(), Comment.class).getId());
    }

    private void assertPostIsCreated(String title, String text, String hashtag1, String hashtag2, AtomicLong id) throws Exception {
        mockMvc.perform(
                        post("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        mapper.writeValueAsString(
                                                new Post().setTitle(title).setText(text).setTags(Set.of(hashtag1, hashtag2)))
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.text").value(text))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.commentCount").value(0))
                .andExpect(jsonPath("$.likeCount").value(0))
                .andDo(r -> id.set(mapper.readValue(r.getResponse().getContentAsString(), Post.class).getId()))
                .andReturn();
    }

    private void assertPostFoundById(Long id, String title, String text) throws Exception {
        mockMvc.perform(get("/api/posts/" + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.text").value(text))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.commentCount").value(0))
                .andExpect(jsonPath("$.likeCount").value(0));
    }

    private void assertLikesCountedCorrectly(Long id) throws Exception {
        mockMvc.perform(post("/api/posts/" + id + "/likes").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
        mockMvc.perform(post("/api/posts/" + id + "/likes").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
        mockMvc.perform(get("/api/posts/" + id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likeCount").value(2));
    }

    private long createPost(String title, String text, String... tags) throws Exception {
        AtomicLong id = new AtomicLong();
        mockMvc.perform(
                        post("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        mapper.writeValueAsString(
                                                new Post().setTitle(title).setText(text).setTags(Set.of(tags)))
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.text").value(text))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.commentCount").value(0))
                .andExpect(jsonPath("$.likeCount").value(0))
                .andDo(r -> id.set(mapper.readValue(r.getResponse().getContentAsString(), Post.class).getId()))
                .andReturn();
        return id.get();
    }

    private void assertLastPageIsCorrect() throws Exception {
        mockMvc.perform(
                        get("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .queryParam("pageNumber", "5")
                                .queryParam("pageSize", "2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts").isArray())
                .andExpect(jsonPath("$.posts[0].title").value("title 8"))
                .andExpect(jsonPath("$.hasPrev").value("true"))
                .andExpect(jsonPath("$.hasNext").value("false"))
                .andExpect(jsonPath("$.lastPage").value("5"));
    }

    private void assertSecondPageIsCorrect() throws Exception {
        mockMvc.perform(
                        get("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .queryParam("pageNumber", "2")
                                .queryParam("pageSize", "2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts").isArray())
                .andExpect(jsonPath("$.posts[0].title").value("title 2"))
                .andExpect(jsonPath("$.hasPrev").value("true"))
                .andExpect(jsonPath("$.hasNext").value("true"))
                .andExpect(jsonPath("$.lastPage").value("5"));
    }

    private void assertFirstPageIsCorrect() throws Exception {
        mockMvc.perform(
                        get("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .queryParam("pageNumber", "1")
                                .queryParam("pageSize", "2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts").isArray())
                .andExpect(jsonPath("$.posts[0].title").value("title 0"))
                .andExpect(jsonPath("$.hasPrev").value("false"))
                .andExpect(jsonPath("$.hasNext").value("true"))
                .andExpect(jsonPath("$.lastPage").value("5"));
    }

    private void assertPostFoundByTagAndTitle() throws Exception {
        mockMvc.perform(
                        get("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .queryParam("pageNumber", "1")
                                .queryParam("pageSize", "1")
                                .queryParam("search", "  #tag2  title   1  ")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts").isArray())
                .andExpect(jsonPath("$.posts[0].title").value("title 1"))
                .andExpect(jsonPath("$.hasPrev").value("false"))
                .andExpect(jsonPath("$.hasNext").value("false"))
                .andExpect(jsonPath("$.lastPage").value("1"));
    }

    private void assertPostFoundByTag() throws Exception {
        mockMvc.perform(
                        get("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .queryParam("pageNumber", "1")
                                .queryParam("pageSize", "1")
                                .queryParam("search", "#tag2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts").isArray())
                .andExpect(jsonPath("$.posts[0].title").value("title 1"))
                .andExpect(jsonPath("$.hasPrev").value("false"))
                .andExpect(jsonPath("$.hasNext").value("true"))
                .andExpect(jsonPath("$.lastPage").value("2"));
        mockMvc.perform(
                        get("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .queryParam("pageNumber", "2")
                                .queryParam("pageSize", "1")
                                .queryParam("search", "#tag2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts").isArray())
                .andExpect(jsonPath("$.posts[0].title").value("title 2"))
                .andExpect(jsonPath("$.hasPrev").value("true"))
                .andExpect(jsonPath("$.hasNext").value("false"))
                .andExpect(jsonPath("$.lastPage").value("2"));
    }

    private void assertPostFoundByTitle() throws Exception {
        mockMvc.perform(
                        get("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .queryParam("pageNumber", "1")
                                .queryParam("pageSize", "2")
                                .queryParam("search", "title 1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts").isArray())
                .andExpect(jsonPath("$.posts[0].title").value("title 1"))
                .andExpect(jsonPath("$.posts[1]").doesNotExist())
                .andExpect(jsonPath("$.hasPrev").value("false"))
                .andExpect(jsonPath("$.hasNext").value("false"))
                .andExpect(jsonPath("$.lastPage").value("1"));
    }

    private void assertImageDeletedOnPostDeletion(long id) throws Exception {
        mockMvc.perform(delete("/api/posts/" + id))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/posts/" + id + "/image"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(new byte[]{}));
    }

    private void assertImageReceived(long id, byte[] png) throws Exception {
        mockMvc.perform(get("/api/posts/" + id + "/image"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(png));
    }

    private void assertPictureUploaded(long id, byte[] png) throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "image.png",
                MediaType.APPLICATION_OCTET_STREAM_VALUE,
                png);
        mockMvc.perform(multipart(HttpMethod.PUT, "/api/posts/" + id + "/image").file(imageFile))
                .andExpect(status().isOk())
                .andExpect(content().bytes(png));
    }

    private void createPostWithLongText(String title, String text, String hashtag1, String hashtag2) throws Exception {
        mockMvc.perform(
                        post("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        mapper.writeValueAsString(
                                                new Post().setTitle(title).setText(text).setTags(Set.of(hashtag1, hashtag2)))
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.text").value(text))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.commentCount").value(0))
                .andExpect(jsonPath("$.likeCount").value(0))
                .andReturn();
    }

    private void assertTextIsTruncated(String expectedText) throws Exception {
        mockMvc.perform(
                        get("/api/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .queryParam("pageNumber", "1")
                                .queryParam("pageSize", "2")
                )
                .andExpect(jsonPath("$.posts[0].text").value(expectedText));
    }
}

