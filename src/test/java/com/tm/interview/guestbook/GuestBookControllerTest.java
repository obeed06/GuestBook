package com.tm.interview.guestbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = GuestBookController.class)
class GuestBookControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;
//
//    @InjectMocks
//    GuestBookController controller;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        MockitoAnnotations.initMocks(this);

    }

    @MockBean
    private FileStoreService fileStoreService;

    @MockBean
    private GuestBookEntryRepository entryRepository;

    @Test
    void forIndexwhenValidInput_thenReturns200() throws Exception {
        GuestBookEntry entry = GuestBookEntry.builder()
                .name("name")
                .message("message").build();
        when(entryRepository.findAll()).thenReturn(asList(entry));


        mvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @WithMockUser(value = "admin")
    @Test
    void whenValidInput_thenReturns200() throws Exception {
        GuestBookEntry entry = GuestBookEntry.builder()
                .name("name")
                .message("message").build();

        when(fileStoreService.saveFile(any())).thenReturn("/url/path");
        when(entryRepository.save(any())).thenReturn(entry);


        mvc.perform(post("/guestbook/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entry)))
                .andExpect(status().isOk());
    }
}