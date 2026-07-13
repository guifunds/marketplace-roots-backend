package com.origem.backend.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SignupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void rejectsSignupMissingRequiredFields() throws Exception {
        mockMvc.perform(post("/api/signups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.length()", is(6)));
    }

    @Test
    void createsSignupAndSimulatesPaymentInDemoMode() throws Exception {
        Map<String, String> request = Map.of(
                "profileType", "SUPPLIER",
                "name", "Fazenda Boa Vista Ltda.",
                "email", "contato@boavista.com",
                "phone", "+1 555 010-0001",
                "document", "TAX-BOAVISTA-1",
                "country", "Estados Unidos",
                "lang", "pt"
        );

        String response = mockMvc.perform(post("/api/signups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.currency", is("BRL")))
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(response).get("id").asText();
        String token = objectMapper.readTree(response).get("accessToken").asText();

        mockMvc.perform(get("/api/signups/" + id).header("X-Access-Token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("PENDING")));

        mockMvc.perform(post("/api/signups/" + id + "/pay").header("X-Access-Token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("PAID")));
    }

    @Test
    void rejectsAccessWithWrongToken() throws Exception {
        Map<String, String> request = Map.of(
                "profileType", "SUPPLIER",
                "name", "Fazenda Boa Vista Ltda.",
                "email", "outro@boavista.com",
                "phone", "+1 555 010-0002",
                "document", "TAX-OUTRO-1",
                "country", "Estados Unidos",
                "lang", "pt"
        );

        String response = mockMvc.perform(post("/api/signups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(get("/api/signups/" + id).header("X-Access-Token", UUID.randomUUID().toString()))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/signups/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsInvalidPhoneFormat() throws Exception {
        Map<String, String> request = Map.of(
                "profileType", "SUPPLIER",
                "name", "Fazenda Boa Vista Ltda.",
                "email", "contato@boavista.com",
                "phone", "abc",
                "document", "TAX-PHONE-TEST",
                "country", "Estados Unidos",
                "lang", "pt"
        );

        mockMvc.perform(post("/api/signups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("phone")));
    }

    @Test
    void rejectsInvalidCnpjForBrazilianSignup() throws Exception {
        Map<String, String> request = Map.of(
                "profileType", "SUPPLIER",
                "name", "Fazenda Boa Vista Ltda.",
                "email", "contato@boavista.com",
                "phone", "+55 11 90000-0001",
                "document", "11.111.111/1111-11",
                "country", "Brasil",
                "lang", "pt"
        );

        mockMvc.perform(post("/api/signups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("document")));
    }

    @Test
    void acceptsValidCnpjForBrazilianSignup() throws Exception {
        Map<String, String> request = Map.of(
                "profileType", "SUPPLIER",
                "name", "Fazenda Boa Vista Ltda.",
                "email", "contato2@boavista.com",
                "phone", "+55 11 90000-0002",
                "document", "11.222.333/0001-81",
                "country", "Brasil",
                "lang", "pt"
        );

        mockMvc.perform(post("/api/signups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void skipsCnpjCheckWhenCountryIsNotBrazil() throws Exception {
        Map<String, String> request = Map.of(
                "profileType", "BUYER",
                "name", "Global Foods Inc.",
                "email", "buyer@globalfoods.com",
                "phone", "+1 555 010-0003",
                "document", "EIN-98-7654321",
                "country", "United States",
                "lang", "en"
        );

        mockMvc.perform(post("/api/signups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void rejectsDuplicateEmailForSameProfileType() throws Exception {
        Map<String, String> request = Map.of(
                "profileType", "SUPPLIER",
                "name", "Fazenda Duplicada Ltda.",
                "email", "duplicado@boavista.com",
                "phone", "+1 555 010-0004",
                "document", "TAX-DUP-1",
                "country", "Estados Unidos",
                "lang", "pt"
        );

        mockMvc.perform(post("/api/signups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        Map<String, String> sameEmailUppercase = Map.of(
                "profileType", "SUPPLIER",
                "name", "Outra Fazenda Ltda.",
                "email", "DUPLICADO@boavista.com",
                "phone", "+1 555 010-0005",
                "document", "TAX-DUP-2",
                "country", "Estados Unidos",
                "lang", "pt"
        );

        mockMvc.perform(post("/api/signups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sameEmailUppercase)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("email")))
                .andExpect(jsonPath("$.message", is("Já existe um cadastro com esse e-mail para esse tipo de perfil")));
    }

    @Test
    void allowsSameEmailForDifferentProfileType() throws Exception {
        Map<String, String> asSupplier = Map.of(
                "profileType", "SUPPLIER",
                "name", "Fazenda Dupla Ltda.",
                "email", "dupla@boavista.com",
                "phone", "+1 555 010-0006",
                "document", "TAX-DUPLA-S",
                "country", "Estados Unidos",
                "lang", "pt"
        );
        Map<String, String> asBuyer = Map.of(
                "profileType", "BUYER",
                "name", "Fazenda Dupla Ltda.",
                "email", "dupla@boavista.com",
                "phone", "+1 555 010-0007",
                "document", "TAX-DUPLA-B",
                "country", "Estados Unidos",
                "lang", "pt"
        );

        mockMvc.perform(post("/api/signups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(asSupplier)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/signups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(asBuyer)))
                .andExpect(status().isCreated());
    }

    @Test
    void rejectsDuplicateCnpjForSameProfileType() throws Exception {
        Map<String, String> request = Map.of(
                "profileType", "SUPPLIER",
                "name", "Fazenda CNPJ Duplicado Ltda.",
                "email", "cnpj-duplicado-1@boavista.com",
                "phone", "+55 11 90000-0003",
                "document", "11.444.777/0001-61",
                "country", "Brasil",
                "lang", "pt"
        );
        Map<String, String> sameCnpjDifferentEmail = Map.of(
                "profileType", "SUPPLIER",
                "name", "Fazenda CNPJ Duplicado Filial Ltda.",
                "email", "cnpj-duplicado-2@boavista.com",
                "phone", "+55 11 90000-0004",
                "document", "11.444.777/0001-61",
                "country", "Brasil",
                "lang", "pt"
        );

        mockMvc.perform(post("/api/signups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/signups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sameCnpjDifferentEmail)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field", is("document")));
    }
}
