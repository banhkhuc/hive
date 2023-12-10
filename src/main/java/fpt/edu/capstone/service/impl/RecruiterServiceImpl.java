package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.common.user.GooglePojo;
import fpt.edu.capstone.dto.AppliedJobByRecruiterResponse;
import fpt.edu.capstone.dto.admin.user.RecruiterManageResponse;
import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.dto.recruiter.ApprovalLicenseRequest;
import fpt.edu.capstone.entity.Recruiter;
import fpt.edu.capstone.entity.Users;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.repository.CompanyRepository;
import fpt.edu.capstone.repository.RecruiterRepository;
import fpt.edu.capstone.repository.UserRepository;
import fpt.edu.capstone.service.RecruiterService;
import fpt.edu.capstone.utils.Enums;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RecruiterServiceImpl implements RecruiterService {

    private final RecruiterRepository recruiterRepository;

    private final DinaryServiceImpl dinaryService;

    @Override
    public Recruiter getRecruiterByUserId(long userId) {
        return recruiterRepository.getRecruiterByUserId(userId);
    }

    @Override
    public boolean existById(long recId) {
        return recruiterRepository.existsById(recId);
    }

    @Override
    public Recruiter getRecruiterById(long id) {
        return recruiterRepository.getById(id);
    }

    @Override
    public Optional<Recruiter> findRecruiterByUserId(long userId) {
        return recruiterRepository.findByUserId(userId);
    }

    @Override
    public Optional<Recruiter> findById(long id) {
        return recruiterRepository.findById(id);
    }

    @Override
    public Recruiter insertRecruiter(long userId) {
        Recruiter recruiter = new Recruiter();
        recruiter.setUserId(userId);
        LocalDateTime nowDate = LocalDateTime.now();
        recruiter.setCreatedAt(nowDate);
        recruiter.setTotalCvView(-1);
        recruiter.setBusinessLicenseApprovalStatus("");
        recruiter.setAdditionalLicenseApprovalStatus("");
        return recruiterRepository.save(recruiter);
    }

    @Override
    public List<AppliedJobByRecruiterResponse> getListAppliedByForRecruiter(long recruiterId) {
        return recruiterRepository.getListAppliedByForRecruiter(recruiterId);
    }

    @Override
    public void updateCompany(long companyId, long id) {
        recruiterRepository.updateCompany(companyId, id);
    }

    @Override
    public Page<Recruiter> getRecruiterByCompanyId(long pageNo, long quantity, long companyId, String fullName, String email, String phone) {
        int pageReq = (int) (pageNo >= 1 ? pageNo - 1 : pageNo);
        Pageable pageable = PageRequest.of(pageReq, (int) quantity);
        return recruiterRepository.getRecruiterByCompanyId(companyId, fullName, email, phone, pageable);
    }

    @Override
    public Page<RecruiterManageResponse> searchRecruitersForAdmin(Pageable pageable, String username, String email, String fullName, long userId, boolean isLocked) {
        return recruiterRepository.searchRecruitersForAdmin(pageable, username, email, fullName, userId, isLocked);
    }

    //check thêm tên file có hợp lệ không
    @Override
    @Transactional(rollbackOn = Exception.class)
    public Recruiter uploadLicense(long recruiterId,
                                   MultipartFile businessMultipartFile,
                                   MultipartFile additionalMultipartFile) throws Exception {
        Optional<Recruiter> optionalRecruiter = findById(recruiterId);
        if (!optionalRecruiter.isPresent()) {
            throw new HiveConnectException(ResponseMessageConstants.USER_DOES_NOT_EXIST);
        }
        if (optionalRecruiter.get().getCompanyId() == 0) {
            throw new HiveConnectException(ResponseMessageConstants.RECRUITER_DOES_NOT_HAVE_COMPANY_INFOR);
        }
        if (businessMultipartFile == null && additionalMultipartFile == null) {
            throw new HiveConnectException(ResponseMessageConstants.CHOOSE_UPLOAD_FILE);
        }
        //check xem đã save được vào amazon chưa
        //"1": business license, "2": additional license
        if (businessMultipartFile != null) {
            //check xem license đã từng upload lên chưa, trạng thái như thế nào?
            if (optionalRecruiter.get().getBusinessLicenseUrl() != null
                    && optionalRecruiter.get().getBusinessLicenseApprovalStatus() != null) {
                if ((optionalRecruiter.get().getBusinessLicenseApprovalStatus().equals(Enums.ApprovalStatus.APPROVED.getStatus()))) {
                    throw new HiveConnectException(ResponseMessageConstants.BUSINESS_LICENSE_IS_PENDING_APPROVAL);
                }
            }
            String fileName = dinaryService.uploadFileToDinary(null, businessMultipartFile);
            //save to database
            optionalRecruiter.get().setBusinessLicenseUrl(fileName);
            optionalRecruiter.get().setBusinessLicenseApprovalStatus(Enums.ApprovalStatus.PENDING.getStatus());
            optionalRecruiter.get().update();
        }
        if (additionalMultipartFile != null) {
            if (optionalRecruiter.get().getAdditionalLicense() != null
                    && optionalRecruiter.get().getAdditionalLicenseApprovalStatus() != null) {
                if (optionalRecruiter.get().getAdditionalLicenseApprovalStatus().equals(Enums.ApprovalStatus.APPROVED.getStatus())
                        || optionalRecruiter.get().getAdditionalLicenseApprovalStatus().equals(Enums.ApprovalStatus.PENDING.getStatus())) {
                    throw new HiveConnectException(ResponseMessageConstants.BUSINESS_LICENSE_IS_PENDING_APPROVAL);
                }
            }
            String fileName = dinaryService.uploadFileToDinary(null, additionalMultipartFile);
            //save to database
            optionalRecruiter.get().setAdditionalLicense(fileName);
            optionalRecruiter.get().setAdditionalLicenseUrl(fileName);
            optionalRecruiter.get().setAdditionalLicenseApprovalStatus(Enums.ApprovalStatus.PENDING.getStatus());
            optionalRecruiter.get().update();
        }
        recruiterRepository.save(optionalRecruiter.get());
        return optionalRecruiter.get();
    }

    @Override
    public List<Recruiter> searchLicenseApprovalForAdmin(String businessApprovalStatus, String additionalApprovalStatus) {
        return recruiterRepository.searchLicenseApprovalForAdmin(businessApprovalStatus, additionalApprovalStatus);
    }

    @Override
    public Recruiter approveLicense(ApprovalLicenseRequest request) {
        Recruiter recruiter = recruiterRepository.getRecruiterByUserId(request.getUserId());
        if (recruiter == null) {
            throw new HiveConnectException(ResponseMessageConstants.USER_DOES_NOT_EXIST);
        }
        //accept , reject
        if (request.getType().equals("1")) {
            if (request.getApprovalStatus().equals(Enums.ApprovalStatus.APPROVED.getStatus())) {
                recruiter.setBusinessLicenseApprovalStatus(Enums.ApprovalStatus.APPROVED.getStatus());
            } else if (request.getApprovalStatus().equals(Enums.ApprovalStatus.REJECT.getStatus())) {
                recruiter.setBusinessLicenseApprovalStatus(Enums.ApprovalStatus.REJECT.getStatus());
            } else {
                throw new HiveConnectException(ResponseMessageConstants.APPROVAL_STATUS_INVALID);
            }
            recruiter.update();
            recruiterRepository.save(recruiter);
        } else if (request.getType().equals("2")) {
            if (request.getApprovalStatus().equals(Enums.ApprovalStatus.APPROVED.getStatus())) {
                recruiter.setAdditionalLicenseApprovalStatus(Enums.ApprovalStatus.APPROVED.getStatus());
            } else if (request.getApprovalStatus().equals(Enums.ApprovalStatus.REJECT.getStatus())) {
                recruiter.setAdditionalLicenseApprovalStatus(Enums.ApprovalStatus.REJECT.getStatus());
            } else {
                throw new HiveConnectException(ResponseMessageConstants.APPROVAL_STATUS_INVALID);
            }
            recruiter.update();
            recruiterRepository.save(recruiter);
        }
        return recruiter;
    }

    @Override
    public Recruiter getById(long recruiterId) {
        return recruiterRepository.getById(recruiterId);
    }

    @Override
    public void updateTotalCvView(long total, long recruiterId) {
        recruiterRepository.updateTotalCvView(total, recruiterId);
    }

    @Override
    public void insertGoogleRecruiter(GooglePojo googlePojo, Users user) {
        Recruiter recruiter = new Recruiter();
        recruiter.setFullName(googlePojo.getName());
        recruiter.setAvatarUrl(googlePojo.getPicture());
        recruiter.setUserId(user.getId());
        recruiter.setTotalCvView(-1);
        recruiterRepository.save(recruiter);
    }

    @Override
    public Integer getTotalViewCV(long recId) {
        Integer totalViewCV = recruiterRepository.getTotalViewCV(recId);
        if(totalViewCV == -1) {
            return 0;
        }
        return totalViewCV;
    }

    @Override
    public Recruiter insertRecruiterForRegister(long userId, String fullName) {
        Recruiter recruiter = new Recruiter();
        recruiter.setUserId(userId);
        LocalDateTime nowDate = LocalDateTime.now();
        recruiter.setCreatedAt(nowDate);
        recruiter.setTotalCvView(-1);
        recruiter.setFullName(fullName);
        recruiter.setBusinessLicenseApprovalStatus("");
        recruiter.setAdditionalLicenseApprovalStatus("");
        return recruiterRepository.save(recruiter);
    }

    @Override
    public void removeRecruiterFromCompany(long recId) {
        Recruiter recruiter = recruiterRepository.getById(recId);
        if(recruiter == null){
            throw new HiveConnectException(ResponseMessageConstants.USER_DOES_NOT_EXIST);
        }
        recruiter.setCompanyId(0);
        recruiterRepository.saveAndFlush(recruiter);
    }
}
