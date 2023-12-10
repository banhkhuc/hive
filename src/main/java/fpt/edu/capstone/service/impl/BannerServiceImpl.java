package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.banner.ConfigBannerRequest;
import fpt.edu.capstone.dto.banner.UpdateBannerRequest;
import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.entity.Banner;
import fpt.edu.capstone.entity.Payment;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.repository.BannerRepository;
import fpt.edu.capstone.service.BannerService;
import fpt.edu.capstone.service.PaymentService;
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

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BannerServiceImpl implements BannerService {

    private final ModelMapper modelMapper;

    private final BannerRepository bannerRepository;

    private final RentalPackageService rentalPackageService;

    @Override
    public List<Banner> getAllBanner() {
        return bannerRepository.findAll();
    }

    @Override
    public void deleteBanner(long bannerId) {
        Optional<Banner> banner = bannerRepository.findById(bannerId);
        if(!banner.isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.DETAIL_PACKAGE_DOES_NOT_EXIST);
        }
        banner.get().setDeleted(true);
        bannerRepository.save(banner.get());
    }

    @Override
    public Banner findById(long bannerId) {
        if(!bannerRepository.findById(bannerId).isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.DETAIL_PACKAGE_DOES_NOT_EXIST);
        }
        return bannerRepository.findById(bannerId).get();
    }

    @Override
    public Banner insertBanner(ConfigBannerRequest request) {
        if(request.getPrice() <= 0) {
            throw new HiveConnectException(ResponseMessageConstants.PRICE_EQUAL_GREATER_THAN_ZERO);
        }
        if(request.getDiscount() < 0) {
            throw new HiveConnectException(ResponseMessageConstants.PRICE_EQUAL_GREATER_THAN_ZERO);
        }
        if(request.getDiscount() > request.getPrice()) {
            throw new HiveConnectException(ResponseMessageConstants.DISCOUNT_PRICE_INVALID);
        }
        if(request.isSpotlight() == false
                && request.isHomepageBannerA() == false && request.isHomePageBannerB() == false && request.isHomePageBannerC() == false
                && request.isJobBannerA() == false && request.isJobBannerB() == false && request.isJobBannerC() == false) {
            throw new HiveConnectException(ResponseMessageConstants.BANNER_POSITION_INVALID);
        }
        if(!rentalPackageService.findById(request.getRentalPackageId()).isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.RENTAL_PACKAGE_DOES_NOT_EXIST);
        }
        List<Banner> bannersByTitle = bannerRepository.getBannersByTitle(request.getTitle());
        if(bannersByTitle != null && !bannersByTitle.isEmpty()) {
            throw new HiveConnectException(ResponseMessageConstants.BANNER_TITLE_HAVE_ALREADY_EXISTED);
        }
        Banner banner = modelMapper.map(request, Banner.class);
        banner.create();
        banner.setDeleted(false);
        bannerRepository.save(banner);
        if(!bannerRepository.findById(banner.getId()).isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.CREATE_BANNER_FAIL);
        }
        return banner;
    }

    @Override
    public void updateBanner(UpdateBannerRequest request) {
        Optional<Banner> detailBannerPackage = bannerRepository.findById(request.getBannerId());
        if(!detailBannerPackage.isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.DETAIL_PACKAGE_DOES_NOT_EXIST);
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
        if(request.isSpotlight() == false
                && request.isHomepageBannerA() == false && request.isHomePageBannerB() == false && request.isHomePageBannerC() == false
                && request.isJobBannerA() == false && request.isJobBannerB() == false && request.isJobBannerC() == false) {
            throw new HiveConnectException(ResponseMessageConstants.BANNER_POSITION_INVALID);
        }
        if(!rentalPackageService.findById(request.getRentalPackageId()).isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.RENTAL_PACKAGE_DOES_NOT_EXIST);
        }
        List<Banner> bannersByTitle = bannerRepository.checkExistBannerByTitle(request.getTitle(), request.getBannerId());
        if(bannersByTitle != null && !bannersByTitle.isEmpty()) {
            throw new HiveConnectException(ResponseMessageConstants.BANNER_TITLE_HAVE_ALREADY_EXISTED);
        }
        detailBannerPackage.get().setRentalPackageId(request.getRentalPackageId());
        detailBannerPackage.get().setPrice(request.getPrice());
        detailBannerPackage.get().setDiscount(request.getDiscount());
        detailBannerPackage.get().setTimeExpired(request.getTimeExpired());
        detailBannerPackage.get().setTitle(request.getTitle());
        detailBannerPackage.get().setDescription(request.getDescription());
        detailBannerPackage.get().setImage(request.getImage());
        detailBannerPackage.get().setSpotlight(request.isSpotlight());
        detailBannerPackage.get().setHomepageBannerA(request.isHomepageBannerA());
        detailBannerPackage.get().setHomepageBannerB(request.isHomePageBannerB());
        detailBannerPackage.get().setHomepageBannerC(request.isHomePageBannerC());
        detailBannerPackage.get().setJobBannerA(request.isJobBannerA());
        detailBannerPackage.get().setJobBannerB(request.isJobBannerB());
        detailBannerPackage.get().setJobBannerC(request.isJobBannerC());
        bannerRepository.save(detailBannerPackage.get());
    }

    @Override
    public ResponseDataPagination getBannerByFilter(Integer pageNo, Integer pageSize, String title, boolean isDeleted, String timeExpired, String section) {
        int pageReq = pageNo >= 1 ? pageNo - 1 : pageNo;
        Pageable pageable = PageRequest.of(pageReq, pageSize);
        Page<Banner> bannerPage = bannerRepository.getBannerByFilter(pageable, title,timeExpired, section, 3,  isDeleted);
        List <Banner> bannerList = bannerPage.getContent();

        ResponseDataPagination responseDataPagination = new ResponseDataPagination();
        Pagination pagination = new Pagination();
        responseDataPagination.setData(bannerList);
        pagination.setCurrentPage(pageNo);
        pagination.setPageSize(pageSize);
        pagination.setTotalPage(bannerPage.getTotalPages());
        pagination.setTotalRecords(Integer.parseInt(String.valueOf(bannerPage.getTotalElements())));
        responseDataPagination.setStatus(Enums.ResponseStatus.SUCCESS.getStatus());
        responseDataPagination.setPagination(pagination);
        return responseDataPagination;
    }

    @Override
    public Page<Banner> getListFilter(Pageable pageable, String name, String timeExpired, String benefit, long rentalId, boolean isDeleted) {
        return bannerRepository.getBannerByFilter(pageable, name, timeExpired, benefit, rentalId, isDeleted);
    }

    @Override
    public Banner findByRentalPackageIdAndId(long rentalPackageId, long id) {
        return bannerRepository.findByRentalPackageIdAndId(rentalPackageId, id);
    }
}
