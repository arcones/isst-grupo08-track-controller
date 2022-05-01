package es.upm.isst.grupo08.trackback.controller;

import es.upm.isst.grupo08.trackback.model.Parcel;
import es.upm.isst.grupo08.trackback.repository.ParcelRepository;
import es.upm.isst.grupo08.trackback.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static es.upm.isst.grupo08.trackback.model.Role.CARRIER;
import static es.upm.isst.grupo08.trackback.model.Role.RECIPIENT;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class TrackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParcelRepository parcelRepository;

    @BeforeEach
    public void initEach() {
        Stream.of("correos", "mrw", "seur").forEach(carrier ->
        {
            try {
                mockMvc.perform(delete("/parcels/" + carrier)).andExpect(status().isNoContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    void shouldRespondToHealthCheck() throws Exception {
        mockMvc.perform(get("/health")).andExpect(status().isOk());
    }

    @Test
    void shouldExistInitialCarriersAndRecipient() {
        assertEquals(4, userRepository.findAll().size());
        assertEquals(1, userRepository.findAll().stream().filter(user -> user.getRole() == RECIPIENT).count());
        assertEquals(3, userRepository.findAll().stream().filter(user -> user.getRole() == CARRIER).count());
    }

    @ParameterizedTest
    @ValueSource(strings = {"seur", "mrw", "correos", "pepa"})
    void shouldAuthenticateWhenCredentialsAreCorrect(String carrierName) throws Exception {
        mockMvc.perform(get("/login")
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

    @ParameterizedTest
    @ValueSource(strings = {"seur", "mrw", "correos"})
    void shouldLoadCSVFileWithParcels(String carrierName) throws Exception {
        Resource parcelsFile = new ClassPathResource("OKLoads/" + carrierName + "Load.csv");

        MockMultipartFile multipartFile = new MockMultipartFile(
                "parcels", parcelsFile.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                parcelsFile.getInputStream());

        mockMvc.perform(multipart("/parcels/" + carrierName)
                        .file(multipartFile))
                .andExpect(status().isOk());

        List<Parcel> parcelList = parcelRepository.findAll();
        long carrierId = userRepository.findAll().stream()
                .filter(carrier -> Objects.equals(carrier.getName(), carrierName))
                .findAny().get().getId();

        assertEquals(10, parcelList.stream()
                .filter(parcel -> parcel.getCarrierId() == carrierId)
                .count());
    }

    @Test
    void shouldPreventLoadForNonRegisteredCarrier() throws Exception {
        Resource parcelsFile = new ClassPathResource("OKLoads/correosLoad.csv");

        MockMultipartFile multipartFile = new MockMultipartFile(
                "parcels", parcelsFile.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                parcelsFile.getInputStream());

        mockMvc.perform(multipart("/parcels/nonExistentCarrier")
                        .file(multipartFile))
                .andExpect(status().isPreconditionFailed());
    }

    @Test
    void shouldPreventLoadWithDuplicatesInTheFile() throws Exception {
        Resource parcelsFile = new ClassPathResource("KOLoads/KO_duplicates_in_same_load.csv");

        MockMultipartFile multipartFile = new MockMultipartFile(
                "parcels", parcelsFile.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                parcelsFile.getInputStream());

        mockMvc.perform(multipart("/parcels/seur")
                        .file(multipartFile))
                .andExpect(status().isConflict());
    }


    @Test
    void shouldPreventLoadWithParcelsAlreadyInDatabase() throws Exception {
        Resource parcelsFile = new ClassPathResource("OKLoads/correosLoad.csv");

        MockMultipartFile multipartFile = new MockMultipartFile(
                "parcels", parcelsFile.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                parcelsFile.getInputStream());

        mockMvc.perform(multipart("/parcels/correos")
                        .file(multipartFile))
                .andExpect(status().isOk());

        mockMvc.perform(multipart("/parcels/correos")
                        .file(multipartFile))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldPreventLoadWithWrongStatus() throws Exception {
        Resource parcelsFile = new ClassPathResource("KOLoads/KO_status_not_existing.csv");

        MockMultipartFile multipartFile = new MockMultipartFile(
                "parcels", parcelsFile.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                parcelsFile.getInputStream());

        mockMvc.perform(multipart("/parcels/correos")
                        .file(multipartFile))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void shouldProvideInfoAboutParcel() throws Exception {
        Resource parcelsFile = new ClassPathResource("OKLoads/correosLoad.csv");

        MockMultipartFile multipartFile = new MockMultipartFile(
                "parcels", parcelsFile.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                parcelsFile.getInputStream());

        String carrierName = "correos";
        int carrierId = (int) userRepository.findAll().stream()
                .filter(carrier -> Objects.equals(carrier.getName(), carrierName))
                .findAny().get().getId();

        mockMvc.perform(multipart("/parcels/"+carrierName)
                        .file(multipartFile))
                .andExpect(status().isOk());

        mockMvc.perform(get("/parcels/cc46218817846sm"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.trackingNumber", is("cc46218817846sm")),
                        jsonPath("$.carrierId", is(carrierId)),
                        jsonPath("$.status", is("Entregado")),
                        jsonPath("$.recipient", is("paca"))
                );
    }

    @Test
    void shouldNotProvideInfoAboutNonExistentParcel() throws Exception {
        mockMvc.perform(get("/parcels/"+ randomAlphanumeric(25)))
                .andExpect(status().isNotFound());
    }
}