package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.Fields;
import fpt.edu.capstone.repository.FieldsRepository;
import fpt.edu.capstone.service.FieldsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FieldsServiceImpl implements FieldsService {

    private final FieldsRepository fieldsRepository;

    @Override
    public List<Fields> getAllField() {
        return fieldsRepository.getAllField();
    }

    @Override
    public boolean existById(long id) {
        return fieldsRepository.existsById(id);
    }

    @Override
    public Fields getById(long id) {
        return fieldsRepository.getById(id);
    }

    @Override
    public Optional<Fields> findById(long id) {
        return fieldsRepository.findById(id);
    }

    @Override
    public Optional<Fields> findByName(String name) {
        return fieldsRepository.findByFieldName(name);
    }
}