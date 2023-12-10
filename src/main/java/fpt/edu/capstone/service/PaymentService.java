package fpt.edu.capstone.service;

import fpt.edu.capstone.dto.payment.PaymentDTO;
import fpt.edu.capstone.dto.payment.PaymentResponse;
import fpt.edu.capstone.dto.payment.PaymentResponseDTO;
import fpt.edu.capstone.dto.payment.RevenueResponse;
import fpt.edu.capstone.entity.Payment;
import fpt.edu.capstone.utils.ResponseDataPagination;
import fpt.edu.capstone.utils.ResponseDataPaginationRevenue;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentService {
    PaymentResponseDTO getPaymentVNPay(PaymentDTO paymentDTO) throws UnsupportedEncodingException;

    ResponseDataPagination getListPaymentFilter(Integer pageNo, Integer pageSize, long recruiterId, long rentalPackageId,
                                                long bannerId, String transactionCode, String orderType);

    List<PaymentResponse> findRecruiterPurchasedPackage(long recruiterId);

    ResponseDataPaginationRevenue getRevenue(LocalDateTime start, LocalDateTime end, Integer pageNo, Integer pageSize);

    List <RevenueResponse> getRevenueExporter(LocalDateTime start, LocalDateTime end);

    void savePayment(String vnpResponseCode, String vnpOrderInfo);

    Payment findById(long id);

    void updatePayment(Payment payment);

    List<Payment> findAll();

    void save(Payment payment);

    Payment findByIdAndRecruiterId(long id, long recruiterId);

    List<Long> getListJobIdInPayment();

    List<Payment> getPaymentBannerInUse(long bannerId);

    List<Payment> getPaymentNormalPackageInUse(long id);

    Optional<Payment> findByJobId(long jobId);
}
