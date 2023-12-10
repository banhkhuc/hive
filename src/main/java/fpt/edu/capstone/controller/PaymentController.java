package fpt.edu.capstone.controller;

import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.dto.job.JobResponse;
import fpt.edu.capstone.dto.payment.*;
import fpt.edu.capstone.entity.Company;
import fpt.edu.capstone.entity.Image;
import fpt.edu.capstone.entity.Job;
import fpt.edu.capstone.entity.Payment;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.service.CompanyService;
import fpt.edu.capstone.service.ImageService;
import fpt.edu.capstone.service.JobService;
import fpt.edu.capstone.service.PaymentService;
import fpt.edu.capstone.utils.*;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/v1/payment")
@AllArgsConstructor
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    private final JobService jobService;

    private final CompanyService companyService;

    private final ImageService imageService;

    private final ModelMapper modelMapper;

    @PostMapping("/create-url-payment")
    public ResponseData createPayment(@RequestBody PaymentDTO paymentDTO) {
        try {
            PaymentResponseDTO dto = paymentService.getPaymentVNPay(paymentDTO);
            return new ResponseData(Enums.ResponseStatus.SUCCESS, ResponseMessageConstants.PAYMENT_SUCCESS, dto);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @PostMapping("/save-payment-success")
    public ResponseData savePaymentSuccess(@RequestParam("vnp_ResponseCode") String vnpResponseCode,
                                           @RequestParam("vnp_OrderInfo") String vnpOrderInfo) {
        try {
            paymentService.savePayment(vnpResponseCode, vnpOrderInfo);
            return new ResponseData(Enums.ResponseStatus.SUCCESS, ResponseMessageConstants.PAYMENT_SUCCESS);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @PutMapping("/change-payment-active")
    @Operation(summary = "thay đổi job được gán vào gói dịch vụ còn hạn")
    public ResponseData changeJobActivePayment(@RequestBody JobActivePaymentDTO request) {
        try {
            //Check expired date
            Payment payment = paymentService.findById(request.getId());
            if (payment == null) {
                throw new HiveConnectException("Không tồn tại thanh toán nào cho dịch vụ này.");
            }
            LocalDateTime now = LocalDateTime.now();
            int a = now.compareTo(payment.getExpiredDate());
            if (a >= 0) {
                throw new HiveConnectException("Gói dịch vụ đã hết hạn, Không thể gắn công việc vào gói.");
            }
            payment.setJobId(request.getJobId());
            payment.update();
            paymentService.updatePayment(payment);
            return new ResponseData(Enums.ResponseStatus.SUCCESS, ResponseMessageConstants.CHANGE_JOB_PAYMENT_ACTIVE_SUCCESS);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @GetMapping("/payment-information")
    public ResponseData transactionHandle(@RequestParam(defaultValue = "0") Integer pageNo,
                                          @RequestParam(defaultValue = "10") Integer pageSize,
                                          @RequestParam(defaultValue = "0", value = "recruiterId", required = false) long recruiterId,
                                          @RequestParam(defaultValue = "0", value = "rentalPackageId", required = false) long rentalPackageId,
                                          @RequestParam(defaultValue = "0", value = "bannerId", required = false) long bannerId,
                                          @RequestParam(value = "transactionCode", required = false) String transactionCode,
                                          @RequestParam(value = "orderType", required = false) String orderType) {
        try {
            ResponseDataPagination paymentList = paymentService.
                    getListPaymentFilter(pageNo, pageSize, recruiterId, rentalPackageId, bannerId, transactionCode, orderType);
            return new ResponseData(Enums.ResponseStatus.SUCCESS, ResponseMessageConstants.GET_LIST_SUCCESS, paymentList);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @GetMapping("/purchased-package")
    @Operation(summary = "kiểm tra rec đã mua gói package nào và gói package đó còn trong thời hạn sử dụng ko")
    public ResponseData recruiterBuyPackage(@RequestParam(value = "recruiterId") long recruiterId) {
        try {
            List<PaymentResponse> payment = paymentService.findRecruiterPurchasedPackage(recruiterId);
            if (payment.isEmpty()) {
                throw new HiveConnectException("Nhà tuyển dụng chưa mua gói dịch vụ nào.");
            }
            return new ResponseData(Enums.ResponseStatus.SUCCESS, ResponseMessageConstants.SUCCESS, payment);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @GetMapping("/total-revenue")
    public ResponseData getTotalProfit(@RequestParam(value = "startDate") String startDate,
                                       @RequestParam(value = "endDate") String endDate,
                                       @RequestParam(defaultValue = "0") Integer pageNo,
                                       @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime start = LocalDate.parse(startDate, formatter).atStartOfDay();
            LocalDateTime end = LocalDate.parse(endDate, formatter).atStartOfDay();

            ResponseDataPaginationRevenue pagination = paymentService.getRevenue(start, end, pageNo, pageSize);
            return new ResponseData(Enums.ResponseStatus.SUCCESS, ResponseMessageConstants.TOTAL_REVENUE, pagination);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @GetMapping("/export-revenue")
    public ResponseData exportRevenue(@RequestParam(value = "startDate") String startDate,
                                      @RequestParam(value = "endDate") String endDate,
                                      HttpServletResponse response) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime start = LocalDate.parse(startDate, formatter).atStartOfDay();
            LocalDateTime end = LocalDate.parse(endDate, formatter).atStartOfDay();

            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            String currentDateTime = dateFormatter.format(new Date());
            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=revenue" + currentDateTime + ".xlsx";
            response.setHeader(headerKey, headerValue);

            List<RevenueResponse> responseList = paymentService.getRevenueExporter(start, end);

            ExcelExporter excelExporter = new ExcelExporter(responseList);
            excelExporter.export(response);

            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus());
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }

    @GetMapping("/suggest-job-with-pay")
    public ResponseData suggestJobWithPay() {
        try {
            List<Long> listJobId = paymentService.getListJobIdInPayment();
            List<JobResponse> list = new ArrayList<>();
            for (Long id : listJobId) {
                Job job = jobService.findById(id).get();
                Company company = companyService.getCompanyById(job.getCompanyId());
                Optional<Image> image = imageService.getImageCompany(company.getId(), true);
                JobResponse response = modelMapper.map(job, JobResponse.class);
                response.setCompanyName(company.getName());
                response.setCompanyAvatar(image.get().getUrl());
                list.add(response);
            }
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, list);
        } catch (Exception e) {
            String msg = LogUtils.printLogStackTrace(e);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
        }
    }
}
