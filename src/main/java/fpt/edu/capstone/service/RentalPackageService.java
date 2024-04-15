package fpt.edu.capstone.service;

import fpt.edu.capstone.entity.RentalPackage;

import java.util.List;
import java.util.Optional;

public interface RentalPackageService {
    List<RentalPackage> findAll();

    boolean existById(long id);

    boolean existByName(String groupName);

    void saveRentalPackage(RentalPackage rentalPackage);

    Optional<RentalPackage> findById(long id);

    String getRentalPackageName(long detailPackageId);
}
