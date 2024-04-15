package fpt.edu.capstone.controller;

import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.dto.company.*;
import fpt.edu.capstone.entity.Company;
import fpt.edu.capstone.entity.Recruiter;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.service.CompanyManageService;
import fpt.edu.capstone.service.CompanyService;
import fpt.edu.capstone.service.RecruiterManageService;
import fpt.edu.capstone.service.RecruiterService;
import fpt.edu.capstone.utils.Enums;
import fpt.edu.capstone.utils.LogUtils;
import fpt.edu.capstone.utils.ResponseData;
import fpt.edu.capstone.utils.ResponseDataPagination;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/company")
public class CompanyController {

    private static final Logger logger = LoggerFactory.getLogger(CompanyController.class);

    private final CompanyService companyService;

    private final CompanyManageService companyManageService;

    private final RecruiterService recruiterService;

    private final RecruiterManageService recruiterManageService;

    @GetMapping("/get-list-company")
    public ResponseData getListCompany(){
        try {
            List<ListCompany> careers = companyService.getAllCompany();
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, careers);
        } catch (Exception e){
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.ERROR);
        }
    }

    @PostMapping("/create-company")
    public ResponseData createCompany(@RequestBody CreateCompanyRequest request) {
        try {
            Optional<Recruiter> recruiter = recruiterService.findById(request.getCreatorId());
            if (!recruiter.isPresent()) {
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.USER_DOES_NOT_EXIST, request.getCreatorId());
            }
            if(recruiter.get().getCompanyId() > 0) {
                throw new HiveConnectException(ResponseMessageConstants.RECRUITER_HAS_ALREADY_HAD_COMPANY);
            }
            Company company = companyManageService.createCompany(request);
            recruiterService.updateCompany(company.getId(), recruiter.get().getId());
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, company);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @GetMapping("/company-information") //get information of company by id
    public ResponseData getCompanyInformation(@RequestParam long companyId,
                                              @RequestParam(defaultValue = "0", required = false) long recruiterId) {
        try {
            CompanyInformationResponse response = recruiterManageService.getCompanyInformation(companyId, recruiterId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, response);
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.NOTFOUND.getStatus(), ex.getMessage());
        }
    }

    @GetMapping("/get-top-12-recruitment-companies")
    public ResponseData getTop12Companies() {
        try {
            List<TopCompanyResponse> topCompanies = companyManageService.getTopCompaniesHomepage();
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, topCompanies);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.ERROR);
        }
    }

    @GetMapping("/search-company")
    @Operation(summary = "Dùng chung - Get list all company")
    public ResponseData searchCompany(@RequestParam(defaultValue = "0") Integer pageNo,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam(value = "companyName", required = false) String companyName) {
        try {
            ResponseDataPagination pagination = companyManageService.searchCompany(pageNo, pageSize, companyName);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS,
                    pagination);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.ERROR);
        }
    }

    @PutMapping("/lock-unlock-company")
    @Operation(summary = "Admin module - Lock, unlock a company")
    public ResponseData lockUnlockCompany(@RequestParam long companyId) {
        try {
            companyManageService.lockCompany(companyId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.ERROR);
        }
    }


    @PutMapping("/update-company-information/{recruiterId}")
    public ResponseData updateCompanyInformation(@PathVariable("recruiterId") long recruiterId,
                                                 @RequestBody UpdateCompanyInforRequest request) {
        try {
            companyManageService.updateCompanyInformation(recruiterId, request);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }
}
