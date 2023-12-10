package fpt.edu.capstone.service;

import fpt.edu.capstone.entity.VietnamCountry;

import java.util.List;
import java.util.Optional;

public interface CountryService {
    List<VietnamCountry> getListCountry();

    Optional<VietnamCountry> findById(long id);
}
