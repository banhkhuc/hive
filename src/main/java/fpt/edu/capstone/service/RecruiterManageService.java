package fpt.edu.capstone.service;

import fpt.edu.capstone.dto.CV.ViewCVWithPayResponse;
import fpt.edu.capstone.dto.CV.ViewCvResponse;
import fpt.edu.capstone.dto.admin.CommonRecruiterInformationResponse;
import fpt.edu.capstone.dto.banner.UploadBannerRequest;
import fpt.edu.capstone.dto.company.CompanyInformationResponse;
import fpt.edu.capstone.dto.recruiter.DetailPurchasedPackageResponse;
import fpt.edu.capstone.dto.recruiter.RecruiterProfileResponse;
import fpt.edu.capstone.dto.recruiter.RecruiterUpdateProfileRequest;
import fpt.edu.capstone.utils.ResponseDataPagination;


public interface RecruiterManageService {

    CommonRecruiterInformationResponse getCommonInforOfRecruiter(long recruiterId);

    ResponseDataPagination getRecruitersOfCompany(Integer pageNo, Integer pageSize, long companyId, String fullName, String email, String phone);

    RecruiterProfileResponse updateRecruiterInformation(long recruiterId, RecruiterUpdateProfileRequest request) throws Exception;

    RecruiterProfileResponse getRecruiterProfile(long userId);

    CompanyInformationResponse getCompanyInformation(long companyId, long recruiterId);

    void uploadBanner(long recruiterId, UploadBannerRequest request);

    DetailPurchasedPackageResponse getDetailPurchasedPackage(long recruiterId, long paymentId);

    ResponseDataPagination getJobOfRecruiter(Integer pageNo, Integer pageSize, long recruiterId, String jobName);

    ResponseDataPagination findCVFilter(Integer pageNo, Integer pageSize, String experience, String candidateAddress, String techStack);

    ViewCVWithPayResponse getCvWithPay(long recruiterId, long cvId);

    ViewCVWithPayResponse previewCV(long recruiterId, long cvId);

    void insertWhoViewCv(ViewCvResponse response);

    ResponseDataPagination getCvListAppliedJob(Integer pageNo, Integer pageSize, long jobId);

}
