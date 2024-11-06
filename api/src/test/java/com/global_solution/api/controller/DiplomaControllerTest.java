package com.global_solution.api.controller;

import com.global_solution.api.model.Diploma;
import com.global_solution.api.service.DiplomaService;
import com.global_solution.api.service.RabbitMQSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DiplomaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiplomaService diplomaService;

    @MockBean
    private RabbitMQSender rabbitMQSender;

    @Test
    public void testCreateDiploma() throws Exception {
        Diploma diploma = new Diploma();
        diploma.setId(1L);
        diploma.setStudentName("John Doe");
        diploma.setCourseName("Computer Science");
        diploma.setCompletionDate(Date.valueOf(LocalDate.now()));
        doReturn(diploma).when(diplomaService).saveDiploma(any(Diploma.class));

        mockMvc.perform(post("/diploma")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"studentName\": \"John Doe\", \"courseName\": \"Computer Science\", \"completionDate\": \"2022-09-09\" }")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string("Dados recebidos e processados com sucesso."));
    }

    @Test
    public void testGetDiploma() throws Exception {
        Diploma diploma = new Diploma();
        diploma.setId(1L);
        diploma.setStudentName("John Doe");
        diploma.setCourseName("Computer Science");
        diploma.setCompletionDate(Date.valueOf(LocalDate.now()));
        doReturn(diploma).when(diplomaService).getDiploma(1L);

        mockMvc.perform(get("/diploma/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.studentName").value("John Doe"))
                .andExpect(jsonPath("$.courseName").value("Computer Science"));
    }
}