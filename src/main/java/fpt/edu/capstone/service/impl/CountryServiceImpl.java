package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.VietnamCountry;
import fpt.edu.capstone.repository.CountryRepository;
import fpt.edu.capstone.service.CountryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;
    @Override
    public List<VietnamCountry> getListCountry() {
        return countryRepository.findAll();
    }

    @Override
    public Optional<VietnamCountry> findById(long id) {
        return countryRepository.findById(id);
    }
}
