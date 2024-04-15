package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.dto.company.*;
import fpt.edu.capstone.entity.Company;
import fpt.edu.capstone.entity.Fields;
import fpt.edu.capstone.entity.Image;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.repository.CompanyRepository;
import fpt.edu.capstone.repository.ImageRepository;
import fpt.edu.capstone.service.*;
import fpt.edu.capstone.utils.Enums;
import fpt.edu.capstone.utils.Pagination;
import fpt.edu.capstone.utils.ResponseDataPagination;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyManageServiceImpl implements CompanyManageService {

    private final ModelMapper modelMapper;

    private final AppliedJobService appliedJobService;

    private final ImageServiceImpl imageService;

    private final RecruiterService recruiterService;

    private final CompanyService companyService;

    private final CompanyRepository companyRepository;

    private final FieldsService fieldsService;

    private final ImageRepository imageRepository;

    @Override
    public List<TopCompanyResponse> getTopCompaniesHomepage() {
        List<TopCompanyResponse> responseList = new ArrayList<>();
        List<CompanyResponse> companyList = appliedJobService.getTopCompaniesHomepage();
        for (CompanyResponse company : companyList) {
            TopCompanyResponse response = new TopCompanyResponse();
            Optional<Image> image = imageService.findAvatarByCompanyId(company.getCompanyId());
            if (image.isPresent()) {
                response.setCompanyAvatar(image.get().getUrl());
            }
            response.setApplyCvNumber(company.getApplyCvNumber());
            response.setCompanyId(company.getCompanyId());
            response.setCompanyName(company.getCompanyName());
            responseList.add(response);
        }
        if (companyList != null && companyList.size() < ResponseMessageConstants.TOP_COMPANY_HOMEPAGE_SIZE) {
            int additionSize = ResponseMessageConstants.TOP_COMPANY_HOMEPAGE_SIZE - companyList.size();
            List<Long> existsId = companyList.stream().map(CompanyResponse::getCompanyId).collect(Collectors.toList());
            List<Company> additionCompanies = companyService.getAdditionCompanies(additionSize, existsId);
            for (Company company : additionCompanies) {
                TopCompanyResponse response = new TopCompanyResponse();
                Optional<Image> image = imageService.findAvatarByCompanyId(company.getId());
                if (image.isPresent()) {
                    response.setCompanyAvatar(image.get().getUrl());
                }
                response.setCompanyId(company.getId());
                response.setCompanyName(company.getName());
                responseList.add(response);
            }
        }
        return responseList;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public UpdateCompanyInforRequest updateCompanyInformation(long recruiterId, UpdateCompanyInforRequest request) {
        if (!recruiterService.findById(recruiterId).isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.USER_DOES_NOT_EXIST);
        }
        Company company = companyService.getCompanyById(request.getCompanyId());
        if (company == null) {
            throw new HiveConnectException(ResponseMessageConstants.COMPANY_DOES_NOT_EXIST);
        }
        if (company.getCreatorId() != recruiterId) {
            throw new HiveConnectException(ResponseMessageConstants.YOU_DONT_HAVE_PERMISSION);
        }
        if ((request.getCompanyEmail() == null && request.getCompanyEmail().trim().isEmpty())
                || (request.getCompanyPhone() == null && request.getCompanyPhone().trim().isEmpty())
                || (request.getCompanyDescription() == null && request.getCompanyDescription().trim().isEmpty())
                || (request.getCompanyWebsite() == null && request.getCompanyWebsite().trim().isEmpty())
                || (request.getNumberEmployees() == null && request.getNumberEmployees().trim().isEmpty())
                || (request.getCompanyAddress() == null && request.getCompanyAddress().trim().isEmpty())
                || (request.getTaxCode() == null && request.getTaxCode().trim().isEmpty())
                || (request.getMapUrl() == null && request.getMapUrl().trim().isEmpty())) {
            throw new HiveConnectException(ResponseMessageConstants.REQUIRE_INPUT_MANDATORY_FIELD);
        }
        company.setEmail(request.getCompanyEmail());
        company.setPhone(request.getCompanyPhone());
        company.setDescription(request.getCompanyDescription());
        company.setWebsite(request.getCompanyWebsite());
        company.setNumberEmployees(request.getNumberEmployees());
        company.setAddress(request.getCompanyAddress());
        company.setTaxCode(request.getTaxCode());
        company.setMapUrl(request.getMapUrl());

        List<String> uploadImageUrlList = new ArrayList<>();

        //update avatar
        if (request.getAvatarUrl() != null && !request.getAvatarUrl().trim().isEmpty()) {
//            uploadImageUrlList.add(request.getAvatarUrl());
            imageService.saveImageCompany(true, false, company.getId(), Arrays.asList(request.getAvatarUrl()));
        }
        //update cover image
        if (request.getCoverImageUrl() != null && !request.getCoverImageUrl().trim().isEmpty()) {
//            uploadImageUrlList.add(request.getCoverImageUrl());
            imageService.saveImageCompany(false, true, company.getId(), Arrays.asList(request.getCoverImageUrl()));
        }
        //xóa cái nào thì trả id của cái đó
        List<Long> deleteImageIdList = request.getDeleteImageIdList();
        if (deleteImageIdList != null && !deleteImageIdList.isEmpty()) {
            imageService.deleteImageById(deleteImageIdList);
        }

        //upload introduction image list of company
        uploadImageUrlList = request.getUploadImageUrlList();
        if (uploadImageUrlList != null && !uploadImageUrlList.isEmpty()) {
            imageService.saveImageCompany(false, false, company.getId(), uploadImageUrlList);
        }
        return null;
    }

    @Override
    public ResponseDataPagination searchCompany(Integer pageNo, Integer pageSize, String companyName) {
        int pageReq = pageNo >= 1 ? pageNo - 1 : pageNo;
        Pageable pageable = PageRequest.of(pageReq, pageSize);
        Page<Company> companyPageable = companyService.searchCompany(pageable, companyName);
        List<ListCompany> list = companyPageable.getContent().stream().map(company -> modelMapper.
                map(company, ListCompany.class)).collect(Collectors.toList());
        for (ListCompany c: list) {
            Optional<Image> image = imageService.getImageCompany(c.getId(), true);
            if (image.isPresent()) {
                c.setAvatar(image.get().getUrl());
            }
        }
        ResponseDataPagination responseDataPagination = new ResponseDataPagination();
        Pagination pagination = new Pagination();
        responseDataPagination.setData(list);
        pagination.setCurrentPage(pageNo);
        pagination.setPageSize(pageSize);
        pagination.setTotalPage(companyPageable.getTotalPages());
        pagination.setTotalRecords(Integer.parseInt(String.valueOf(companyPageable.getTotalElements())));
        responseDataPagination.setStatus(Enums.ResponseStatus.SUCCESS.getStatus());
        responseDataPagination.setPagination(pagination);
        return responseDataPagination;
    }

    @Override
    public void lockCompany(long companyId) {
        Optional<Company> company = companyService.findById(companyId);
        if (!company.isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.COMPANY_DOES_NOT_EXIST);
        }
        if (company.get().isLocked()) {
            company.get().setLocked(false);
        } else if (!company.get().isLocked()) {
            company.get().setLocked(true);
        }
        company.get().update();
        companyRepository.save(company.get());
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Company createCompany(CreateCompanyRequest request) {
        if (companyRepository.getCompanyByName(request.getName()).isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.COMPANY_NAME_EXISTS);
        }
        Optional<Fields> field = fieldsService.findByName(request.getFieldWork());
        if (!field.isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.FIELD_WORK_OF_COMPANY_DOES_NOT_EXIST);
        }
        if ((request.getName() == null || request.getName().trim().isEmpty())
                || (request.getEmail() == null || request.getEmail().trim().isEmpty())
                || (request.getPhoneNumber() == null || request.getPhoneNumber().trim().isEmpty())
                || (request.getTaxCode() == null || request.getTaxCode().trim().isEmpty())
                || (request.getAddress() == null || request.getAddress().trim().isEmpty())
                || (request.getNumberEmployees() == null || request.getNumberEmployees().trim().isEmpty())) {
            throw new HiveConnectException(ResponseMessageConstants.REQUIRE_INPUT_MANDATORY_FIELD);
        }
        if (companyRepository.getCompanyByTaxcode(request.getTaxCode().trim()).isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.TAX_CODE_EXISTS);
        }
        Company company = modelMapper.map(request, Company.class);
        company.setPhone(request.getPhoneNumber());
        company.create();
        companyRepository.save(company);

        Image image = new Image();
        image.setName(request.getName());
        image.setUrl(request.getUrl());
        image.setCompanyId(company.getId());
        image.setAvatar(true);
        imageRepository.save(image);
        Company savedCompany = companyRepository.getCompanyById(company.getId());
        return savedCompany;
    }
}
