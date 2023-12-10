package fpt.edu.capstone.repository;

import fpt.edu.capstone.entity.VietnamCountry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<VietnamCountry, Long> {
}
