package es.upm.isst.grupo08.trackback.controller;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.apache.commons.lang3.RandomStringUtils.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class TrackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRespondToHealthCheck() throws Exception {
        mockMvc.perform(get("/health")).andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"seur", "mrw", "correos"})
    void shouldAuthenticateWhenCredentialsAreCorrect(String carrierName) throws Exception {
        mockMvc.perform(get("/carriers")
                        .header("User", carrierName)
                        .header("Password", "test"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotAuthenticateWhenCredentialsAreWrong() throws Exception {
        mockMvc.perform(get("/carriers")
                        .header("User", randomAlphabetic(5))
                        .header("Password", randomAlphanumeric(8))
                ).andExpect(status().isNotFound());
    }


}