package fpt.edu.capstone.service;

import fpt.edu.capstone.dto.banner.ConfigBannerRequest;
import fpt.edu.capstone.dto.banner.UpdateBannerRequest;
import fpt.edu.capstone.entity.Banner;
import fpt.edu.capstone.utils.ResponseDataPagination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BannerService {
    List<Banner> getAllBanner();

    void deleteBanner(long bannerId);

    Banner findById(long bannerId);

    Banner insertBanner(ConfigBannerRequest request);

    void updateBanner(UpdateBannerRequest request);

    ResponseDataPagination getBannerByFilter(Integer pageNo, Integer pageSize, String title, boolean isDeleted, String timeExpired, String section);

    Page<Banner> getListFilter(Pageable pageable, String name, String timeExpired, String benefit, long rentalId, boolean isDeleted);

    Banner findByRentalPackageIdAndId(long rentalPackageId, long id);

}
