package fpt.edu.capstone.repository;

import fpt.edu.capstone.entity.RentalPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RentalPackageServiceRepository extends JpaRepository<RentalPackage, Long> {
    boolean existsByPackageGroup(String g);

    @Query(value = "select rp.package_group  from rental_package rp join detail_package dp on rp.id = dp.rental_package_id where dp.id  = ?1", nativeQuery = true)
    String getRentalPackageName(long detailPackageId);
}
