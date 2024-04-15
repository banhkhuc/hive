package fpt.edu.capstone.controller;

import fpt.edu.capstone.dto.banner.ApproveBannerRequest;
import fpt.edu.capstone.dto.banner.ConfigBannerRequest;
import fpt.edu.capstone.dto.banner.UpdateBannerRequest;
import fpt.edu.capstone.dto.banner.UploadBannerRequest;
import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.dto.recruiter.DetailPurchasedPackageResponse;
import fpt.edu.capstone.entity.Banner;
import fpt.edu.capstone.entity.BannerActive;
import fpt.edu.capstone.entity.Payment;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.service.*;
import fpt.edu.capstone.utils.Enums;
import fpt.edu.capstone.utils.LogUtils;
import fpt.edu.capstone.utils.ResponseData;
import fpt.edu.capstone.utils.ResponseDataPagination;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/v1/banner")
@AllArgsConstructor
public class BannerController {

    private static final Logger logger = LoggerFactory.getLogger(BannerController.class);

    private final BannerService bannerService;

    private final BannerActiveService bannerActiveService;

    private final RecruiterManageService recruiterManageService;

    private final AdminManageService adminManageService;

    private final PaymentService paymentService;

    //config cho các gói banner
    @Operation(summary = "Admin module - Create new banner package on Manage Banner screen")
    @PostMapping("/config-banner")
    public ResponseData insertBanner(@RequestBody ConfigBannerRequest request) {
        try {
            Banner banner = bannerService.insertBanner(request);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, banner);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @Operation(summary = "Admin module - Get list all BANNER PAYMENT PACKAGE on Manage Banner screen")
    @GetMapping("/get-all-banner-package")
    public ResponseData getAllBanner(@RequestParam(value = "name", required = false) String title,
                                     @RequestParam(value = "status", required = false) boolean isDeleted,
                                     @RequestParam(value = "timeExpired", defaultValue = "") String timeExpired ,
                                     @RequestParam(value = "section", defaultValue = "") String section ,
                                     @RequestParam(defaultValue = "1") Integer pageNo,
                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            ResponseDataPagination pagination = bannerService.getBannerByFilter(pageNo, pageSize, title, isDeleted, timeExpired, section);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, pagination);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @PutMapping("/update-banner-package")
    @Operation(summary = "Admin module - Update banner package on Manage Banner screen")
    public ResponseData updateBanner(@RequestBody UpdateBannerRequest request) {
        try {
            List<Payment> paymentList = paymentService.getPaymentBannerInUse(request.getBannerId());
            if(paymentList != null && !paymentList.isEmpty()) {
                throw new HiveConnectException(ResponseMessageConstants.PACKAGE_IN_USE);
            }
            bannerService.updateBanner(request);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @DeleteMapping("/delete-banner-package")
    @Operation(summary = "Admin module - Delete a banner package of banner group")
    public ResponseData deleteBanner(@RequestParam long bannerPackageId) {
        try {
            bannerService.deleteBanner(bannerPackageId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @GetMapping("/get-banner-for-approval")
    @Operation(summary = "Admin module - Get banner upload by recruiter for approval")
    public ResponseData getBannerOfRecruiterForAdmin(@RequestParam(defaultValue = "0") Integer pageNo,
                                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                                     @RequestParam(value = "screenName", required = false) String screenName,
                                                     @RequestParam(value = "from") String from,
                                                     @RequestParam(value = "to") String to) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime start = LocalDate.parse(from, formatter).atStartOfDay();
            LocalDateTime end = LocalDate.parse(to, formatter).atStartOfDay();

            ResponseDataPagination pagination = adminManageService.getBannerOfRecruiterForAdmin(pageNo, pageSize, screenName,start, end);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, pagination);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @PutMapping("/approve-banner")
    @Operation(summary = "Amin module - approve banner of recruiter")
    public ResponseData approveBanner(@RequestBody ApproveBannerRequest request) {
        try {
            adminManageService.approveBanner(request);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @PutMapping("/upload-banner/{recruiterId}")
    @Operation(summary = "Recruiter module - recruiter upload banner image after bought a banner package")
    public ResponseData uploadBannerForPayment(@PathVariable long recruiterId, @RequestBody UploadBannerRequest request) {
        try {
            recruiterManageService.uploadBanner(recruiterId, request);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @GetMapping("/get-detail-purchased-package/{recruiterId}")
    @Operation(summary = "Recruiter module - get detail of recruiter's purchased package")
    public ResponseData getDetailPurchasedPackage(@PathVariable long recruiterId,
                                                  @RequestParam long paymentId) {
        try {
            DetailPurchasedPackageResponse response = recruiterManageService
                    .getDetailPurchasedPackage(recruiterId, paymentId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, response);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }


    @GetMapping("/get-banner-for-candidate")
    @Operation(summary = "Candidate module - Display banner for candidate")
    public ResponseData displayBannerForCandidate(@RequestParam String displayPosition) {
        try {
            List<BannerActive> bannerActives = bannerActiveService.getBannersByPosition(displayPosition);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, bannerActives);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @GetMapping("/detail-banner-package")
    @Operation(summary = "Dùng chung - Get detail of a banner payment package by id")
    public ResponseData getDetailBannerPackage(@RequestParam long bannerPackageId) {
        try {
            Banner banner = bannerService.findById(bannerPackageId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, banner);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

}
