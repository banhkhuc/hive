package fpt.edu.capstone.service;

import fpt.edu.capstone.dto.company.CreateCompanyRequest;
import fpt.edu.capstone.dto.company.TopCompanyResponse;
import fpt.edu.capstone.dto.company.UpdateCompanyInforRequest;
import fpt.edu.capstone.entity.Company;
import fpt.edu.capstone.utils.ResponseDataPagination;

import java.util.List;

public interface CompanyManageService {
    List<TopCompanyResponse> getTopCompaniesHomepage();

    UpdateCompanyInforRequest updateCompanyInformation(long recruiterId, UpdateCompanyInforRequest request);

    ResponseDataPagination searchCompany(Integer pageNo, Integer pageSize, String companyName);

    void lockCompany(long companyId);

    Company createCompany(CreateCompanyRequest request);

}
