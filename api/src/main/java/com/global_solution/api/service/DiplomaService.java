package com.global_solution.api.service;

import com.global_solution.api.model.Diploma;
import com.global_solution.api.repository.DiplomaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class DiplomaService {

    @Autowired
    private DiplomaRepository diplomaRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public Diploma createDiploma(Diploma diploma) {
        Diploma savedDiploma = diplomaRepository.save(diploma);
        cacheDiploma(savedDiploma);
        return savedDiploma;
    }

    @Cacheable(value = "diplomas", key = "#id")
    public Diploma getDiploma(Long id) {
        String cacheKey = "diploma:" + id;
        Diploma diploma = (Diploma) redisTemplate.opsForValue().get(cacheKey);

        if (diploma == null) {
            diploma = diplomaRepository.findById(id).orElse(null);
            if (diploma != null) {
                cacheDiploma(diploma);
            }
        }

        return diploma;
    }

    @CacheEvict(value = "diplomas", key = "#diploma.id")
    public Diploma updateDiploma(Diploma diploma) {
        Diploma updatedDiploma = diplomaRepository.save(diploma);
        cacheDiploma(updatedDiploma);
        return updatedDiploma;
    }

    private void cacheDiploma(Diploma diploma) {
        String cacheKey = "diploma:" + diploma.getId();
        redisTemplate.opsForValue().set(cacheKey, diploma, 1, TimeUnit.HOURS);
    }
}
