package br.com.techx.backendapis;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:tarefa04;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false"
})
class AlunoApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void crudUsaDtosEStatusCorretos() throws Exception {
        String criacao = "{\"nome\":\"Yuri Ribeiro Moraes\",\"idade\":25,\"ra\":\"027348\"}";
        MvcResult resultado = mockMvc.perform(post("/aluno")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(criacao))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Yuri Ribeiro Moraes"))
                .andExpect(jsonPath("$.ra").value("027348"))
                .andReturn();

        Number idGerado = JsonPath.read(resultado.getResponse().getContentAsString(), "$.id");
        String id = idGerado.toString();

        mockMvc.perform(get("/aluno/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ra").value("027348"));

        mockMvc.perform(get("/aluno").param("nome", "yuri"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ra").value("027348"));

        mockMvc.perform(put("/aluno/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Yuri Atualizado\",\"idade\":26,\"ra\":\"027348\"}"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        mockMvc.perform(get("/aluno/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Yuri Atualizado"));

        mockMvc.perform(post("/aluno")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(criacao))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/aluno")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\",\"idade\":-1,\"ra\":\"\"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(delete("/aluno/" + id))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        mockMvc.perform(get("/aluno/" + id)).andExpect(status().isNotFound());
        mockMvc.perform(put("/aluno/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(criacao))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete("/aluno/" + id)).andExpect(status().isNotFound());
    }
}
