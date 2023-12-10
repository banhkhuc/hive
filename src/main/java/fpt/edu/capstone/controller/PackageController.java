package fpt.edu.capstone.controller;

import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.dto.detail_package.CreatePackageRequest;
import fpt.edu.capstone.dto.detail_package.DetailPackageResponse;
import fpt.edu.capstone.dto.rental_package.RentalPackageRequest;
import fpt.edu.capstone.entity.DetailPackage;
import fpt.edu.capstone.entity.Payment;
import fpt.edu.capstone.entity.RentalPackage;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.service.DetailPackageService;
import fpt.edu.capstone.service.PaymentService;
import fpt.edu.capstone.service.RentalPackageService;
import fpt.edu.capstone.utils.Enums;
import fpt.edu.capstone.utils.LogUtils;
import fpt.edu.capstone.utils.ResponseData;
import fpt.edu.capstone.utils.ResponseDataPagination;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/package")
@AllArgsConstructor
public class PackageController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final DetailPackageService detailPackageService;

    private final RentalPackageService rentalPackageService;

    private final ModelMapper modelMapper;

    private final PaymentService paymentService;

    @GetMapping("/list-package")
    public ResponseData getListDetailPackage(@RequestParam(defaultValue = "0") Integer pageNo,
                                             @RequestParam(defaultValue = "10") Integer pageSize,
                                             @RequestParam(value = "name", defaultValue = "") String name ,
                                             @RequestParam(value = "timeExpired", defaultValue = "") String timeExpired ,
                                             @RequestParam(value = "benefit", defaultValue = "") String benefit ,
                                             @RequestParam(value = "rentalPackageId", defaultValue = "0", required = false) long rentalPackageId,
                                             @RequestParam(value = "status",required = false) boolean isDeleted){
        try {
            ResponseDataPagination pagination = detailPackageService.getListDetailPackageFilter(pageNo, pageSize, name,
                                                                        timeExpired, benefit, rentalPackageId, isDeleted);

            return new ResponseData(Enums.ResponseStatus.SUCCESS, ResponseMessageConstants.SUCCESS,pagination);
        }catch (Exception e){
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @GetMapping("/package")
    public ResponseData getDetailPackage(@RequestParam(value = "groupPackageId") long groupPackageId,
                                         @RequestParam(value = "packageId") long packageId){
        try {
            DetailPackageResponse dp =  detailPackageService.getDetailPackageInfor(groupPackageId, packageId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS, ResponseMessageConstants.SUCCESS,dp);
        }catch (Exception e){
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @GetMapping("/list-rental-package")
    @Operation (summary = "api này trả về cặp key-value để khi " +
            "admin tạo 1 gói quảng cáo thì sẽ  phân loại thuộc 1 trong nhóm group package này")
    public ResponseData getListGroupPackage(){
        try {
            List<RentalPackage> dp =  rentalPackageService.findAll();
            return new ResponseData(Enums.ResponseStatus.SUCCESS, ResponseMessageConstants.SUCCESS,dp);
        }catch (Exception e){
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @PostMapping("/create-new-package")
    @Operation(summary = "Admin module - tạo gói DỊCH VỤ MỞ HỒ SƠ ỨNG VIÊN VÀ GÓI DỊCH VỤ CHÍNH DÙNG CHUNG")
    public ResponseData creatNormalPackage(@RequestBody CreatePackageRequest request){
        try {
            detailPackageService.createNormalPackage(request);
            return new ResponseData(Enums.ResponseStatus.SUCCESS, ResponseMessageConstants.SUCCESS);
        }catch (Exception e){
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }


    @PutMapping("/update-new-package")
    @Operation(summary = "chỉnh sửa gói dịch vụ chính và gói dịch vụ mở hồ sơ ứng viên")
    public ResponseData updateSubPackage(@RequestBody DetailPackage request){
        try {
            List<Payment> paymentList = paymentService.getPaymentNormalPackageInUse(request.getId());
            if(paymentList != null && !paymentList.isEmpty()) {
                throw new HiveConnectException(ResponseMessageConstants.PACKAGE_IN_USE);
            }
            detailPackageService.updateDetailPackage(request);
            return new ResponseData(Enums.ResponseStatus.SUCCESS, ResponseMessageConstants.SUCCESS);
        }catch (Exception e){
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @PostMapping("/create-rental-package")
    @Operation(summary = "tạo nhóm dịch vụ")
    public ResponseData createGroupPackage(@RequestBody RentalPackageRequest request){
        try {
            if(rentalPackageService.existByName(request.getPackageGroup())){
                throw new HiveConnectException("Nhóm dịch vụ đã tồn tại. Vui lòng đặt tên khác");
            }
            RentalPackage p = modelMapper.map(request, RentalPackage.class);
            rentalPackageService.saveRentalPackage(p);
            return new ResponseData(Enums.ResponseStatus.SUCCESS, ResponseMessageConstants.SUCCESS);
        }catch (Exception e){
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @DeleteMapping("/delete-detail-package")
    @Operation(summary = "Admin module - Delete main package and open cv package")
    public ResponseData deleteDetailPackage(@RequestParam long detailPackageId) {
        try {
            detailPackageService.deleteDetailPackage(detailPackageId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.DELETE_SUCCESSFULLY);
        } catch (Exception e) {
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.ERROR);
        }

    }
}
