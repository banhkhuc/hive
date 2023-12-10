package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.dto.payment.PaymentDTO;
import fpt.edu.capstone.dto.payment.PaymentResponse;
import fpt.edu.capstone.dto.payment.PaymentResponseDTO;
import fpt.edu.capstone.dto.payment.RevenueResponse;
import fpt.edu.capstone.entity.*;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.repository.PaymentRepository;
import fpt.edu.capstone.utils.Enums;
import fpt.edu.capstone.utils.Pagination;
import fpt.edu.capstone.utils.ResponseDataPagination;
import fpt.edu.capstone.utils.ResponseDataPaginationRevenue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceImplTest {

	@InjectMocks
	PaymentServiceImpl paymentService;

	@Mock
	ModelMapper modelMapper;

	@Mock
	RecruiterServiceImpl recruiterService;

	@Mock
	DetailPackageServiceImpl detailPackageService;

	@Mock
	BannerServiceImpl bannerService;

	@Mock
	PaymentRepository paymentRepository;

	@Mock
	JobServiceImpl jobService;

	@Mock
	RentalPackageServiceImpl rentalPackageService;

	@BeforeEach
	public void init() {
		MockitoAnnotations.openMocks(this);
	}

	private Recruiter recruiter(){
		Recruiter recruiter = new Recruiter();
		recruiter.setId(1L);
		recruiter.setCompanyId(1L);
		recruiter.setCompanyName("companyName");
		recruiter.setFullName("fullName");
		recruiter.setVerifyAccount(true);
		recruiter.setGender(false);
		recruiter.setPosition("HR");
		recruiter.setLinkedinAccount("linkedinAccount");
		recruiter.setAdditionalLicense("additionalLicense");
		recruiter.setBusinessLicenseUrl("businessLicenseUrl");
		recruiter.setAdditionalLicenseUrl("additionalLicenseUrl");
		recruiter.setUserId(1L);
		recruiter.setDeleted(false);
		recruiter.setCompanyAddress("companyAddress");
		recruiter.setBusinessLicenseApprovalStatus("businessLicenseApprovalStatus");
		recruiter.setAvatarUrl("avatarUrl");
		recruiter.setTotalCvView(10);
		return recruiter;
	}

	Payment payment(){
		Payment payment = new Payment(1L, 1L, 1L, 1L, 1L, "transactionCode", 0, "description", "orderType", "bankCode", "command",
						"currCode", "local", LocalDateTime.of(2021, 10, 1, 0, 0, 0), false);
		return payment;
	}
	
	DetailPackage detailPackage(){
		DetailPackage detailPackage = new DetailPackage(1L, 1L, "detailName", 1231L, 1L, "timeExpired",
				"description", false, false, false, 0, false,false,false);
		return detailPackage;
	}
	@Test
	void givenVnpOrderInfoInvalid_whenCallSavePayment_thenModelMapperMapAreNotCalled() {
		String vnpOrderInfo = "recruiterId a detailPackageId b bannerId 1 amount 1 description abc orderType abc bankCode abc jobId 1";
		String vnpResponseCode = "00";

		Exception exception = assertThrows(Exception.class, () -> paymentService.savePayment(vnpResponseCode, vnpOrderInfo));

		verify(modelMapper, times(0)).map(ArgumentMatchers.any(),any());
	}

	@Test
	void givenModelMapperMapReturnNull_whenCallSavePayment_thenRecruiterServiceGetRecruiterByIdAreNotCalled() {
		String vnpOrderInfo = "recruiterId 1 detailPackageId 1 bannerId 1 amount 1 description abc orderType abc bankCode abc jobId 1";
		String vnpResponseCode = "00";

		PaymentDTO paymentDTO = handlerString(vnpOrderInfo);

		when(modelMapper.map(paymentDTO, Payment.class)).thenReturn(null);

		Exception exception = assertThrows(Exception.class, () -> paymentService.savePayment(vnpResponseCode, vnpOrderInfo));

		verify(recruiterService, times(0)).getRecruiterById(1L);
	}

	@Test
	void givenRecruiterServiceGetRecruiterByIdReturnNull_whenCallSavePayment_thenThrowException() {
		String vnpOrderInfo = "recruiterId 1 detailPackageId 1 bannerId 1 amount 1 description abc orderType abc bankCode abc jobId 1";
		String vnpResponseCode = "00";

		Payment payment = new Payment();
		payment.setRecruiterId(1L);

		when(modelMapper.map(ArgumentMatchers.any(), any())).thenReturn(payment);
		when(recruiterService.getRecruiterById(anyLong())).thenReturn(null);

		Exception exception = assertThrows(Exception.class, () -> paymentService.savePayment(vnpResponseCode, vnpOrderInfo));

		assertEquals(null, exception.getMessage());
	}

//	@Test
//	public void testGetListPaymentFilter() {
//		final List<Payment> paymentList = Arrays.asList();
//		when(paymentRepository.getListPaymentFilter(1L, 1L, 1L, "transactionCode", "orderType"))
//				.thenReturn(paymentList);
//		final List<Payment> result = paymentService.getListPaymentFilter(1L, 1L, 1L, "transactionCode",
//				"orderType");
//	}
//
//	@Test
//	public void testGetListPaymentFilter_PaymentRepositoryReturnsNoItems() {
//		when(paymentRepository.getListPaymentFilter(1L, 1L, 1L, "transactionCode", "orderType"))
//				.thenReturn(Collections.emptyList());
//		final List<Payment> result = paymentService.getListPaymentFilter(1L, 1L, 1L, "transactionCode",
//				"orderType");
//		assertThat(result).isEqualTo(Collections.emptyList());
//	}

	@Test
	public void testFindRecruiterPurchasedPackage() {
		final List<Payment> paymentList = Arrays.asList(payment());
		when(paymentRepository.findByRecruiterIdAndExpiredStatusFalse(1L)).thenReturn(paymentList);
		final DetailPackage detailPackage = detailPackage();
		when(detailPackageService.findById(1L)).thenReturn(detailPackage);
		final Banner banner = new Banner(1L, 1L, 123456L, 123L, "timeExpired", "detailName", "description", "image", false,
				false, false, false, false, false, false, false);
		when(bannerService.findById(1L)).thenReturn(banner);
		final List<PaymentResponse> result = paymentService.findRecruiterPurchasedPackage(1L);
	}

	@Test
	public void testFindRecruiterPurchasedPackage_PaymentRepositoryReturnsNull() {
		when(paymentRepository.findByRecruiterIdAndExpiredStatusFalse(1L)).thenReturn(null);
		assertThatThrownBy(() -> paymentService.findRecruiterPurchasedPackage(1L))
				.isInstanceOf(HiveConnectException.class);
	}

	@Test
	public void testFindRecruiterPurchasedPackage_PaymentRepositoryReturnsNoItems() {
		when(paymentRepository.findByRecruiterIdAndExpiredStatusFalse(1L)).thenReturn(Collections.emptyList());
		final DetailPackage detailPackage = detailPackage();
		when(detailPackageService.findById(1L)).thenReturn(detailPackage);
		final Banner banner = new Banner(1L, 1L, 1L, 1L, "timeExpired", "detailName", "description", "image", false,
				false, false, false, false, false, false, false);
		when(bannerService.findById(1L)).thenReturn(banner);
		final List<PaymentResponse> result = paymentService.findRecruiterPurchasedPackage(1L);
		assertThat(result).isEqualTo(Collections.emptyList());
	}

	@Test
	public void testGetRevenueExporter() {
		final List<Payment> paymentList = Arrays.asList(
				payment());
		when(paymentRepository.getRevenueInMonthExporter(LocalDateTime.of(2021, 10, 1, 0, 0, 0),
				LocalDateTime.of(2021, 10, 1, 0, 0, 0))).thenReturn(paymentList);
		final RevenueResponse response = new RevenueResponse();
		response.setId(1L);
		response.setRecruiterId(1L);
		response.setRecruiterName("fullName");
		response.setJobId(1L);
		response.setDetailPackageId(1L);
		response.setRentalPackageName("Banner quảng cáo");
		response.setBannerId(1L);
		response.setTransactionCode("transactionCode");
		response.setAmount(0);
		response.setDescription("description");
		response.setOrderType("orderType");
		response.setBankCode("bankCode");
		response.setCommand("command");
		response.setCurrCode("currCode");
		response.setLocal("local");
		when(modelMapper.map(any(Object.class), eq(RevenueResponse.class))).thenReturn(response);

		when(rentalPackageService.getRentalPackageName(1L)).thenReturn("Banner quảng cáo");
		final Recruiter recruiter = recruiter();
		when(recruiterService.getRecruiterById(1L)).thenReturn(recruiter);
		final List<RevenueResponse> result = paymentService.getRevenueExporter(
				LocalDateTime.of(2021, 10, 1, 0, 0, 0), LocalDateTime.of(2021, 10, 1, 0, 0, 0));
	}

	@Test
	public void testGetRevenueExporter_PaymentRepositoryReturnsNoItems() {
		when(paymentRepository.getRevenueInMonthExporter(LocalDateTime.of(2021, 10, 1, 0, 0, 0),
				LocalDateTime.of(2021, 10, 1, 0, 0, 0))).thenReturn(Collections.emptyList());
		final RevenueResponse response = new RevenueResponse();
		response.setId(1L);
		response.setRecruiterId(1L);
		response.setRecruiterName("fullName");
		response.setJobId(1L);
		response.setDetailPackageId(1L);
		response.setRentalPackageName("Banner quảng cáo");
		response.setBannerId(1L);
		response.setTransactionCode("transactionCode");
		response.setAmount(0);
		response.setDescription("description");
		response.setOrderType("orderType");
		response.setBankCode("bankCode");
		response.setCommand("command");
		response.setCurrCode("currCode");
		response.setLocal("local");
		when(modelMapper.map(any(Object.class), eq(RevenueResponse.class))).thenReturn(response);
		when(rentalPackageService.getRentalPackageName(1L)).thenReturn("Banner quảng cáo");
		final Recruiter recruiter = recruiter();
		when(recruiterService.getRecruiterById(1L)).thenReturn(recruiter);
		final List<RevenueResponse> result = paymentService.getRevenueExporter(
				LocalDateTime.of(2021, 10, 1, 0, 0, 0), LocalDateTime.of(2021, 10, 1, 0, 0, 0));
		assertThat(result).isEqualTo(Collections.emptyList());
	}

	@Test
	public void testFindById() throws Exception {
		final Optional<Payment> payment = Optional.of(
				payment());
		when(paymentRepository.findById(1L)).thenReturn(payment);
		final Payment result = paymentService.findById(1L);
	}

	@Test
	public void testFindById_PaymentRepositoryReturnsAbsent() {
		when(paymentRepository.findById(1L)).thenReturn(Optional.empty());
		assertThatThrownBy(() -> paymentService.findById(1L)).isInstanceOf(HiveConnectException.class);
	}

	@Test
	public void testUpdatePayment() {
		final Payment payment = payment();
		final Payment payment1 = payment();
		when(paymentRepository.saveAndFlush(any(Payment.class))).thenReturn(payment1);
		paymentService.updatePayment(payment);
		verify(paymentRepository).saveAndFlush(any(Payment.class));
	}

	@Test
	public void testFindAll() {
		final List<Payment> paymentList = Arrays.asList(
				payment());
		when(paymentRepository.findAll()).thenReturn(paymentList);
		final List<Payment> result = paymentService.findAll();
	}

	@Test
	public void testFindAll_PaymentRepositoryReturnsNoItems() {
		when(paymentRepository.findAll()).thenReturn(Collections.emptyList());
		final List<Payment> result = paymentService.findAll();
		assertThat(result).isEqualTo(Collections.emptyList());
	}

	@Test
	public void testSave() {
		final Payment payment = payment();
		final Payment payment1 = payment();
		when(paymentRepository.save(any(Payment.class))).thenReturn(payment1);
		paymentService.save(payment);
		verify(paymentRepository).save(any(Payment.class));
	}

	@Test
	public void testFindByIdAndRecruiterId() {
		final Payment payment = payment();
		when(paymentRepository.findByIdAndRecruiterId(1L, 1L)).thenReturn(payment);
		final Payment result = paymentService.findByIdAndRecruiterId(1L, 1L);
	}

	@Test
	public void testGetListJobIdInPayment() {
		when(paymentRepository.getListJobIdInPayment()).thenReturn(Arrays.asList(1L));
		final List<Long> result = paymentService.getListJobIdInPayment();
		assertThat(result).isEqualTo(Arrays.asList(1L));
	}

	@Test
	public void testGetListJobIdInPayment_PaymentRepositoryReturnsNoItems() {
		when(paymentRepository.getListJobIdInPayment()).thenReturn(Collections.emptyList());
		final List<Long> result = paymentService.getListJobIdInPayment();
		assertThat(result).isEqualTo(Collections.emptyList());
	}

	@Test
	void givenPaymentGetBannerIdGreaterThan0AndPaymentGetDetailPackageIdGreaterThan0_whenCallSavePayment_thenThrowException() {
		String vnpOrderInfo = "recruiterId 1 detailPackageId 1 bannerId 1 amount 1 description abc orderType abc bankCode abc jobId 1";
		String vnpResponseCode = "00";
		Payment payment = new Payment();
		payment.setRecruiterId(1L);
		payment.setBannerId(1);
		payment.setDetailPackageId(1);
		when(modelMapper.map(ArgumentMatchers.any(), any())).thenReturn(payment);

		Recruiter recruiter = new Recruiter();
		when(recruiterService.getRecruiterById(anyLong())).thenReturn(recruiter);

		Exception exception = assertThrows(Exception.class, () -> paymentService.savePayment(vnpResponseCode, vnpOrderInfo));

		assertEquals(null, exception.getMessage());
	}

	@Test
	void givenPaymentGetBannerIdEquals0AndPaymentGetDetailPackageIdEquals0_whenCallSavePayment_thenThrowException() {
		String vnpOrderInfo = "recruiterId 1 detailPackageId 1 bannerId 1 amount 1 description abc orderType abc bankCode abc jobId 1";
		String vnpResponseCode = "00";
		Payment payment = new Payment();
		payment.setRecruiterId(1L);
		payment.setBannerId(0);
		payment.setDetailPackageId(0);
		when(modelMapper.map(ArgumentMatchers.any(), any())).thenReturn(payment);

		Recruiter recruiter = new Recruiter();
		when(recruiterService.getRecruiterById(anyLong())).thenReturn(recruiter);

		Exception exception = assertThrows(Exception.class, () -> paymentService.savePayment(vnpResponseCode, vnpOrderInfo));

		assertEquals(null, exception.getMessage());
	}

	@Test
	void givenVnpResponseCodeEquals00AndGetRentalPackageIdEquals1_thenCallRecruiterServiceUpdateTotalCvView() {
		String vnpOrderInfo = "recruiterId 1 detailPackageId 1 bannerId 1 amount 1 description abc orderType abc bankCode abc jobId 1";
		String vnpResponseCode = "00";
		Payment payment = new Payment();
		payment.setRecruiterId(1L);
		payment.setBannerId(0);
		payment.setDetailPackageId(1);
		when(modelMapper.map(ArgumentMatchers.any(), any())).thenReturn(payment);

		Recruiter recruiter = new Recruiter();
		when(recruiterService.getRecruiterById(anyLong())).thenReturn(recruiter);

		when(paymentRepository.save(any())).thenReturn(payment);

		DetailPackage detailPackage = new DetailPackage();
		detailPackage.setRentalPackageId(1L);
		when(detailPackageService.findById(1)).thenReturn(detailPackage);

		when(paymentRepository.countByTotalCvView(1L)).thenReturn(1);

		paymentService.savePayment(vnpResponseCode, vnpOrderInfo);

		verify(recruiterService, times(1)).updateTotalCvView(1, 1L);
	}

	//when (payment.getBannerId() == 0 && payment.getDetailPackageId() == 0) ||
	// 	(payment.getBannerId() > 0 && payment.getDetailPackageId() > 0) == false
	@Test
	void givenVnpResponseCodeEquals00AndGetRentalPackageIdEquals1AndPaymentRepositoryCountByTotalCvViewReturnNullReturnNull_whenCallSavePayment_thenJobServiceSaveJobAreNotCalled() {
		String vnpOrderInfo = "recruiterId 1 detailPackageId 1 bannerId 1 amount 1 description abc orderType abc bankCode abc jobId 1";
		String vnpResponseCode = "00";
		Payment payment = new Payment();
		payment.setRecruiterId(1L);
		payment.setBannerId(0);
		payment.setDetailPackageId(1);
		payment.setJobId(1L);
		when(modelMapper.map(ArgumentMatchers.any(), any())).thenReturn(payment);

		Recruiter recruiter = new Recruiter();
		when(recruiterService.getRecruiterById(anyLong())).thenReturn(recruiter);

		when(paymentRepository.save(any())).thenReturn(payment);

		DetailPackage detailPackage = new DetailPackage();
		detailPackage.setRentalPackageId(2L);
		when(jobService.getJobById(1L)).thenReturn(null);

		Exception exception = assertThrows(Exception.class, () -> paymentService.savePayment(vnpResponseCode, vnpOrderInfo));

		verify(recruiterService, times(0)).updateTotalCvView(1, 1L);
	}

	//when (payment.getBannerId() == 0 && payment.getDetailPackageId() == 0) ||
	// 	(payment.getBannerId() > 0 && payment.getDetailPackageId() > 0) == false
	@Test
	void givenVnpResponseCodeEquals00AndGetRentalPackageIdEquals2_whenCallSavePayment_thenCallJobServiceSaveJob() {
		String vnpOrderInfo = "recruiterId 1 detailPackageId 1 bannerId 1 amount 1 description abc orderType abc bankCode abc jobId 1";
		String vnpResponseCode = "00";
		Payment payment = new Payment();
		payment.setRecruiterId(1L);
		payment.setBannerId(0);
		payment.setDetailPackageId(1);
		payment.setJobId(1L);
		when(modelMapper.map(ArgumentMatchers.any(), any())).thenReturn(payment);

		Recruiter recruiter = new Recruiter();
		when(recruiterService.getRecruiterById(anyLong())).thenReturn(recruiter);

		when(paymentRepository.save(any())).thenReturn(payment);

		DetailPackage detailPackage = new DetailPackage();
		detailPackage.setRentalPackageId(2L);
		when(detailPackageService.findById(1)).thenReturn(detailPackage);

		Job job = new Job();
		when(jobService.getJobById(1L)).thenReturn(job);

		paymentService.savePayment(vnpResponseCode, vnpOrderInfo);

		verify(jobService, times(1)).saveJob(any());
	}

	//when (payment.getBannerId() == 0 && payment.getDetailPackageId() == 0) ||
	// 	(payment.getBannerId() > 0 && payment.getDetailPackageId() > 0) == false
	@Test
	void givenVnpResponseCodeEquals00AndGetRentalPackageIdEquals2AndJobServiceGetJobByIdReturnNull_whenCallSavePayment_thenJobServiceSaveJobAreNotCalled() {
		String vnpOrderInfo = "recruiterId 1 detailPackageId 1 bannerId 1 amount 1 description abc orderType abc bankCode abc jobId 1";
		String vnpResponseCode = "00";
		Payment payment = new Payment();
		payment.setRecruiterId(1L);
		payment.setBannerId(0);
		payment.setDetailPackageId(1);
		payment.setJobId(1L);
		when(modelMapper.map(ArgumentMatchers.any(), any())).thenReturn(payment);

		Recruiter recruiter = new Recruiter();
		when(recruiterService.getRecruiterById(anyLong())).thenReturn(recruiter);

		when(paymentRepository.save(any())).thenReturn(payment);

		DetailPackage detailPackage = new DetailPackage();
		detailPackage.setRentalPackageId(2L);
		when(detailPackageService.findById(1)).thenReturn(detailPackage);

		Exception exception = assertThrows(Exception.class, () -> paymentService.savePayment(vnpResponseCode, vnpOrderInfo));

		verify(jobService, times(0)).saveJob(any());
	}

	@Test
	void givenVnpResponseCodeEquals07_whenCallSavePayment_thenThrowException() {
		String vnpOrderInfo = "recruiterId 1 detailPackageId 1 bannerId 1 amount 1 description abc orderType abc bankCode abc jobId 1";
		String vnpResponseCode = "07";
		Payment payment = new Payment();
		payment.setRecruiterId(1L);
		payment.setBannerId(0);
		payment.setDetailPackageId(0);
		when(modelMapper.map(ArgumentMatchers.any(), any())).thenReturn(payment);

		Recruiter recruiter = new Recruiter();
		when(recruiterService.getRecruiterById(anyLong())).thenReturn(recruiter);

		Exception exception = assertThrows(Exception.class, () -> paymentService.savePayment(vnpResponseCode, vnpOrderInfo));

		assertEquals(null, exception.getMessage());
	}

	@Test
	void givenVnpResponseCodeEquals09_whenCallSavePayment_thenThrowException() {
		String vnpOrderInfo = "recruiterId 1 detailPackageId 1 bannerId 1 amount 1 description abc orderType abc bankCode abc jobId 1";
		String vnpResponseCode = "09";
		Payment payment = new Payment();
		payment.setRecruiterId(1L);
		payment.setBannerId(0);
		payment.setDetailPackageId(0);
		when(modelMapper.map(ArgumentMatchers.any(), any())).thenReturn(payment);

		Recruiter recruiter = new Recruiter();
		when(recruiterService.getRecruiterById(anyLong())).thenReturn(recruiter);

		Exception exception = assertThrows(Exception.class, () -> paymentService.savePayment(vnpResponseCode, vnpOrderInfo));

		assertEquals(null, exception.getMessage());
	}

	@Test
	void givenVnpResponseCodeEquals10_whenCallSavePayment_thenThrowException() {
		String vnpOrderInfo = "recruiterId 1 detailPackageId 1 bannerId 1 amount 1 description abc orderType abc bankCode abc jobId 1";
		String vnpResponseCode = "10";
		Payment payment = new Payment();
		payment.setRecruiterId(1L);
		payment.setBannerId(0);
		payment.setDetailPackageId(0);
		when(modelMapper.map(ArgumentMatchers.any(), any())).thenReturn(payment);

		Recruiter recruiter = new Recruiter();
		when(recruiterService.getRecruiterById(anyLong())).thenReturn(recruiter);

		Exception exception = assertThrows(Exception.class, () -> paymentService.savePayment(vnpResponseCode, vnpOrderInfo));

		assertEquals(null, exception.getMessage());
	}

	@Test
	void givenVnpResponseCodeEquals11_whenCallSavePayment_thenThrowException() {
		String vnpOrderInfo = "recruiterId 1 detailPackageId 1 bannerId 1 amount 1 description abc orderType abc bankCode abc jobId 1";
		String vnpResponseCode = "11";
		Payment payment = new Payment();
		payment.setRecruiterId(1L);
		payment.setBannerId(0);
		payment.setDetailPackageId(0);
		when(modelMapper.map(ArgumentMatchers.any(), any())).thenReturn(payment);

		Recruiter recruiter = new Recruiter();
		when(recruiterService.getRecruiterById(anyLong())).thenReturn(recruiter);

		Exception exception = assertThrows(Exception.class, () -> paymentService.savePayment(vnpResponseCode, vnpOrderInfo));

		assertEquals(null, exception.getMessage());
	}

	@Test
	void givenVnpResponseCodeEquals12_whenCallSavePayment_thenThrowException() {
		String vnpOrderInfo = "recruiterId 1 detailPackageId 1 bannerId 1 amount 1 description abc orderType abc bankCode abc jobId 1";
		String vnpResponseCode = "12";
		Payment payment = new Payment();
		payment.setRecruiterId(1L);
		payment.setBannerId(0);
		payment.setDetailPackageId(0);
		when(modelMapper.map(ArgumentMatchers.any(), any())).thenReturn(payment);

		Recruiter recruiter = new Recruiter();
		when(recruiterService.getRecruiterById(anyLong())).thenReturn(recruiter);

		Exception exception = assertThrows(Exception.class, () -> paymentService.savePayment(vnpResponseCode, vnpOrderInfo));

		assertEquals(null, exception.getMessage());
	}

	@Test
	void givenVnpResponseCodeEquals13_whenCallSavePayment_thenThrowException() {
		String vnpOrderInfo = "recruiterId 1 detailPackageId 1 bannerId 1 amount 1 description abc orderType abc bankCode abc jobId 1";
		String vnpResponseCode = "13";
		Payment payment = new Payment();
		payment.setRecruiterId(1L);
		payment.setBannerId(0);
		payment.setDetailPackageId(0);
		when(modelMapper.map(ArgumentMatchers.any(), any())).thenReturn(payment);

		Recruiter recruiter = new Recruiter();
		when(recruiterService.getRecruiterById(anyLong())).thenReturn(recruiter);

		Exception exception = assertThrows(Exception.class, () -> paymentService.savePayment(vnpResponseCode, vnpOrderInfo));

		assertEquals(null, exception.getMessage());
	}

	@Test
	void givenVnpResponseCodeEquals24_whenCallSavePayment_thenThrowException() {
		String vnpOrderInfo = "recruiterId 1 detailPackageId 1 bannerId 1 amount 1 description abc orderType abc bankCode abc jobId 1";
		String vnpResponseCode = "24";
		Payment payment = new Payment();
		payment.setRecruiterId(1L);
		payment.setBannerId(0);
		payment.setDetailPackageId(0);
		when(modelMapper.map(ArgumentMatchers.any(), any())).thenReturn(payment);

		Recruiter recruiter = new Recruiter();
		when(recruiterService.getRecruiterById(anyLong())).thenReturn(recruiter);

		Exception exception = assertThrows(Exception.class, () -> paymentService.savePayment(vnpResponseCode, vnpOrderInfo));

		assertEquals(null, exception.getMessage());
	}

	@Test
	void givenVnpResponseCodeEquals51_whenCallSavePayment_thenThrowException() {
		String vnpOrderInfo = "recruiterId 1 detailPackageId 1 bannerId 1 amount 1 description abc orderType abc bankCode abc jobId 1";
		String vnpResponseCode = "51";
		Payment payment = new Payment();
		payment.setRecruiterId(1L);
		payment.setBannerId(0);
		payment.setDetailPackageId(0);
		when(modelMapper.map(ArgumentMatchers.any(), any())).thenReturn(payment);

		Recruiter recruiter = new Recruiter();
		when(recruiterService.getRecruiterById(anyLong())).thenReturn(recruiter);

		Exception exception = assertThrows(Exception.class, () -> paymentService.savePayment(vnpResponseCode, vnpOrderInfo));

		assertEquals(null, exception.getMessage());
	}

	@Test
	void givenVnpResponseCodeEquals65_whenCallSavePayment_thenThrowException() {
		String vnpOrderInfo = "recruiterId 1 detailPackageId 1 bannerId 1 amount 1 description abc orderType abc bankCode abc jobId 1";
		String vnpResponseCode = "65";
		Payment payment = new Payment();
		payment.setRecruiterId(1L);
		payment.setBannerId(0);
		payment.setDetailPackageId(0);
		when(modelMapper.map(ArgumentMatchers.any(), any())).thenReturn(payment);

		Recruiter recruiter = new Recruiter();
		when(recruiterService.getRecruiterById(anyLong())).thenReturn(recruiter);

		Exception exception = assertThrows(Exception.class, () -> paymentService.savePayment(vnpResponseCode, vnpOrderInfo));

		assertEquals(null, exception.getMessage());
	}

	@Test
	void givenVnpResponseCodeEquals79_whenCallSavePayment_thenThrowException() {
		String vnpOrderInfo = "recruiterId 1 detailPackageId 1 bannerId 1 amount 1 description abc orderType abc bankCode abc jobId 1";
		String vnpResponseCode = "79";
		Payment payment = new Payment();
		payment.setRecruiterId(1L);
		payment.setBannerId(0);
		payment.setDetailPackageId(0);
		when(modelMapper.map(ArgumentMatchers.any(), any())).thenReturn(payment);

		Recruiter recruiter = new Recruiter();
		when(recruiterService.getRecruiterById(anyLong())).thenReturn(recruiter);

		Exception exception = assertThrows(Exception.class, () -> paymentService.savePayment(vnpResponseCode, vnpOrderInfo));

		assertEquals(null,exception.getMessage());
	}

	@Test
	void givenVnpResponseCodeEquals99_whenCallSavePayment_thenThrowException() {
		String vnpOrderInfo = "recruiterId 1 detailPackageId 1 bannerId 1 amount 1 description abc orderType abc bankCode abc jobId 1";
		String vnpResponseCode = "99";
		Payment payment = new Payment();
		payment.setRecruiterId(1L);
		payment.setBannerId(0);
		payment.setDetailPackageId(0);
		when(modelMapper.map(ArgumentMatchers.any(), any())).thenReturn(payment);

		Recruiter recruiter = new Recruiter();
		when(recruiterService.getRecruiterById(anyLong())).thenReturn(recruiter);

		Exception exception = assertThrows(Exception.class, () -> paymentService.savePayment(vnpResponseCode, vnpOrderInfo));

		assertEquals(null, exception.getMessage());
	}

	//PayController line 64

	PaymentDTO handlerString(String vnpOrderInfo) {
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
			if (key.equals("amount")) paymentDTO.setAmount(Integer.parseInt(value));
			if (key.equals("description")) paymentDTO.setDescription(value);
			if (key.equals("orderType")) paymentDTO.setOrderType(value);
			if (key.equals("bankCode")) paymentDTO.setBankCode(value);
			if (key.equals("jobId")) paymentDTO.setJobId(Long.parseLong(value));
		});
		return paymentDTO;
	}

	//PayController line 48

	@Test
	void getPaymentVNPayTest() throws UnsupportedEncodingException {
		PaymentDTO paymentDTO = new PaymentDTO();
		paymentDTO.setAmount(1);
		paymentDTO.setRecruiterId(1);
		paymentDTO.setDetailPackageId(1);
		paymentDTO.setJobId(1);
		paymentDTO.setDescription("a");
		paymentDTO.setOrderType("a");

		Payment payment = new Payment();
		when(modelMapper.map(ArgumentMatchers.any(), any())).thenReturn(payment);

		PaymentResponseDTO paymentResponseDTO = paymentService.getPaymentVNPay(paymentDTO);

		assertEquals(false, paymentResponseDTO.getPaymentUrl().isEmpty());
	}

	@Test
	void whenModelMapperMapReturnNull_whenGetPaymentVNPayTest_thenThrowNull() throws UnsupportedEncodingException {
		PaymentDTO paymentDTO = new PaymentDTO();
		paymentDTO.setAmount(1);
		paymentDTO.setRecruiterId(1);
		paymentDTO.setDetailPackageId(1);
		paymentDTO.setJobId(1);
		paymentDTO.setDescription("a");
		paymentDTO.setOrderType("a");

		when(modelMapper.map(ArgumentMatchers.any(), any())).thenReturn(null);

		Exception exception = assertThrows(Exception.class, () -> paymentService.getPaymentVNPay(paymentDTO));

		assertEquals(null, exception.getMessage());
	}

	//PayController line 48

	//PayController line 141

	@Test
	void givenPageNoIsNull_whenCallGetRevenue_thenPaymentRepositoryGetRevenueInMonthAreNotCalled() {
		LocalDateTime start = LocalDateTime.of(2022, Month.AUGUST, 01, 12, 00, 00	);
		LocalDateTime end = LocalDateTime.of(2022, Month.AUGUST, 31, 12, 00, 00	);
		Integer pageNo = null;
		Integer pageSize = 1;

		Exception exception = assertThrows(Exception.class,()->paymentService.getRevenue(start,end,pageNo,pageSize));

		verify(paymentRepository,times(0)).getRevenueInMonth(any(),any(),any());
	}

	@Test
	void givenPaymentRepositoryGetRevenueReturnNull_whenCallGetRevenue_thenPaymentRepositoryGetRevenueInMonthAreNotCalled() {
		LocalDateTime start = LocalDateTime.of(2022, Month.AUGUST, 01, 12, 00, 00	);
		LocalDateTime end = LocalDateTime.of(2022, Month.AUGUST, 31, 12, 00, 00	);
		Integer pageNo = null;
		Integer pageSize = 1;

		Exception exception = assertThrows(Exception.class,()->paymentService.getRevenue(start,end,pageNo,pageSize));

		verify(modelMapper,times(0)).map(ArgumentMatchers.any(),any());
	}

	@Test
	void givenModelMapperMapReturnNull_whenCallGetRevenue_thenRecruiterServiceGetRecruiterByIdAreNotCalled() {
		LocalDateTime start = LocalDateTime.of(2022, Month.AUGUST, 01, 12, 00, 00	);
		LocalDateTime end = LocalDateTime.of(2022, Month.AUGUST, 31, 12, 00, 00	);
		Integer pageNo = 1;
		Integer pageSize = 1;
		int pageReq = pageNo >= 1 ? pageNo - 1 : pageNo;
		Pageable pageable = PageRequest.of(pageReq, pageSize);
		Payment payment = new Payment();
		Page<Payment> paymentPage = new PageImpl<>(Arrays.asList(payment));
		when(paymentRepository.getRevenueInMonth(start,end,pageable)).thenReturn(paymentPage);
		when(modelMapper.getTypeMap(ArgumentMatchers.any(),any())).thenReturn(null);

		Exception exception = assertThrows(Exception.class,()->paymentService.getRevenue(start,end,pageNo,pageSize));

		verify(recruiterService,times(0)).getRecruiterById(1);
	}

	@Test
	void givenModelMapperMapReturnNull_whenCallGetRevenue_thenRentalPackageServiceGetRentalPackageNameAreNotCalled() {
		LocalDateTime start = LocalDateTime.of(2022, Month.AUGUST, 01, 12, 00, 00	);
		LocalDateTime end = LocalDateTime.of(2022, Month.AUGUST, 31, 12, 00, 00	);
		Integer pageNo = 1;
		Integer pageSize = 1;
		int pageReq = pageNo >= 1 ? pageNo - 1 : pageNo;
		Pageable pageable = PageRequest.of(pageReq, pageSize);
		Payment payment = new Payment();
		Page<Payment> paymentPage = new PageImpl<>(Arrays.asList(payment));
		when(paymentRepository.getRevenueInMonth(start,end,pageable)).thenReturn(paymentPage);
		when(modelMapper.getTypeMap(ArgumentMatchers.any(),any())).thenReturn(null);

		Exception exception = assertThrows(Exception.class,()->paymentService.getRevenue(start,end,pageNo,pageSize));

		verify(rentalPackageService,times(0)).getRentalPackageName(1);
	}

	@Test
	void givenItemResponseGetBannerIdEquals0AndRentalPackageServiceGetRentalPackageReturnNull_whenGetRevenue_thenRecruiterServiceGetRecruiterByIdAreNotCalled(){
		LocalDateTime start = LocalDateTime.of(2022, Month.AUGUST, 01, 12, 00, 00	);
		LocalDateTime end = LocalDateTime.of(2022, Month.AUGUST, 31, 12, 00, 00	);
		Integer pageNo = 1;
		Integer pageSize = 1;
		int pageReq = pageNo >= 1 ? pageNo - 1 : pageNo;
		Pageable pageable = PageRequest.of(pageReq, pageSize);
		Payment payment = new Payment();
		Page<Payment> paymentPage = new PageImpl<>(Arrays.asList(payment));
		when(paymentRepository.getRevenueInMonth(start,end,pageable)).thenReturn(paymentPage);
		RevenueResponse revenueResponse = new RevenueResponse();
		revenueResponse.setBannerId(0);
		when(modelMapper.map(payment, RevenueResponse.class)).thenReturn(revenueResponse);
		when(rentalPackageService.getRentalPackageName(1)).thenReturn(null);
		Exception exception = assertThrows(Exception.class,()->paymentService.getRevenue(start,end,pageNo,pageSize));

		verify(recruiterService,times(0)).getRecruiterById(1);
	}

	@Test
	void givenAllParamIsCorrect_whenGetRevenue_thenResponseDataPaginationRevenueGetFirstItem(){
		LocalDateTime start = LocalDateTime.of(2022, Month.AUGUST, 01, 12, 00, 00	);
		LocalDateTime end = LocalDateTime.of(2022, Month.AUGUST, 31, 12, 00, 00	);
		Integer pageNo = 1;
		Integer pageSize = 1;
		int pageReq = pageNo >= 1 ? pageNo - 1 : pageNo;
		Pageable pageable = PageRequest.of(pageReq, pageSize);
		Payment payment = new Payment();
		Page<Payment> paymentPage = new PageImpl<>(Arrays.asList(payment));
		when(paymentRepository.getRevenueInMonth(start,end,pageable)).thenReturn(paymentPage);
		RevenueResponse revenueResponse = new RevenueResponse();
		revenueResponse.setBannerId(0);
		revenueResponse.setRecruiterId(1);
		when(modelMapper.map(payment, RevenueResponse.class)).thenReturn(revenueResponse);
		when(rentalPackageService.getRentalPackageName(1)).thenReturn("a");
		Recruiter recruiter = new Recruiter();
		recruiter.setFullName("a");
		when(recruiterService.getRecruiterById(1)).thenReturn(recruiter);

		ResponseDataPaginationRevenue responseDataPagination =  paymentService.getRevenue(start,end,pageNo,pageSize);

		List<RevenueResponse> revenueResponseList = (List<RevenueResponse>) responseDataPagination.getData();
		Pagination pagination = (Pagination) responseDataPagination.getPagination();

		assertEquals(0,revenueResponseList.get(0).getBannerId());

	}

	@Test
	void givenAllParamIsCorrect_whenGetRevenue_thenResponseDataPaginationRevenueSize(){
		LocalDateTime start = LocalDateTime.of(2022, Month.AUGUST, 01, 12, 00, 00	);
		LocalDateTime end = LocalDateTime.of(2022, Month.AUGUST, 31, 12, 00, 00	);
		Integer pageNo = 1;
		Integer pageSize = 1;
		int pageReq = pageNo >= 1 ? pageNo - 1 : pageNo;
		Pageable pageable = PageRequest.of(pageReq, pageSize);
		Payment payment = new Payment();
		Page<Payment> paymentPage = new PageImpl<>(Arrays.asList(payment));
		when(paymentRepository.getRevenueInMonth(start,end,pageable)).thenReturn(paymentPage);
		RevenueResponse revenueResponse = new RevenueResponse();
		revenueResponse.setBannerId(0);
		revenueResponse.setRecruiterId(1);
		when(modelMapper.map(payment, RevenueResponse.class)).thenReturn(revenueResponse);
		when(rentalPackageService.getRentalPackageName(1)).thenReturn("a");
		Recruiter recruiter = new Recruiter();
		recruiter.setFullName("a");
		when(recruiterService.getRecruiterById(1)).thenReturn(recruiter);

		ResponseDataPaginationRevenue responseDataPagination =  paymentService.getRevenue(start,end,pageNo,pageSize);

		List<RevenueResponse> revenueResponseList = (List<RevenueResponse>) responseDataPagination.getData();
			assertEquals(1,revenueResponseList.size());

	}

	@Test
	void givenAllParamIsCorrect_whenGetRevenue_thenResponseDataPaginationRevenueStatus(){
		LocalDateTime start = LocalDateTime.of(2022, Month.AUGUST, 01, 12, 00, 00	);
		LocalDateTime end = LocalDateTime.of(2022, Month.AUGUST, 31, 12, 00, 00	);
		Integer pageNo = 1;
		Integer pageSize = 1;
		int pageReq = pageNo >= 1 ? pageNo - 1 : pageNo;
		Pageable pageable = PageRequest.of(pageReq, pageSize);
		Payment payment = new Payment();
		Page<Payment> paymentPage = new PageImpl<>(Arrays.asList(payment));
		when(paymentRepository.getRevenueInMonth(start,end,pageable)).thenReturn(paymentPage);
		RevenueResponse revenueResponse = new RevenueResponse();
		revenueResponse.setBannerId(0);
		revenueResponse.setRecruiterId(1);
		when(modelMapper.map(payment, RevenueResponse.class)).thenReturn(revenueResponse);
		when(rentalPackageService.getRentalPackageName(1)).thenReturn("a");
		Recruiter recruiter = new Recruiter();
		recruiter.setFullName("a");
		when(recruiterService.getRecruiterById(1)).thenReturn(recruiter);

		ResponseDataPaginationRevenue responseDataPagination =  paymentService.getRevenue(start,end,pageNo,pageSize);

		assertEquals(Enums.ResponseStatus.SUCCESS.getStatus(),responseDataPagination.getStatus());

	}

	@Test
	void givenAllParamIsCorrect_whenGetRevenue_thenResponseDataPaginationRevenueToTalPage(){
		LocalDateTime start = LocalDateTime.of(2022, Month.AUGUST, 01, 12, 00, 00	);
		LocalDateTime end = LocalDateTime.of(2022, Month.AUGUST, 31, 12, 00, 00	);
		Integer pageNo = 1;
		Integer pageSize = 1;
		int pageReq = pageNo >= 1 ? pageNo - 1 : pageNo;
		Pageable pageable = PageRequest.of(pageReq, pageSize);
		Payment payment = new Payment();
		Page<Payment> paymentPage = new PageImpl<>(Arrays.asList(payment));
		when(paymentRepository.getRevenueInMonth(start,end,pageable)).thenReturn(paymentPage);
		RevenueResponse revenueResponse = new RevenueResponse();
		revenueResponse.setBannerId(0);
		revenueResponse.setRecruiterId(1);
		when(modelMapper.map(payment, RevenueResponse.class)).thenReturn(revenueResponse);
		when(rentalPackageService.getRentalPackageName(1)).thenReturn("a");
		Recruiter recruiter = new Recruiter();
		recruiter.setFullName("a");
		when(recruiterService.getRecruiterById(1)).thenReturn(recruiter);

		ResponseDataPaginationRevenue responseDataPagination =  paymentService.getRevenue(start,end,pageNo,pageSize);
		Pagination pagination = (Pagination) responseDataPagination.getPagination();

		assertEquals(1,pagination.getTotalPage());
	}

	@Test
	void givenAllParamIsCorrect_whenGetRevenue_thenResponseDataPaginationTotalRevenue(){
		LocalDateTime start = LocalDateTime.of(2022, Month.AUGUST, 01, 12, 00, 00	);
		LocalDateTime end = LocalDateTime.of(2022, Month.AUGUST, 31, 12, 00, 00	);
		Integer pageNo = 1;
		Integer pageSize = 1;
		int pageReq = pageNo >= 1 ? pageNo - 1 : pageNo;
		Pageable pageable = PageRequest.of(pageReq, pageSize);
		Payment payment = new Payment();
		Page<Payment> paymentPage = new PageImpl<>(Arrays.asList(payment));
		when(paymentRepository.getRevenueInMonth(start,end,pageable)).thenReturn(paymentPage);
		RevenueResponse revenueResponse = new RevenueResponse();
		revenueResponse.setBannerId(0);
		revenueResponse.setRecruiterId(1);
		when(modelMapper.map(payment, RevenueResponse.class)).thenReturn(revenueResponse);
		when(rentalPackageService.getRentalPackageName(1)).thenReturn("a");
		Recruiter recruiter = new Recruiter();
		recruiter.setFullName("a");
		when(recruiterService.getRecruiterById(1)).thenReturn(recruiter);

		ResponseDataPaginationRevenue responseDataPagination =  paymentService.getRevenue(start,end,pageNo,pageSize);
		Pagination pagination = (Pagination) responseDataPagination.getPagination();

		assertEquals(0,responseDataPagination.getTotalRevenue());
	}
	
	@Test
	public void testGetListPaymentFilter() {
		final Page<Payment> payments = new PageImpl<>(Arrays.asList(payment()));
		when(paymentRepository.getListPaymentFilter(any(Pageable.class), eq(1L), eq(1L), eq(1L),
				eq("transactionCode"), eq("orderType"))).thenReturn(payments);
		
		final ResponseDataPagination result = paymentService.getListPaymentFilter(1, 10, 1L, 1L, 1L,
				"transactionCode", "orderType");
	}

	@Test
	public void testGetPaymentBannerInUse() {
		final List<Payment> paymentList = Arrays.asList(payment());
		when(paymentRepository.findByBannerIdAndExpiredStatusIsFalse(1L)).thenReturn(paymentList);
		final List<Payment> result = paymentService.getPaymentBannerInUse(1L);
	}

	@Test
	public void testGetPaymentNormalPackageInUse() {
		final List<Payment> paymentList = Arrays.asList(payment());
		when(paymentRepository.findByDetailPackageIdAndExpiredStatusIsFalse(1L)).thenReturn(paymentList);
		final List<Payment> result = paymentService.getPaymentNormalPackageInUse(1L);
	}

	@Test
	void testFindByJobId() {
		final Optional<Payment> payment = Optional.of(payment());
		when(paymentRepository.findByJobId(1L)).thenReturn(payment);
		final Optional<Payment> result = paymentService.findByJobId(1L);
	}

}