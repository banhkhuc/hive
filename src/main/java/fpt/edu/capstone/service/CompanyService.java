package fpt.edu.capstone.service;

import fpt.edu.capstone.dto.company.ListCompany;
import fpt.edu.capstone.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CompanyService {

    Company getCompanyById(long id);

    boolean existById(long id);

    List <ListCompany> getAllCompany();

    Optional<Company> findById(long companyId);

    Page<Company> searchCompany(Pageable pageable, String companyName);

    List<Company> getAdditionCompanies(int additionSize, List<Long> existsId);
}
