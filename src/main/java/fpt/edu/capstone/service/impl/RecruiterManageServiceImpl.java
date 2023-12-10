package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.CV.CVProfileResponse;
import fpt.edu.capstone.dto.CV.FindCVResponse;
import fpt.edu.capstone.dto.CV.ViewCVWithPayResponse;
import fpt.edu.capstone.dto.CV.ViewCvResponse;
import fpt.edu.capstone.dto.admin.CommonRecruiterInformationResponse;
import fpt.edu.capstone.dto.banner.UploadBannerRequest;
import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.dto.company.CompanyImageResponse;
import fpt.edu.capstone.dto.company.CompanyInformationResponse;
import fpt.edu.capstone.dto.job.CvAppliedJobResponse;
import fpt.edu.capstone.dto.job.JobForRecruiterResponse;
import fpt.edu.capstone.dto.recruiter.*;
import fpt.edu.capstone.entity.*;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.repository.*;
import fpt.edu.capstone.service.*;
import fpt.edu.capstone.utils.*;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RecruiterManageServiceImpl implements RecruiterManageService {

    private final ModelMapper modelMapper;

    private final RecruiterRepository recruiterRepository;

    private final UserRepository userRepository;

    private final JobService jobService;

    private final AppliedJobService appliedJobService;

    private final RecruiterService recruiterService;

    private final UserService userService;

    private final ImageServiceImpl imageService;

    private final CompanyService companyService;

    private final CVService cvService;

    private final CandidateService candidateService;

    private final WorkExperienceService workExperienceService;

    private final EducationService educationService;

    private final PaymentService paymentService;

    private final BannerService bannerService;

    private final BannerActiveRepository bannerActiveRepository;

    private final DetailPackageService detailPackageService;

    private final RentalPackageService rentalPackageService;

    private final BannerActiveService bannerActiveService;

    private final FieldsService fieldsService;

    private final CountryService countryService;

    private final CertificateService certificateService;

    private final LanguageService languageService;

    private final MajorLevelService majorLevelService;

    private final OtherSkillService otherSkillService;

    private final ProfileViewerService profileViewerService;

    private final ProfileViewerRepository profileViewerRepository;

    private final CVRepository cvRepository;

    private final EducationRepository educationRepository;

    private final MajorService majorService;

    private final MajorRepository majorRepository;

    private final WorkExperienceRepository workExperienceRepository;

    @Override
    public CommonRecruiterInformationResponse getCommonInforOfRecruiter(long recruiterId) {
        CommonRecruiterInformationResponse response = new CommonRecruiterInformationResponse();
        Optional<Recruiter> optionalRecruiter = recruiterRepository.findById(recruiterId);
        if (!optionalRecruiter.isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.USER_DOES_NOT_EXIST);
        }
        String message = "";
        String uncheck = "";
        int step = 0;
        int totalStep = 4;
        Users user = userRepository.getById(optionalRecruiter.get().getUserId());
        if (user == null) {
            throw new HiveConnectException(ResponseMessageConstants.PLEASE_TRY_TO_CONTACT_ADMIN);
        }
        //verify step 1: email
        if (user.isVerifiedEmail()) {
            step++;
        } else {
            message = ResponseMessageConstants.EMAIL_HAS_BEEN_NOT_VERIFIED;
            uncheck = "email";
        }
        //verify step 2: company
        if (optionalRecruiter.get().getCompanyId() > 0) {
            step++;
        } else {
            message = ResponseMessageConstants.COMPANY_HAS_BEEN_NOT_VERIFIED;
            uncheck = "company";
        }
        //verify step 3: phone number
        if (user.isVerifiedPhone()) {
            step++;
        } else {
            message = ResponseMessageConstants.PHONE_NUMBER_HAS_BEEN_NOT_VERIFIED;
            uncheck = "phone";
        }
        Company company = companyService.getCompanyById(optionalRecruiter.get().getCompanyId());
        //verify step 4: business license
        if(company != null) {
            Recruiter companyCreator = recruiterService.getRecruiterById(company.getCreatorId());
            if (companyCreator.getBusinessLicenseApprovalStatus() != null
                    && companyCreator.getBusinessLicenseApprovalStatus().equals(Enums.ApprovalStatus.APPROVED.getStatus())) {
                step++;
            } else {
                message = ResponseMessageConstants.BUSINESS_LICENSE_HAS_NOT_BEEN_VERIFIED;
                uncheck = "license";
            }
        }
        if (step == totalStep) {
            message = ResponseMessageConstants.ACCOUNT_VERIFY_SUCCESSFULLY;
        }
        response.setUncheck(uncheck);
        response.setMessage(message);
        response.setRecruiterFullName(optionalRecruiter.get().getFullName());
        response.setVerifyStep(step + " / " + totalStep);

        CountTotalCreatedJobResponse countTotalCreatedJobResponse = jobService.countTotalCreatedJobOfRecruiter(recruiterId);
        long totalCreatedJob = 0;
        if (countTotalCreatedJobResponse != null) {
            totalCreatedJob = countTotalCreatedJobResponse.getTotalCreatedJob();
        }
        response.setTotalCreatedJob(totalCreatedJob);

        List<CountCandidateApplyPercentageResponse> countApplyCandidateEachJob = appliedJobService.countApplyPercentage(recruiterId);
        String result = "0";
        if (countApplyCandidateEachJob != null && !countApplyCandidateEachJob.isEmpty()) {
            long totalApply = 0L;
            long numberRecruits = 0L;
            for (CountCandidateApplyPercentageResponse countResponse : countApplyCandidateEachJob) {
                totalApply += countResponse.getTotalApplied();
                numberRecruits += countResponse.getNumberRecruits();
            }
            double applyPercentage = (double) totalApply / numberRecruits;
            DecimalFormat formatter = new DecimalFormat("##.##"); //
            formatter.setRoundingMode(RoundingMode.DOWN); // Towards zero
            result = formatter.format(applyPercentage);
        }
        response.setCandidateApplyPercentage(result);
        if(optionalRecruiter.get().getTotalCvView() == -1) {
            response.setTotalViewCV(0);
        } else {
            response.setTotalViewCV(optionalRecruiter.get().getTotalCvView());
        }
        return response;
    }

    public ResponseDataPagination getRecruitersOfCompany(Integer pageNo, Integer pageSize, long companyId, String fullName, String email, String phone) {
        List<RecruiterBaseOnCompanyResponse> responseList = new ArrayList<>();
        Page<Recruiter> recruiters = recruiterService.getRecruiterByCompanyId(pageNo, pageSize, companyId, fullName, email, phone);
        if (recruiters.hasContent()) {
            for (Recruiter recruiter : recruiters) {
                RecruiterBaseOnCompanyResponse response = new RecruiterBaseOnCompanyResponse();
                response.setRecruiterId(recruiter.getId());

                Users user = userService.getUserById(recruiter.getUserId());
                if (user == null) {
                    throw new HiveConnectException(ResponseMessageConstants.PLEASE_TRY_TO_CONTACT_ADMIN);
                }
                response.setUserName(user.getUsername());

                response.setAvatar(user.getAvatar());
                response.setFullName(recruiter.getFullName());
                response.setGender(recruiter.isGender());
                response.setPosition(recruiter.getPosition());
                response.setLinkedinAccount(recruiter.getLinkedinAccount());

                response.setEmail(user.getEmail());
                response.setPhone(user.getPhone());

                responseList.add(response);
            }
        }
        ResponseDataPagination responseDataPagination = new ResponseDataPagination();
        Pagination pagination = new Pagination();
        responseDataPagination.setData(responseList);
        pagination.setCurrentPage(pageNo);
        pagination.setPageSize(pageSize);
        pagination.setTotalPage(recruiters.getTotalPages());
        pagination.setTotalRecords(Integer.parseInt(String.valueOf(recruiters.getTotalElements())));
        responseDataPagination.setStatus(Enums.ResponseStatus.SUCCESS.getStatus());
        responseDataPagination.setPagination(pagination);
        return responseDataPagination;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public RecruiterProfileResponse updateRecruiterInformation(long recruiterId, RecruiterUpdateProfileRequest request) throws Exception {
        Recruiter recruiter = recruiterService.getById(recruiterId);
        if (recruiter == null) {
            throw new HiveConnectException(ResponseMessageConstants.USER_DOES_NOT_EXIST);
        }
        Users user = userRepository.getUserById(recruiter.getUserId());
        if (request == null) {
            return null;
        }
        if ((request.getFullName() == null || request.getFullName().trim().isEmpty())
                || (request.getPhone() == null || request.getPhone().trim().isEmpty())
                || (request.getPosition() == null || request.getPosition().trim().isEmpty())) {
            throw new HiveConnectException(ResponseMessageConstants.REQUIRE_INPUT_MANDATORY_FIELD);
        }
        if (request.getAvatarUrl() != null && request.getAvatarUrl().trim().isEmpty()) {
            recruiter.setAvatarUrl(request.getAvatarUrl());
            user.setAvatar(request.getAvatarUrl());
        }
        recruiter.setFullName(request.getFullName());
        recruiter.setPosition(request.getPhone());
        recruiter.setGender(request.isGender());
        recruiter.setPosition(request.getPosition());
        recruiter.setLinkedinAccount(request.getLinkedinAccount());
        recruiterRepository.save(recruiter);

        //recruiter tồn tại nhưng user không tồn tại (database lỗi)
        if (user == null) {
            throw new HiveConnectException(ResponseMessageConstants.PLEASE_TRY_TO_CONTACT_ADMIN);
        }
        if (userService.findByPhoneAndIdIsNotIn(request.getPhone(), recruiter.getUserId()) != null) {
            throw new HiveConnectException(ResponseMessageConstants.PHONE_NUMBER_IN_USE);
        } else {
            if (!request.getPhone().equals(user.getPhone())) {
                if (user.isVerifiedPhone()) {
                    throw new HiveConnectException(ResponseMessageConstants.PHONE_NUMBER_VERIFIED);
                }
                user.setPhone(request.getPhone());
            }
        }
        userRepository.save(user);
        RecruiterProfileResponse profileResponse = getRecruiterProfile(recruiter.getUserId());
        return profileResponse;
    }

    @Override
    public RecruiterProfileResponse getRecruiterProfile(long userId) {
        Users user = userService.getUserById(userId);
        //recruiter tồn tại nhưng user không tồn tại (database lỗi)
        if (user == null) {
            throw new HiveConnectException(ResponseMessageConstants.USER_DOES_NOT_EXIST);
        }
        RecruiterProfileResponse profileResponse = new RecruiterProfileResponse();
        Recruiter recruiter = recruiterService.getRecruiterByUserId(userId);
        if (recruiter == null) {
            throw new HiveConnectException(ResponseMessageConstants.USER_DOES_NOT_EXIST);
        }
        profileResponse.setRecruiterId(recruiter.getId());
        profileResponse.setUserName(user.getUsername());
        profileResponse.setEmail(user.getEmail());
        profileResponse.setPhone(user.getPhone());
        profileResponse.setTotalCvView(recruiter.getTotalCvView());

        profileResponse.setAvatarUrl(recruiter.getAvatarUrl());

        Company company = companyService.getCompanyById(recruiter.getCompanyId());
        if (company != null) {
            profileResponse.setCompanyId(company.getId());
            profileResponse.setCompanyName(company.getName());
            profileResponse.setCompanyAddress(company.getAddress());
        }

        profileResponse.setFullName(recruiter.getFullName());
        profileResponse.setGender(recruiter.isGender());
        profileResponse.setPosition(recruiter.getPosition());
        profileResponse.setLinkedinAccount(recruiter.getLinkedinAccount());
        profileResponse.setVerifiedPhone(user.isVerifiedPhone());
        if (recruiter.getCompanyId() == 0) {
            profileResponse.setJoinedCompany(false);
        } else if (recruiter.getCompanyId() > 0) {
            profileResponse.setJoinedCompany(true);
        }
        if (recruiter.getBusinessLicenseApprovalStatus() != null
                && recruiter.getBusinessLicenseApprovalStatus().equals(Enums.ApprovalStatus.APPROVED.getStatus())) {
            profileResponse.setApprovedBusinessLicense(true);
        } else {
            profileResponse.setApprovedBusinessLicense(false);
        }
        return profileResponse;
    }

    @Override
    public CompanyInformationResponse getCompanyInformation(long companyId, long recruiterId) {
        CompanyInformationResponse response = new CompanyInformationResponse();
        Optional<Company> companyOp = companyService.findById(companyId);
        if (!companyOp.isPresent()) {
            throw new HiveConnectException("Thông tin công ty không tồn tại");
        }
        Company tmp = companyOp.get();
        response.setAddress(tmp.getAddress());
        Optional<Image> image = imageService.getImageCompany(companyId, true);
        if (image.isPresent()) {
            response.setAvatar(image.get().getUrl());
        }
        response.setDescription(tmp.getDescription());
        response.setEmail(tmp.getEmail());
        response.setFieldWork(tmp.getFieldWork());
        response.setId(tmp.getId());
        response.setName(tmp.getName());
        response.setMapUrl(tmp.getMapUrl());
        response.setPhone(tmp.getPhone());
        response.setNumberEmployees(tmp.getNumberEmployees());
        response.setWebsite(tmp.getWebsite());
        response.setTaxCode(tmp.getTaxCode());
        long creatorId = tmp.getCreatorId();
        boolean isCreator = false;
        if (recruiterId != 0 && creatorId == recruiterId) {
            isCreator = true;
        }
        response.setCreator(isCreator);
        List<Image> companyImageList = imageService.getCompanyImageList(companyId, false, false);
        List<CompanyImageResponse> companyImageUrls = companyImageList
                .stream().map(i -> modelMapper.map(i, CompanyImageResponse.class))
                .collect(Collectors.toList());
        response.setCompanyImageUrlList(companyImageUrls);
        List<Image> coverImage = imageService.getCompanyImageList(companyId, false, true);
        if (coverImage != null && !coverImage.isEmpty()) {
            response.setCoverImageUrl(coverImage.get(0).getUrl());
        }
        return response;
    }

    @Override
    public void uploadBanner(long recruiterId, UploadBannerRequest request) {
        if (recruiterService.getRecruiterById(recruiterId) == null) {
            throw new HiveConnectException(ResponseMessageConstants.USER_DOES_NOT_EXIST);
        }
        Payment payment = paymentService.findByIdAndRecruiterId(request.getPaymentId(), recruiterId);
        if (payment == null) {
            throw new HiveConnectException(ResponseMessageConstants.PAYMENT_DOES_NOT_EXIST);
        }
        //xem gói mà nhà tuyển dụng mua có những tính năng nào
        Banner banner = bannerService.findById(payment.getBannerId());
        if (banner.isSpotlight() && request.getSpotLightImage() != null) {
            BannerActive bannerActive = bannerActiveService.findByPaymentIdAndPosition(request.getPaymentId(),
                    Enums.BannerPosition.SPOTLIGHT.getStatus());
            setupBanner(payment, bannerActive, Enums.BannerPosition.SPOTLIGHT.getStatus(), request.getSpotLightImage());
        }
        if (banner.isHomepageBannerA() && request.getHomepageBannerAImage() != null) {
            BannerActive bannerActive = bannerActiveService.findByPaymentIdAndPosition(request.getPaymentId(),
                    Enums.BannerPosition.HOME_BANNER_A.getStatus());
            setupBanner(payment, bannerActive, Enums.BannerPosition.HOME_BANNER_A.getStatus(), request.getHomepageBannerAImage());
        }
        if (banner.isHomepageBannerB() && request.getHomepageBannerBImage() != null) {
            BannerActive bannerActive = bannerActiveService.findByPaymentIdAndPosition(request.getPaymentId(),
                    Enums.BannerPosition.HOME_BANNER_B.getStatus());
            setupBanner(payment, bannerActive, Enums.BannerPosition.HOME_BANNER_B.getStatus(), request.getHomepageBannerBImage());
        }
        if (banner.isHomepageBannerC() && request.getHomepageBannerCImage() != null) {
            BannerActive bannerActive = bannerActiveService.findByPaymentIdAndPosition(request.getPaymentId(),
                    Enums.BannerPosition.HOME_BANNER_C.getStatus());
            setupBanner(payment, bannerActive, Enums.BannerPosition.HOME_BANNER_C.getStatus(), request.getHomepageBannerCImage());
        }
        if (banner.isJobBannerA() && request.getJobBannerAImage() != null) {
            BannerActive bannerActive = bannerActiveService.findByPaymentIdAndPosition(request.getPaymentId(),
                    Enums.BannerPosition.JOB_BANNER_A.getStatus());
            setupBanner(payment, bannerActive, Enums.BannerPosition.JOB_BANNER_A.getStatus(), request.getJobBannerAImage());
        }
        if (banner.isJobBannerB() && request.getJobBannerBImage() != null) {
            BannerActive bannerActive = bannerActiveService.findByPaymentIdAndPosition(request.getPaymentId(),
                    Enums.BannerPosition.JOB_BANNER_B.getStatus());
            setupBanner(payment, bannerActive, Enums.BannerPosition.JOB_BANNER_B.getStatus(), request.getJobBannerBImage());
        }
        if (banner.isJobBannerC() && request.getJobBannerCImage() != null) {
            BannerActive bannerActive = bannerActiveService.findByPaymentIdAndPosition(request.getPaymentId(),
                    Enums.BannerPosition.JOB_BANNER_C.getStatus());
            setupBanner(payment, bannerActive, Enums.BannerPosition.JOB_BANNER_C.getStatus(), request.getJobBannerCImage());
        }
    }

    public void setupBanner(Payment payment, BannerActive bannerActive, String bannerDisplayPosition, String item) {
        if (bannerActive != null && bannerActive.getApprovalStatus().equals(Enums.ApprovalStatus.APPROVED.getStatus())) {
            //can not change banner image if admin approved
            return;
        } else if (bannerActive != null
                && (bannerActive.getApprovalStatus().equals(Enums.ApprovalStatus.PENDING.getStatus())
                || bannerActive.getApprovalStatus() == null)) {
            bannerActive.setImageUrl(item);
            bannerActive.update();
            bannerActiveRepository.save(bannerActive);
        } else {
            BannerActive newBannerActive = new BannerActive();
            newBannerActive.setImageUrl(item);
            newBannerActive.setPaymentId(payment.getId());
            newBannerActive.setApprovalStatus(Enums.ApprovalStatus.PENDING.getStatus());
            newBannerActive.setDisplayPosition(bannerDisplayPosition);
            newBannerActive.create();
            bannerActiveRepository.save(newBannerActive);
        }
    }

    @Override
    public ResponseDataPagination getJobOfRecruiter(Integer pageNo, Integer pageSize, long recruiterId, String jobName) {
        List<JobForRecruiterResponse> responseList = new ArrayList<>();

        int pageReq = pageNo >= 1 ? pageNo - 1 : pageNo;
        Pageable pageable = PageRequest.of(pageReq, pageSize);

        if (!recruiterService.existById(recruiterId)) {
            throw new HiveConnectException(ResponseMessageConstants.USER_DOES_NOT_EXIST);
        }
        Page<Job> jobsListOfRecruiter = jobService.getJobOfRecruiter(pageable, recruiterId, jobName);
        if (jobsListOfRecruiter.hasContent()) {
            for (Job job : jobsListOfRecruiter) {
                    JobForRecruiterResponse response = new JobForRecruiterResponse();
                    response.setJobId(job.getId());
                    response.setJobName(job.getJobName());
                    Company company = companyService.getCompanyById(job.getCompanyId());
                    if (company == null) {
                        //company không tồn tại mà đã tạo được job
                        throw new HiveConnectException(ResponseMessageConstants.PLEASE_TRY_TO_CONTACT_ADMIN);
                    }
                    response.setNumberRecruits(job.getNumberRecruits());
                    response.setCompanyName(company.getName());
                    response.setWorkPlace(job.getWorkPlace());
                    response.setFromSalary(job.getFromSalary());
                    response.setToSalary(job.getToSalary());

                    int applyCount = appliedJobService.countAppliedCVOfJob(job.getId());
                    response.setApplyCount(applyCount);
                    response.setStartDate(job.getStartDate());
                    response.setEndDate(job.getEndDate());
                    response.setFlag(job.getFlag());
                    responseList.add(response);
            }
        }
        ResponseDataPagination responseDataPagination = new ResponseDataPagination();
        Pagination pagination = new Pagination();
        responseDataPagination.setData(responseList);
        pagination.setCurrentPage(pageNo);
        pagination.setPageSize(pageSize);
        pagination.setTotalPage(jobsListOfRecruiter.getTotalPages());
        pagination.setTotalRecords(Integer.parseInt(String.valueOf(jobsListOfRecruiter.getTotalElements())));
        responseDataPagination.setStatus(Enums.ResponseStatus.SUCCESS.getStatus());
        responseDataPagination.setPagination(pagination);
        return responseDataPagination;
    }

    @Override
    public ResponseDataPagination findCVFilter(Integer pageNo, Integer pageSize, String experience, String candidateAddress, String techStack) {
        int pageReq = pageNo >= 1 ? pageNo - 1 : pageNo;
        Pageable pageable = PageRequest.of(pageReq, pageSize);
        Page<CV> cvPage = cvService.findCVFilter(pageable, experience, candidateAddress, techStack);

        List<FindCVResponse> findCVResponseList = cvPage.stream().
                map(cv -> modelMapper.map(cv, FindCVResponse.class)).collect(Collectors.toList());
        for (FindCVResponse cv : findCVResponseList) {
            Candidate candidate = candidateService.getCandidateById(cv.getCandidateId());
            Users users = userService.getUserById(candidate.getUserId());
            List<String> techstack = majorService.getMajorNameByCVId(cv.getId());
            String name = candidate.getFullName();
            if (name != null) {
                cv.setFullName(name);
            } else {
                cv.setFullName(users.getUsername());
            }
            cv.setAvatarUrl(users.getAvatar());
            cv.setAddress(candidate.getAddress());
            cv.setTechStack(techstack);
        }

        ResponseDataPagination responseDataPagination = new ResponseDataPagination();
        Pagination pagination = new Pagination();
        responseDataPagination.setData(findCVResponseList);
        pagination.setCurrentPage(pageNo);
        pagination.setPageSize(pageSize);
        pagination.setTotalPage(cvPage.getTotalPages());
        pagination.setTotalRecords(Integer.parseInt(String.valueOf(cvPage.getTotalElements())));
        responseDataPagination.setStatus(Enums.ResponseStatus.SUCCESS.getStatus());
        responseDataPagination.setPagination(pagination);
        return responseDataPagination;
    }

    @Override
    public DetailPurchasedPackageResponse getDetailPurchasedPackage(long recruiterId, long paymentId) {
        DetailPurchasedPackageResponse response = new DetailPurchasedPackageResponse();
        Payment payment = paymentService.findById(paymentId);
        response.setInfoPPRes(getDetailPaymentPackageInfo(payment));
        //lấy chi tiết recruiter đã upload cái gì cho gói này
        response.setBanner(getDetailPaymentBanner(payment));
        response.setJobPurchasedPPRes(getDetailPaymentJob(payment));
        return response;
    }

    private JobDetailPurchasedResponse getDetailPaymentJob(Payment payment) {
        if (payment.getDetailPackageId() > 0) {
            if (payment.getJobId() > 0) {
                Job selectedJob = jobService.getJobById(payment.getJobId());
                JobDetailPurchasedResponse jobDetailPurchasedRes = modelMapper
                        .map(selectedJob, JobDetailPurchasedResponse.class);
                Optional<Company> company = companyService.findById(selectedJob.getCompanyId());
                jobDetailPurchasedRes.setCompanyName(company.get().getName());
                Fields field = fieldsService.getById(selectedJob.getFieldId());
                jobDetailPurchasedRes.setFieldName(field.getFieldName());
                Optional<VietnamCountry> country = countryService.findById(selectedJob.getCountryId());
                jobDetailPurchasedRes.setCountry(country.get().getCountryName());

                return jobDetailPurchasedRes;
            }
        }
        return null;
    }

    private Map<String, BannerPositionDetailResponse> getDetailPaymentBanner(Payment payment) {
        if (payment.getBannerId() > 0) {
            List<BannerPositionDetailResponse> bannerActiveOfPayment = bannerActiveService
                    .getAllBannerByPaymentId(payment.getId());
            if (bannerActiveOfPayment != null && !bannerActiveOfPayment.isEmpty()) {
                Map<String, BannerPositionDetailResponse> bannerResponse = new HashMap<>();
                for (BannerPositionDetailResponse detail : bannerActiveOfPayment) {
                    bannerResponse.put(detail.getDisplayPosition(), detail);
                }
                return bannerResponse;
            }
        }
        return null;
    }

    private DetailPaymentPackageInfoResponse getDetailPaymentPackageInfo(Payment payment) {

        if (payment.getBannerId() <= 0 && payment.getDetailPackageId() <= 0) {
            throw new HiveConnectException(ResponseMessageConstants.DETAIL_PAYMENT_NOT_FOUND + ". " + ResponseMessageConstants.PLEASE_TRY_TO_CONTACT_ADMIN);
        }
        //lấy thông tin của gói mua
        DetailPaymentPackageInfoResponse detailPPIInfoRes = new DetailPaymentPackageInfoResponse();
        if (payment.getBannerId() > 0) {
            Banner banner = bannerService.findById(payment.getBannerId());
            detailPPIInfoRes.setBannerPaymentPackage(banner);
        }
        if (payment.getDetailPackageId() > 0) {
            NormalPaymentPackage normalPP = new NormalPaymentPackage();
            DetailPackage detailPackage = detailPackageService.findById(payment.getDetailPackageId());
            RentalPackage rentalPackage = rentalPackageService.findById(detailPackage.getRentalPackageId())
                    .orElseThrow(() -> new HiveConnectException(
                            ResponseMessageConstants.RENTAL_PACKAGE_DOES_NOT_EXIST + ". " + ResponseMessageConstants.PLEASE_TRY_TO_CONTACT_ADMIN));
            normalPP.setGroupPackageId(detailPackage.getRentalPackageId());
            normalPP.setGroupPackageName(rentalPackage.getPackageGroup());
            normalPP.setDetailPackageName(detailPackage.getDetailName());
            normalPP.setPrice(detailPackage.getPrice());
            normalPP.setDiscountPrice(detailPackage.getDiscount());
            normalPP.setTimeExpired(detailPackage.getTimeExpired());
            normalPP.setRelatedJob(detailPackage.isRelatedJob());
            normalPP.setSuggestJob(detailPackage.isSuggestJob());
            normalPP.setMaxCvView(detailPackage.getMaxCvView());
            detailPPIInfoRes.setNormalPaymentPackage(normalPP);
        }
        return detailPPIInfoRes;
    }


    @Override
    public ViewCVWithPayResponse getCvWithPay(long recruiterId, long cvId) {
        ViewCVWithPayResponse viewCVWithPayResponse = new ViewCVWithPayResponse();
        Optional<Recruiter> r = recruiterService.findById(recruiterId);
        if (!r.isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.RECRUITER_DOES_NOT_EXIST);
        }
        Recruiter recruiter = r.get();
        Optional<CV> cv = cvService.findCvById(cvId);
        if (!cv.isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.CV_NOT_EXIST);
        }
        Optional<Candidate> c = candidateService.findById(cv.get().getCandidateId());
        if (!c.isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.CANDIDATE_DOES_NOT_EXIST);
        }
        CVProfileResponse cvProfileResponse = new CVProfileResponse();
        List<Certificate> certificates = certificateService.getListCertificateByCvId(cv.get().getId());
        List<Education> educations = educationService.getListEducationByCvId(cv.get().getId());
        List<Language> languages = languageService.getListLanguageByCvId(cv.get().getId());
        List<MajorLevel> majorLevels = majorLevelService.getListMajorLevelByCvId(cv.get().getId());
        for (MajorLevel ml : majorLevels){
            Major majorName = majorRepository.findById(ml.getMajorId()).get();
            cvProfileResponse.setMajorName(majorName.getMajorName());
        }
        List<OtherSkill> otherSkills = otherSkillService.getListOtherSkillByCvId(cv.get().getId());
        List<WorkExperience> workExperiences = workExperienceService.getListWorkExperienceByCvId(cv.get().getId());
        cvProfileResponse.setCandidateId(cv.get().getCandidateId());
        cvProfileResponse.setCertificates(certificates);
        cvProfileResponse.setEducations(educations);
        cvProfileResponse.setLanguages(languages);
        cvProfileResponse.setSummary(cv.get().getSummary());
        cvProfileResponse.setMajorLevels(majorLevels);
        cvProfileResponse.setOtherSkills(otherSkills);
        cvProfileResponse.setWorkExperiences(workExperiences);


        Candidate candidate = c.get();
        cvProfileResponse.setCandidateId(candidate.getId());
        cvProfileResponse.setGender(candidate.isGender());
        cvProfileResponse.setBirthDate(candidate.getBirthDate());
        cvProfileResponse.setCountry(candidate.getCountry());
        cvProfileResponse.setFullName(candidate.getFullName());
        cvProfileResponse.setAddress(candidate.getAddress());
        cvProfileResponse.setSocialLink(candidate.getSocialLink());

        cvProfileResponse.setExperienceLevel(candidate.getExperienceLevel());
        cvProfileResponse.setIntroduction(candidate.getIntroduction());

        Optional<Users> u = userService.findByIdOp(candidate.getUserId());
        Users users = u.get();
        String phoneNumber = users.getPhone();
        cvProfileResponse.setAvatarUrl(users.getAvatar());
        String email = users.getEmail();
        String message = "";

        Optional<ProfileViewer> profileViewer = profileViewerService.getByCvIdAndViewerIdOptional(cv.get().getId(), recruiter.getId());
        if (profileViewer.isPresent()) {
            cvProfileResponse.setEmail(email);
            cvProfileResponse.setPhoneNumber(phoneNumber);
            message = ResponseMessageConstants.VIEW_CV_ALL_INFORMATION;
        } else if (recruiter.getTotalCvView() > 0) {
            cvProfileResponse.setEmail(email);
            cvProfileResponse.setPhoneNumber(phoneNumber);
            recruiterService.updateTotalCvView(recruiter.getTotalCvView() - 1, recruiter.getId());
            //Tieu mai them : insert profileviewed
            //Tieu mai them
            ViewCvResponse viewCvResponse = new ViewCvResponse();
            viewCvResponse.setCandidateId(candidate.getId());
            viewCvResponse.setCvId(cvId);
            viewCvResponse.setViewerId(recruiterId);
            insertWhoViewCv(viewCvResponse);
            message = ResponseMessageConstants.VIEW_CV_ALL_INFORMATION;
        } else if (recruiter.getTotalCvView() == 0) {
            cvProfileResponse.setEmail("*****@gmail.com");
            cvProfileResponse.setPhoneNumber("+84**********");
            cvProfileResponse.setSocialLink("https://******/******");
            message = ResponseMessageConstants.TOTAL_VIEW_CV_RUN_OUT;
        } else {
            cvProfileResponse.setEmail("*****@gmail.com");
            cvProfileResponse.setPhoneNumber("+84**********");
            cvProfileResponse.setSocialLink("https://******/******");
            message = ResponseMessageConstants.PAY_FOR_PACKAGE_TO_VIEW_CV;
        }
        viewCVWithPayResponse.setMessage(message);
        viewCVWithPayResponse.setCvProfileResponse(cvProfileResponse);
        return viewCVWithPayResponse;
    }

    @Override
    public ViewCVWithPayResponse previewCV(long recruiterId, long cvId) {
        ViewCVWithPayResponse viewCVWithPayResponse = new ViewCVWithPayResponse();
        Optional<Recruiter> r = recruiterService.findById(recruiterId);
        if (!r.isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.RECRUITER_DOES_NOT_EXIST);
        }
        Recruiter recruiter = r.get();
        Optional<CV> cv = cvService.findCvById(cvId);
        if (!cv.isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.CV_NOT_EXIST);
        }
        Optional<Candidate> c = candidateService.findById(cv.get().getCandidateId());
        if (!c.isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.CANDIDATE_DOES_NOT_EXIST);
        }
        CVProfileResponse cvProfileResponse = new CVProfileResponse();
        List<Certificate> certificates = certificateService.getListCertificateByCvId(cv.get().getId());
        List<Education> educations = educationService.getListEducationByCvId(cv.get().getId());
        List<Language> languages = languageService.getListLanguageByCvId(cv.get().getId());
        List<MajorLevel> majorLevels = majorLevelService.getListMajorLevelByCvId(cv.get().getId());
        List<OtherSkill> otherSkills = otherSkillService.getListOtherSkillByCvId(cv.get().getId());
        List<WorkExperience> workExperiences = workExperienceService.getListWorkExperienceByCvId(cv.get().getId());
        cvProfileResponse.setCandidateId(cv.get().getCandidateId());
        cvProfileResponse.setCertificates(certificates);
        cvProfileResponse.setEducations(educations);
        cvProfileResponse.setLanguages(languages);
        cvProfileResponse.setSummary(cv.get().getSummary());
        cvProfileResponse.setMajorLevels(majorLevels);
        cvProfileResponse.setOtherSkills(otherSkills);
        cvProfileResponse.setWorkExperiences(workExperiences);


        Candidate candidate = c.get();
        cvProfileResponse.setCandidateId(candidate.getId());
        cvProfileResponse.setGender(candidate.isGender());
        cvProfileResponse.setBirthDate(candidate.getBirthDate());
        cvProfileResponse.setCountry(candidate.getCountry());
        cvProfileResponse.setFullName(candidate.getFullName());
        cvProfileResponse.setAddress(candidate.getAddress());
        cvProfileResponse.setSocialLink(candidate.getSocialLink());

        cvProfileResponse.setExperienceLevel(candidate.getExperienceLevel());
        cvProfileResponse.setIntroduction(candidate.getIntroduction());

        Optional<Users> u = userService.findByIdOp(candidate.getUserId());
        Users users = u.get();
        String phoneNumber = users.getPhone();
        cvProfileResponse.setAvatarUrl(users.getAvatar());
        String email = users.getEmail();
        String message = "";
        cvProfileResponse.setEmail("*****@gmail.com");
        cvProfileResponse.setPhoneNumber("+84**********");
        cvProfileResponse.setSocialLink("https://******/******");
        viewCVWithPayResponse.setCvProfileResponse(cvProfileResponse);
        return viewCVWithPayResponse;
    }

    @Override
    public void insertWhoViewCv(ViewCvResponse response) {
        Optional<CV> cv = cvService.findByIdAndCandidateId(response.getCvId(), response.getCandidateId());
        if (!cv.isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.CV_NOT_EXIST);
        }
        Recruiter recruiter = recruiterService.getRecruiterById(response.getViewerId());
        if (recruiter == null) {
            throw new HiveConnectException(ResponseMessageConstants.USER_DOES_NOT_EXIST);
        }
        ProfileViewer profileViewer = profileViewerService.getByCvIdAndViewerId(response.getCvId(), response.getViewerId());
        if (profileViewer == null) {
            ProfileViewer saveProfileViewer = new ProfileViewer();
            saveProfileViewer.setViewerId(response.getViewerId());
            saveProfileViewer.setCvId(response.getCvId());
            saveProfileViewer.setCandidateId(response.getCandidateId());
            saveProfileViewer.setSave(false);
            saveProfileViewer.create();
            profileViewerRepository.save(saveProfileViewer);
        }
    }

    @Override
    public ResponseDataPagination getCvListAppliedJob(Integer pageNo, Integer pageSize, long jobId) {
        List<CvAppliedJobResponse> responseList = new ArrayList<>();

        int pageReq = pageNo >= 1 ? pageNo - 1 : pageNo;
        Pageable pageable = PageRequest.of(pageReq, pageSize);

        Optional<Job> job = jobService.findById(jobId);
        if (!job.isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.JOB_DOES_NOT_EXIST);
        }
        Page<AppliedJob> appliedJobs = appliedJobService.getCvAppliedJob(pageable, jobId, true);
        if (appliedJobs.isEmpty()) {
            throw new HiveConnectException(ResponseMessageConstants.JOB_DOES_NOT_HAVE_CV_APPLY_FOR);
        }
        if (appliedJobs.hasContent()) {
            for (AppliedJob appliedJob : appliedJobs) {
                CvAppliedJobResponse responseObj = new CvAppliedJobResponse();
                responseObj.setJobId(jobId);
                Candidate candidate = candidateService.getCandidateById(appliedJob.getCandidateId());
                responseObj.setCandidateId(appliedJob.getCandidateId());
                responseObj.setCandidateName(candidate.getFullName());
                responseObj.setAvatar(candidate.getAvatarUrl());
                if (appliedJob.isUploadCv()) {
                    //upload CV
                    responseObj.setCvUrl("************");
                }
                CV cv = cvRepository.getByCandidateId(appliedJob.getCandidateId());
                if (cv == null) {
                    if (!appliedJob.isUploadCv()) {
                        //Profile không tồn tại mà cũng không upload CV
                        throw new HiveConnectException(ResponseMessageConstants.PLEASE_TRY_TO_CONTACT_ADMIN);
                    }
                }
                List<WorkExperience> workExperiencesOfCv = workExperienceRepository.getListWorkExperienceByCvId(cv.getId());
                if (!workExperiencesOfCv.isEmpty()) {
                    List<String> experienceDesc = new ArrayList<>();
                    for (WorkExperience workExperience : workExperiencesOfCv) {
                        String workEx = workExperience.getCompanyName() + " - " + workExperience.getPosition();
                        experienceDesc.add(workEx);
                    }
                    responseObj.setExperienceDesc(experienceDesc);
                }
                List<Education> educations = educationRepository.getListEducationByCvId(cv.getId());
                if (!educations.isEmpty()) {
                    List<String> schools = educations.stream().map(Education::getSchool).collect(Collectors.toList());
                    responseObj.setEducations(schools);
                }
                responseObj.setCareerGoal(candidate.getIntroduction());
                responseObj.setAddress(candidate.getAddress());
                responseObj.setApprovalStatus(appliedJob.getApprovalStatus());
                responseObj.setCvId(cv.getId());
                responseList.add(responseObj);
            }
        }
        ResponseDataPagination responseDataPagination = new ResponseDataPagination();
        Pagination pagination = new Pagination();
        responseDataPagination.setData(responseList);
        pagination.setCurrentPage(pageNo);
        pagination.setPageSize(pageSize);
        pagination.setTotalPage(appliedJobs.getTotalPages());
        pagination.setTotalRecords(Integer.parseInt(String.valueOf(appliedJobs.getTotalElements())));
        responseDataPagination.setStatus(Enums.ResponseStatus.SUCCESS.getStatus());
        responseDataPagination.setPagination(pagination);
        return responseDataPagination;
    }
}
