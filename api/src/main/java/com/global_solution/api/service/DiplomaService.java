package com.global_solution.api.service;

import com.global_solution.api.model.Diploma;
import com.global_solution.api.repository.DiplomaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiplomaService {

    @Autowired
    private DiplomaRepository diplomaRepository;

    public Diploma saveDiploma(Diploma diploma) {
        return diplomaRepository.save(diploma);
    }

    public Diploma getDiploma(Long id) {
        return diplomaRepository.findById(id).orElse(null);
    }
}
