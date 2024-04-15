package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.dto.detail_package.CreatePackageRequest;
import fpt.edu.capstone.dto.detail_package.DetailPackageResponse;
import fpt.edu.capstone.dto.rental_package.RentalPackageResponse;
import fpt.edu.capstone.entity.Banner;
import fpt.edu.capstone.entity.DetailPackage;
import fpt.edu.capstone.entity.RentalPackage;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.repository.DetailPackageRepository;
import fpt.edu.capstone.service.BannerService;
import fpt.edu.capstone.service.DetailPackageService;
import fpt.edu.capstone.service.RentalPackageService;
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
import java.util.Optional;

@Service
@AllArgsConstructor
public class DetailPackageServiceImpl implements DetailPackageService {

    private final ModelMapper modelMapper;

    private final DetailPackageRepository detailPackageRepository;

    private final RentalPackageService rentalPackageService;

    private final BannerService bannerService;

    @Override
    public ResponseDataPagination getListDetailPackageFilter(Integer pageNo, Integer pageSize, String name,
                                                             String timeExpired, String benefit, long rentalId, boolean isDeleted) {
        int pageReq = pageNo >= 1 ? pageNo - 1 : pageNo;
        Pageable pageable = PageRequest.of(pageReq, pageSize);

        ResponseDataPagination responseDataPagination = new ResponseDataPagination();
        Pagination pagination = new Pagination();

        if(rentalId == 0){
            RentalPackageResponse packageResponse = new RentalPackageResponse();
            Page<DetailPackage> packagePage = detailPackageRepository.getListFilter(pageable, name,timeExpired, benefit, rentalId, isDeleted);
            Page <Banner> bannerPage = bannerService.getListFilter(pageable, name,timeExpired, benefit, rentalId, isDeleted);

            packageResponse.setDetailPackage(packagePage.getContent());
            packageResponse.setBanner(bannerPage.getContent());

            long totalPage = packagePage.getTotalPages() + bannerPage.getTotalPages()-1;
            int totalRecords = Integer.parseInt(String.valueOf(packagePage.getTotalElements())) +
                    Integer.parseInt(String.valueOf(bannerPage.getTotalElements()));

            responseDataPagination.setData(packageResponse);
            pagination.setCurrentPage(pageNo);
            pagination.setPageSize(pageSize);
            pagination.setTotalPage(totalPage);
            pagination.setTotalRecords(totalRecords);
            responseDataPagination.setStatus(Enums.ResponseStatus.SUCCESS.getStatus());
            responseDataPagination.setPagination(pagination);

        }

        if(rentalId == 1 || rentalId == 2 ){
            Page<DetailPackage> packagePage = detailPackageRepository.getListFilter(pageable, name,timeExpired,benefit, rentalId, isDeleted);
            responseDataPagination.setData(packagePage.toList());
            pagination.setCurrentPage(pageNo);
            pagination.setPageSize(pageSize);
            pagination.setTotalPage(packagePage.getTotalPages());
            pagination.setTotalRecords(Integer.parseInt(String.valueOf(packagePage.getTotalElements())));
            responseDataPagination.setStatus(Enums.ResponseStatus.SUCCESS.getStatus());
            responseDataPagination.setPagination(pagination);
        }
        if(rentalId == 3){
            Page <Banner> bannerPage = bannerService.getListFilter(pageable, name,timeExpired, benefit, rentalId, isDeleted);
            responseDataPagination.setData(bannerPage.toList());
            pagination.setCurrentPage(pageNo);
            pagination.setPageSize(pageSize);
            pagination.setTotalPage(bannerPage.getTotalPages());
            pagination.setTotalRecords(Integer.parseInt(String.valueOf(bannerPage.getTotalElements())));
            responseDataPagination.setStatus(Enums.ResponseStatus.SUCCESS.getStatus());
            responseDataPagination.setPagination(pagination);
        }
        return responseDataPagination;
    }

    @Override
    public DetailPackage findById(long id) {
        if (!detailPackageRepository.findById(id).isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.DETAIL_PACKAGE_DOES_NOT_EXIST);
        }
        return detailPackageRepository.findById(id).get();
    }

    @Override
    public void updateDetailPackage(DetailPackage detailPackage) {
        if(detailPackage.getDetailName() == null || detailPackage.getDetailName().trim().isEmpty()) {
            throw new HiveConnectException(ResponseMessageConstants.REQUIRE_INPUT_MANDATORY_FIELD);
        }
        Optional<DetailPackage> existedPackage = detailPackageRepository
                .checkExistByDetailName(detailPackage.getDetailName().trim(), detailPackage.getId());
        if(existedPackage.isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.PACKAGE_NAME_EXISTS);
        }
        if(detailPackage.getPrice() <= 0) {
            throw new HiveConnectException(ResponseMessageConstants.PRICE_EQUAL_GREATER_THAN_ZERO);
        }
        if(detailPackage.getDiscount() < 0) {
            throw new HiveConnectException(ResponseMessageConstants.PRICE_EQUAL_GREATER_THAN_ZERO);
        }
        if(detailPackage.getDiscount() > detailPackage.getPrice()) {
            throw new HiveConnectException(ResponseMessageConstants.DISCOUNT_PRICE_INVALID);
        }
        if(detailPackage.getRentalPackageId() == 1) {
            if(detailPackage.getMaxCvView() <= 0) {
                throw new HiveConnectException(ResponseMessageConstants.MAX_CV_VIEW_INVALID);
            }
        } else if(detailPackage.getRentalPackageId() == 2) {
            if(!detailPackage.isRelatedJob() && !detailPackage.isSuggestJob()) {
                throw new HiveConnectException(ResponseMessageConstants.PAYMENT_PACKAGE_BENEFIT_INVALID);
            }
        }
        findById(detailPackage.getId());
        detailPackageRepository.saveAndFlush(detailPackage);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void createNormalPackage(CreatePackageRequest request) {
        Optional<RentalPackage> rentalPackage = rentalPackageService.findById(request.getRentalPackageId());
        if(!rentalPackage.isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.RENTAL_PACKAGE_DOES_NOT_EXIST);
        }
        if(request.getDetailName() == null || request.getDetailName().trim().isEmpty()) {
            throw new HiveConnectException(ResponseMessageConstants.REQUIRE_INPUT_MANDATORY_FIELD);
        }
        Optional<DetailPackage> existedPackage = detailPackageRepository.findByDetailName(request.getDetailName().trim());
        if(existedPackage.isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.PACKAGE_NAME_EXISTS);
        }
        if(request.getPrice() <= 0) {
            throw new HiveConnectException(ResponseMessageConstants.PRICE_EQUAL_GREATER_THAN_ZERO);
        }
        if(request.getDiscount() < 0) {
            throw new HiveConnectException(ResponseMessageConstants.PRICE_EQUAL_GREATER_THAN_ZERO);
        }
        if(request.getDiscount() > request.getPrice()) {
            throw new HiveConnectException(ResponseMessageConstants.DISCOUNT_PRICE_INVALID);
        }
        if(request.getRentalPackageId() == 1) {
            if(request.getMaxCvView() <= 0) {
                throw new HiveConnectException(ResponseMessageConstants.PAYMENT_PACKAGE_BENEFIT_INVALID);
            }
        } else if(request.getRentalPackageId() == 2) {
            if(!request.isRelatedJob() && !request.isSuggestJob() && !request.isNewJob() &&
                    !request.isPopularJob() && !request.isUrgentJob()) {
                throw new HiveConnectException(ResponseMessageConstants.PAYMENT_PACKAGE_BENEFIT_INVALID);
            }
        }
        DetailPackage openCvPackage = modelMapper.map(request, DetailPackage.class);
        openCvPackage.create();
        detailPackageRepository.save(openCvPackage);
        if(!detailPackageRepository.findById(openCvPackage.getId()).isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.CREATE_FAIL);
        }
    }

    @Override
    public void deleteDetailPackage(long detailPackageId) {
        Optional<DetailPackage> detailPackage = detailPackageRepository.findById(detailPackageId);
        if(!detailPackage.isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.DETAIL_PACKAGE_DOES_NOT_EXIST);
        }
        detailPackage.get().setDeleted(true);
        detailPackageRepository.save(detailPackage.get());
    }

    @Override
    public DetailPackageResponse getDetailPackageInfor(long groupPackageId, long packageId) {
        DetailPackageResponse response = new DetailPackageResponse();
        DetailPackage normalPackage = detailPackageRepository.findByRentalPackageIdAndId(groupPackageId, packageId);
        Banner bannerPackage = bannerService.findByRentalPackageIdAndId(groupPackageId, packageId);
        response.setNormalPackage(normalPackage);
        response.setBannerPackage(bannerPackage);
        return response;
    }
}
