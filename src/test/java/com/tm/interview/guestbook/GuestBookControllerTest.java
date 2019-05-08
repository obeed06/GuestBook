package com.tm.interview.guestbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = GuestBookController.class,
        includeFilters = @ComponentScan.Filter(type = ASSIGNABLE_TYPE, classes = {FileStoreService.class}))
class GuestBookControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mvc;


    @MockBean
    private GuestBookEntryRepository entryRepository;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void forIndexwhenValidInput_thenReturns200() throws Exception {
        GuestBookEntry entry = GuestBookEntry.builder()
                .name("name")
                .message("message").build();
        when(entryRepository.findAll()).thenReturn(asList(entry));

        mvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    void whenUserWithRoleUserTriesToDelete_thenReturns403() throws Exception {
        mvc.perform(get("/guestbook/delete/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    void whenUserWithRoleAdminTriesToDelete_thenReturns302() throws Exception {
        GuestBookEntry entry = GuestBookEntry.builder()
                .name("name")
                .message("message").build();

        when(entryRepository.findById(anyLong())).thenReturn(Optional.of(entry));
        mvc.perform(get("/guestbook/delete/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    void whenUserWithRoleAdminTriesToSave_thenReturns302() throws Exception {
        GuestBookEntry entry = GuestBookEntry.builder()
                .name("name")
                .message("message").build();

        mvc.perform(post("/guestbook/save")
                .content(objectMapper.writeValueAsString(entry))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }

    @WithMockUser(username = "user", roles = "USER")
    @Test
    void whenUserWithRoleUserTriesToSave_thenReturns302() throws Exception {
        GuestBookEntry entry = GuestBookEntry.builder()
                .name("name")
                .message("message").build();

        mvc.perform(post("/guestbook/save")
                .content(objectMapper.writeValueAsString(entry))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }
}