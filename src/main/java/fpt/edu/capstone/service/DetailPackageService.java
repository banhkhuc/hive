package fpt.edu.capstone.service;

import fpt.edu.capstone.dto.detail_package.CreatePackageRequest;
import fpt.edu.capstone.dto.detail_package.DetailPackageResponse;
import fpt.edu.capstone.entity.DetailPackage;
import fpt.edu.capstone.utils.ResponseDataPagination;

import java.util.Optional;

public interface DetailPackageService {
    ResponseDataPagination getListDetailPackageFilter(Integer pageNo, Integer pageSize, String name, String timeExpired,
                                                      String benefit, long rentalId, boolean isDeleted);

    DetailPackage findById(long id);

    void updateDetailPackage(DetailPackage detailPackage);

    void createNormalPackage(CreatePackageRequest request);

    void deleteDetailPackage(long detailPackageId);

    DetailPackageResponse getDetailPackageInfor(long groupPackageId, long packageId);
}
