package ru.custom.intershop.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AdminControllerTest extends BaseControllerTest {
    private static final String TEST_FILE_NAME = "test-image.jpg";
    private static final String TEST_FILE_CONTENT = "test file content";
    private static final String TEST_FILE_CONTENT_TYPE = "image/jpeg";

    @Test
    void showDefault_shouldRedirectToAdd() throws Exception {
        mockMvc.perform(get("/admin"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/add"));
    }

    @Test
    void showAdd_shouldReturnAddItemTemplate() throws Exception {
        mockMvc.perform(get("/admin/add"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("text/html;charset=UTF-8"))
            .andExpect(view().name("add-item"))
            .andReturn();
    }

    @Test
    void handleAddItem_shouldUpdateItem() throws Exception {
        MockMultipartFile imageFile = createMultipart();

        mockMvc.perform(multipart("/admin/add")
                .file(imageFile)
                .param("title", "new title")
                .param("price", "100")
                .param("description", "some test description"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/add"));
    }

    private MockMultipartFile createMultipart() {
        return new MockMultipartFile(
            "image", TEST_FILE_NAME, TEST_FILE_CONTENT_TYPE, TEST_FILE_CONTENT.getBytes());
    }
}
