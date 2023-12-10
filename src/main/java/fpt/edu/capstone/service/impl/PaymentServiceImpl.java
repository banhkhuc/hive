package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.common.payment.PaymentConfig;
import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.dto.payment.PaymentDTO;
import fpt.edu.capstone.dto.payment.PaymentResponse;
import fpt.edu.capstone.dto.payment.PaymentResponseDTO;
import fpt.edu.capstone.dto.payment.RevenueResponse;
import fpt.edu.capstone.entity.*;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.repository.PaymentRepository;
import fpt.edu.capstone.service.*;
import fpt.edu.capstone.utils.Enums;
import fpt.edu.capstone.utils.Pagination;
import fpt.edu.capstone.utils.ResponseDataPagination;
import fpt.edu.capstone.utils.ResponseDataPaginationRevenue;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final ModelMapper modelMapper;

    private final PaymentRepository paymentRepository;

    private final RecruiterService recruiterService;

    private final DetailPackageService detailPackageService;

    private final RentalPackageService rentalPackageService;

    private final JobService jobService;

    private final BannerService bannerService;

    @Override
    public PaymentResponseDTO getPaymentVNPay(PaymentDTO paymentDTO) throws UnsupportedEncodingException {
        Payment payment = modelMapper.map(paymentDTO, Payment.class);
        String randomTransactionCode = PaymentConfig.getRandomNumber(8);
        payment.setTransactionCode(randomTransactionCode);
        int amount = paymentDTO.getAmount() * 100;

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = formatter.format(date);
        String vnpCreateDate = dateString;

        String recruiterId = String.valueOf(paymentDTO.getRecruiterId());
        String detailPackageId = String.valueOf(paymentDTO.getDetailPackageId());
        String bannerId = String.valueOf(paymentDTO.getBannerId());
        String jobId = String.valueOf(paymentDTO.getJobId());
        String description = paymentDTO.getDescription();
        String orderType = paymentDTO.getOrderType();
        String p = "recruiterId " + recruiterId + " jobId " + jobId + " detailPackageId " + detailPackageId +
                " bannerId " + bannerId + " amount " + String.valueOf(amount) + " bankCode " + paymentDTO.getBankCode() +
                " description " + description + " orderType " + orderType;

        Map<String, String> vnpParams = new HashMap<>();
        vnpParams.put("vnp_Version", PaymentConfig.VERSION_VNPAY);
        vnpParams.put("vnp_Command", PaymentConfig.COMMAND);
        vnpParams.put("vnp_TmnCode", PaymentConfig.TMN_CODE);
        vnpParams.put("vnp_Amount", String.valueOf(amount));
        vnpParams.put("vnp_CreateDate", vnpCreateDate);
        vnpParams.put("vnp_CurrCode", PaymentConfig.CURR_CODE);
        vnpParams.put("vnp_IpAddr", PaymentConfig.IP_DEFAULT);

        vnpParams.put("vnp_Locale", PaymentConfig.LOCATE_DEFAULT);
        vnpParams.put("vnp_OrderInfo", p);
        vnpParams.put("vnp_OrderType", PaymentConfig.ORDER_TYPE);
        vnpParams.put("vnp_ReturnUrl", PaymentConfig.RETURN_URL);
        vnpParams.put("vnp_TxnRef", payment.getTransactionCode());
        vnpParams.put("vnp_BankCode", paymentDTO.getBankCode());


        List fieldNames = new ArrayList(vnpParams.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnpParams.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = PaymentConfig.hmacSHA512(PaymentConfig.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = PaymentConfig.vnp_PayUrl + "?" + queryUrl;

        PaymentResponseDTO result = new PaymentResponseDTO();
        result.setPaymentUrl(paymentUrl);
        return result;
    }

    @Override
    public ResponseDataPagination getListPaymentFilter(Integer pageNo, Integer pageSize, long recruiterId, long rentalPackageId,
                                              long bannerId, String transactionCode, String orderType) {
        int pageReq = pageNo >= 1 ? pageNo - 1 : pageNo;
        Pageable pageable = PageRequest.of(pageReq, pageSize);
        Page<Payment> paymentList = paymentRepository.
                getListPaymentFilter(pageable, recruiterId, rentalPackageId, bannerId, transactionCode, orderType);

        ResponseDataPagination responseDataPagination = new ResponseDataPagination();
        Pagination pagination = new Pagination();
        responseDataPagination.setData(paymentList.getContent());
        pagination.setCurrentPage(pageNo);
        pagination.setPageSize(pageSize);
        pagination.setTotalPage(paymentList.getTotalPages());
        pagination.setTotalRecords(Integer.parseInt(String.valueOf(paymentList.getTotalElements())));
        responseDataPagination.setStatus(Enums.ResponseStatus.SUCCESS.getStatus());
        responseDataPagination.setPagination(pagination);
        return responseDataPagination;
    }

    @Override
    public List<PaymentResponse> findRecruiterPurchasedPackage(long recruiterId) {

        List<Payment> payments = paymentRepository.findByRecruiterIdAndExpiredStatusFalse(recruiterId);
        if (payments == null) {
            throw new HiveConnectException(ResponseMessageConstants.NO_PURCHASED_PACKAGE);
        }
        List<PaymentResponse> responseList = new ArrayList<>();
        for (Payment payment : payments) {
            PaymentResponse response = new PaymentResponse();
            response.setPaymentId(payment.getId());
            response.setRecruiterId(payment.getRecruiterId());
            response.setDetailPackageId(payment.getDetailPackageId());
            if (payment.getDetailPackageId() > 0) {
                DetailPackage normalPP = detailPackageService.findById(payment.getDetailPackageId());
                response.setDetailPackageName(normalPP.getDetailName());
            } else {
                if (payment.getBannerId() > 0) {
                    Banner banner = bannerService.findById(payment.getBannerId());
                    response.setDetailPackageName(banner.getTitle());
                }
            }
            response.setBannerId(payment.getBannerId());
            response.setJobId(payment.getJobId());
            response.setAmount(payment.getAmount());
            response.setOrderType(payment.getOrderType());
            response.setDescription(payment.getDescription());
            response.setBankCode(payment.getBankCode());

            responseList.add(response);
        }
        return responseList;
    }

    @Override
    public ResponseDataPaginationRevenue getRevenue(LocalDateTime start, LocalDateTime end, Integer pageNo, Integer pageSize) {
        int pageReq = pageNo >= 1 ? pageNo - 1 : pageNo;
        Pageable pageable = PageRequest.of(pageReq, pageSize);
        Page<Payment> payments = paymentRepository.getRevenueInMonth(start, end, pageable);
        List<Payment> totalRevenue = payments.getContent();
        List<RevenueResponse> revenueResponseList = totalRevenue.stream().
                map(payment -> modelMapper.map(payment, RevenueResponse.class)).collect(Collectors.toList());
        long total = 0;
        for (RevenueResponse response : revenueResponseList) {
            if (response.getBannerId() == 0) {
                String rentalPackageName = rentalPackageService.getRentalPackageName(response.getDetailPackageId());
                response.setRentalPackageName(rentalPackageName);
            } else {
                response.setRentalPackageName("Banner quảng cáo");
            }
            Recruiter recruiter = recruiterService.getRecruiterById(response.getRecruiterId());
            response.setRecruiterName(recruiter.getFullName());
            total += response.getAmount();
        }

        ResponseDataPaginationRevenue responseDataPagination = new ResponseDataPaginationRevenue();
        Pagination pagination = new Pagination();
        responseDataPagination.setData(revenueResponseList);
        pagination.setCurrentPage(pageNo);
        pagination.setPageSize(pageSize);
        pagination.setTotalPage(payments.getTotalPages());
        pagination.setTotalRecords(Integer.parseInt(String.valueOf(payments.getTotalElements())));
        responseDataPagination.setStatus(Enums.ResponseStatus.SUCCESS.getStatus());
        responseDataPagination.setPagination(pagination);
        responseDataPagination.setTotalRevenue(total);
        return responseDataPagination;
    }

    @Override
    public List<RevenueResponse> getRevenueExporter(LocalDateTime start, LocalDateTime end) {
        List<Payment> totalRevenue = paymentRepository.getRevenueInMonthExporter(start, end);
        List<RevenueResponse> revenueResponseList = totalRevenue.stream().
                map(payment -> modelMapper.map(payment, RevenueResponse.class)).collect(Collectors.toList());
        long total = 0;
        for (RevenueResponse response : revenueResponseList) {
            if (response.getBannerId() == 0) {
                String rentalPackageName = rentalPackageService.getRentalPackageName(response.getDetailPackageId());
                response.setRentalPackageName(rentalPackageName);
            } else {
                response.setRentalPackageName("Banner quảng cáo");
            }
            Recruiter recruiter = recruiterService.getRecruiterById(response.getRecruiterId());
            response.setRecruiterName(recruiter.getFullName());
            total += response.getAmount();
        }
        return revenueResponseList;
    }

    @Override
    public void savePayment(String vnpResponseCode, String vnpOrderInfo) {
        PaymentDTO paymentDTO = new PaymentDTO();

        String[] ss = vnpOrderInfo.split(" ");
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < ss.length; i = i + 2) {
            map.put(ss[i], ss[i + 1]);
        }
        map.forEach((key, value) -> {
            if (key.equals("recruiterId")) paymentDTO.setRecruiterId(Long.parseLong(value));
            if (key.equals("detailPackageId")) paymentDTO.setDetailPackageId(Long.parseLong(value));
            if (key.equals("bannerId")) paymentDTO.setBannerId(Long.parseLong(value));
            if (key.equals("amount")) paymentDTO.setAmount(Integer.parseInt(value) / 100);
            if (key.equals("description")) paymentDTO.setDescription(value);
            if (key.equals("orderType")) paymentDTO.setOrderType(value);
            if (key.equals("bankCode")) paymentDTO.setBankCode(value);
            if (key.equals("jobId")) paymentDTO.setJobId(Long.parseLong(value));
        });
        if(paymentDTO.getBannerId() == 0){
            DetailPackage getName = detailPackageService.findById(paymentDTO.getDetailPackageId());
            String name  = getName.getDetailName();
            paymentDTO.setOrderType("MUA_" +name);
        } else {
            Banner getName = bannerService.findById(paymentDTO.getBannerId());
            String bannerName = getName.getTitle();
            paymentDTO.setOrderType("MUA_"+bannerName);
        }

        LocalDateTime now = LocalDateTime.now();

        Payment payment = modelMapper.map(paymentDTO, Payment.class);
        Recruiter recruiter = recruiterService.getRecruiterById(payment.getRecruiterId());
        if (recruiter == null) {
            throw new HiveConnectException("Nhà tuyển dụng có id = " + recruiter.getId() + "không tồn tại");
        }
        payment.setCommand(PaymentConfig.COMMAND);
        payment.setCurrCode(PaymentConfig.CURR_CODE);
        payment.setLocal(PaymentConfig.LOCATE_DEFAULT);
        String randomTransactionCode = PaymentConfig.getRandomNumber(8);
        payment.setTransactionCode(randomTransactionCode);
        payment.setExpiredDate(now.plusDays(14));
        payment.create();

        if (vnpResponseCode.equals("00")) {
            if ((payment.getBannerId() == 0 && payment.getDetailPackageId() == 0) || (payment.getBannerId() > 0 && payment.getDetailPackageId() > 0)) {
                throw new HiveConnectException(ResponseMessageConstants.PLEASE_TRY_TO_CONTACT_ADMIN);
            }
            System.out.println("Thanh toán thành công");
            paymentRepository.save(payment);
            //Kích hoạt gói dịch vụ chính / gói xem hồ sơ ứng viên
            if (payment.getDetailPackageId() > 0) {
                DetailPackage detailPackage = detailPackageService.findById(payment.getDetailPackageId());
                //Kích hoạt số lượng cv mà recruiter được xem full thông tin
                if (detailPackage.getRentalPackageId() == 1) {
                    Integer totalCv = paymentRepository.countByTotalCvView(payment.getRecruiterId());
                    recruiterService.updateTotalCvView(totalCv, payment.getRecruiterId());
                }
                if (detailPackage.getRentalPackageId() == 2) {
                    //Kích hoạt tính năng gói package đó cho Job tương ứng
//                    Job job = jobService.getJobById(payment.getJobId());
//                    job.setPopularJob(true);
//                    job.setUrgentJob(true);
//                    job.setNewJob(true);
//                    job.update();
//                    jobService.saveJob(job);
                }
            }
        }
        if (vnpResponseCode.equals("07")) {
            throw new HiveConnectException(ResponseMessageConstants.VNP_RESPONSE_CODE_07);
        }
        if (vnpResponseCode.equals("09")) {
            throw new HiveConnectException(ResponseMessageConstants.VNP_RESPONSE_CODE_09);
        }
        if (vnpResponseCode.equals("10")) {
            throw new HiveConnectException(ResponseMessageConstants.VNP_RESPONSE_CODE_10);
        }
        if (vnpResponseCode.equals("11")) {
            throw new HiveConnectException(ResponseMessageConstants.VNP_RESPONSE_CODE_11);
        }
        if (vnpResponseCode.equals("12")) {
            throw new HiveConnectException(ResponseMessageConstants.VNP_RESPONSE_CODE_12);
        }
        if (vnpResponseCode.equals("13")) {
            throw new HiveConnectException(ResponseMessageConstants.VNP_RESPONSE_CODE_13);
        }
        if (vnpResponseCode.equals("24")) {
            throw new HiveConnectException(ResponseMessageConstants.VNP_RESPONSE_CODE_24);
        }
        if (vnpResponseCode.equals("51")) {
            throw new HiveConnectException(ResponseMessageConstants.VNP_RESPONSE_CODE_51);
        }
        if (vnpResponseCode.equals("65")) {
            throw new HiveConnectException(ResponseMessageConstants.VNP_RESPONSE_CODE_65);
        }
        if (vnpResponseCode.equals("79")) {
            throw new HiveConnectException(ResponseMessageConstants.VNP_RESPONSE_CODE_79);
        }
        if (vnpResponseCode.equals("99")) {
            throw new HiveConnectException(ResponseMessageConstants.VNP_RESPONSE_CODE_99);
        }
    }

    @Override
    public Payment findById(long id) {
        if (!paymentRepository.findById(id).isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.PAYMENT_DOES_NOT_EXIST);
        }
        return paymentRepository.findById(id).get();
    }

    @Override
    public void updatePayment(Payment payment) {
        paymentRepository.saveAndFlush(payment);
    }

    @Override
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    @Override
    public void save(Payment payment) {
        paymentRepository.save(payment);
    }

    @Override
    public Payment findByIdAndRecruiterId(long id, long recruiterId) {
        return paymentRepository.findByIdAndRecruiterId(id, recruiterId);
    }

    @Override
    public List<Long> getListJobIdInPayment() {
        return paymentRepository.getListJobIdInPayment();
    }

    @Override
    public List<Payment> getPaymentBannerInUse(long bannerId) {
        return paymentRepository.findByBannerIdAndExpiredStatusIsFalse(bannerId);
    }

    @Override
    public List<Payment> getPaymentNormalPackageInUse(long detailPackageId) {
        return paymentRepository.findByDetailPackageIdAndExpiredStatusIsFalse(detailPackageId);
    }

    @Override
    public Optional<Payment> findByJobId(long jobId) {
        return paymentRepository.findByJobId(jobId);
    }
}
