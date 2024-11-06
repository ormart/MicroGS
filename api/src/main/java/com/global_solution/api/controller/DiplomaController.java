package com.global_solution.api.controller;

import com.global_solution.api.model.Diploma;
import com.global_solution.api.service.DiplomaService;
import com.global_solution.api.service.RabbitMQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/diploma")
public class DiplomaController {

    @Autowired
    private DiplomaService diplomaService;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @PostMapping
    public ResponseEntity<String> createDiploma(@RequestBody Diploma diploma) {
        Diploma savedDiploma = diplomaService.saveDiploma(diploma);
        rabbitMQSender.send(savedDiploma);
        return ResponseEntity.status(HttpStatus.CREATED).body("Dados recebidos e processados com sucesso.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Diploma> getDiploma(@PathVariable Long id) {
        Diploma diploma = diplomaService.getDiploma(id);
        if (diploma != null) {
            return ResponseEntity.ok(diploma);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
