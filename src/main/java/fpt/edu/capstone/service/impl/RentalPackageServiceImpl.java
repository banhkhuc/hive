package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.RentalPackage;
import fpt.edu.capstone.repository.RentalPackageServiceRepository;
import fpt.edu.capstone.service.DetailPackageService;
import fpt.edu.capstone.service.RentalPackageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RentalPackageServiceImpl implements RentalPackageService {
    private final RentalPackageServiceRepository rentalPackageServiceRepository;
    @Override
    public List<RentalPackage> findAll() {
        return rentalPackageServiceRepository.findAll();
    }

    @Override
    public boolean existById(long id) {
        return rentalPackageServiceRepository.existsById(id);
    }

    @Override
    public boolean existByName(String groupName) {
        return rentalPackageServiceRepository.existsByPackageGroup(groupName);
    }

    @Override
    public void saveRentalPackage(RentalPackage rentalPackage) {
        rentalPackageServiceRepository.save(rentalPackage);
    }

    @Override
    public Optional<RentalPackage> findById(long id) {
        return rentalPackageServiceRepository.findById(id);
    }

    @Override
    public String getRentalPackageName(long detailPackageId) {
        return rentalPackageServiceRepository.getRentalPackageName(detailPackageId);
    }
}
