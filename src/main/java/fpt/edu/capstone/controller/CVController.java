package fpt.edu.capstone.controller;


import fpt.edu.capstone.dto.CV.*;
import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.entity.*;
import fpt.edu.capstone.service.*;
import fpt.edu.capstone.utils.Enums;
import fpt.edu.capstone.utils.LogUtils;
import fpt.edu.capstone.utils.ResponseData;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/CV")
public class CVController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private CVService cvService;

    @Autowired
    private UserService userService;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private EducationService educationService;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private MajorLevelService majorLevelService;

    @Autowired
    private OtherSkillService otherSkillService;

    @Autowired
    private WorkExperienceService workExperienceService;

    @Autowired
    private FieldsService fieldsService;

    @Autowired
    private MajorService majorService;

    @Autowired
    private RecruiterService recruiterService;

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RecruiterManageService recruiterManageService;

    @Autowired
    private CandidateManageService candidateManageService;

    @Autowired
    private ProfileViewerService profileViewerService;

    //Them phan add summary

    @GetMapping("/get-list-CV")
    public ResponseData getListCompany() {
        return null;
    }

    @GetMapping("/get-cv")
    public ResponseData findCvByCandidateID(@RequestParam long candidateId) {
        try {
            CVResponse cvResponse = candidateManageService.findCvByCandidateId(candidateId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS,
                    cvResponse);
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ex.getMessage());
        }
    }

    @PostMapping("/create-cv")
    public ResponseData createCV(@RequestBody CVRequest request) {
        try {
            CV cv = candidateManageService.createCV(request);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, cv);
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ex.getMessage());
        }
    }

    @PostMapping("/insert-education")
    public ResponseData insertEducation(@RequestBody Education newEdudation) {
        try {
            Optional<CV> cv = cvService.findCvById(newEdudation.getCvId());
            if (!cv.isPresent()) {
                return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.CV_NOT_EXIST);
            }
            Education ed = educationService.insertEducation(newEdudation);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.CREATE_SUCCESSFULLY, ed);
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ex.getMessage());
        }
    }

    @PostMapping("/insert-work-exp")
    public ResponseData insertWorkExp(@RequestBody WorkExperience newWorkExperience) {
        try {
            Optional<CV> cv = cvService.findCvById(newWorkExperience.getCvId());
            if (!cv.isPresent()) {
                return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.CV_NOT_EXIST);
            }
            newWorkExperience.setWorking(false);
            WorkExperience workExperience = workExperienceService.insertWorkExperience(newWorkExperience);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.CREATE_SUCCESSFULLY,
                    workExperience);
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ex.getMessage());
        }
    }

    @PostMapping("/insert-language")
    public ResponseData insertLanguage(@RequestBody Language newLanguage) {
        try {
            Optional<CV> cv = cvService.findCvById(newLanguage.getCvId());
            if (!cv.isPresent()) {
                return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.CV_NOT_EXIST);
            }
            Language language = languageService.insertLanguage(newLanguage);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.CREATE_SUCCESSFULLY,
                    language);
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ex.getMessage());
        }
    }

    @PostMapping("/insert-other-skill")
    public ResponseData insertOtherSkill(@RequestBody OtherSkill newOtherSkill) {
        try {
            Optional<CV> cv = cvService.findCvById(newOtherSkill.getCvId());
            if (!cv.isPresent()) {
                return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.CV_NOT_EXIST);
            }
            OtherSkill otherSkill = otherSkillService.insertOtherSkill(newOtherSkill);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.CREATE_SUCCESSFULLY, otherSkill);
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ex.getMessage());
        }
    }

    @PostMapping("/insert-certificate")
    public ResponseData insertCert(@RequestBody Certificate newCertificate) {
        try {
            Optional<CV> cv = cvService.findCvById(newCertificate.getCvId());
            if (!cv.isPresent()) {
                return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.CV_NOT_EXIST);
            }
            Certificate certificate = certificateService.insertCertificate(newCertificate);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.CREATE_SUCCESSFULLY, certificate);
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ex.getMessage());
        }
    }

    @GetMapping("/get-all-field")
    public ResponseData getAllField() {
        try {
            List<Fields> fields = fieldsService.getAllField();
            if (fields.isEmpty()) {
                return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), "Không có lĩnh vực môn nào");
            }
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, fields);
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ex.getMessage());
        }
    }

    @GetMapping("/get-major-by-field")
    public ResponseData getMajorByFieldId(@RequestParam long fieldId) {
        try {
            List<Major> majors = majorService.getAllMajorByFieldId(fieldId);
            if (majors.isEmpty()) {
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Chuyên môn trống.");
            }
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, majors);
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ex.getMessage());
        }
    }

    @PostMapping("/insert-major-level")
    public ResponseData insertMajorLevel(@RequestBody MajorLevel newMajorLevel) {
        try {
            Optional<CV> cv = cvService.findCvById(newMajorLevel.getCvId());
            if (!cv.isPresent()) {
                return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.CV_NOT_EXIST);
            }
            MajorLevel majorLevel = majorLevelService.insertNewMajorLevel(newMajorLevel);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.CREATE_SUCCESSFULLY, majorLevel);

        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ex.getMessage());
        }
    }

    @PostMapping("/update-cv-summary")
    public ResponseData updateCvSummary(@RequestBody UpdateCVSummaryRequest updateCVSummaryRequest) {
        try {
            Optional<CV> cv = cvService.findCvById(updateCVSummaryRequest.getCvId());
            if (cv.isPresent()) {
                cvService.updateSummary(updateCVSummaryRequest.getCvId(), updateCVSummaryRequest.getNewSummary());
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(),
                        ResponseMessageConstants.UPDATE_SUCCESSFULLY, updateCVSummaryRequest.getNewSummary());
            }
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.CV_NOT_EXIST,
                    updateCVSummaryRequest.getCvId());
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ex.getMessage());
        }
    }

    @PutMapping("/update-updated-date-of-cv")
    public ResponseData updateUpdatedDateOfCv(@RequestBody long id) {
        try {
            Optional<CV> cv = cvService.findCvById(id);
            if (cv.isPresent()) {
                LocalDateTime nowDate = LocalDateTime.now();
                cvService.updateUpdatedDateOfCV(id, nowDate);
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(),
                        ResponseMessageConstants.UPDATE_SUCCESSFULLY, nowDate);
            }
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.CV_NOT_EXIST);
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ex.getMessage());
        }
    }

    @PutMapping("/update-education")
    public ResponseData updateEducationById(@RequestBody Education updateEducation) {
        try {
            Optional<Education> education = educationService.getEducationById(updateEducation.getId());
            if (education.isPresent()) {
                LocalDateTime nowDate = LocalDateTime.now();
                educationService.updateEducation(updateEducation);
                cvService.updateUpdatedDateOfCV(updateEducation.getCvId(), nowDate);
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(),
                        ResponseMessageConstants.UPDATE_SUCCESSFULLY, updateEducation);
            }
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Học vấn trống.");
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ex.getMessage());
        }
    }

    @DeleteMapping("/delete-education")
    public ResponseData deleteEducationById(@RequestParam(value = "id") long id) {
        try {
            Optional<Education> education = educationService.getEducationById(id);
            if (education.isPresent()) {
                LocalDateTime nowDate = LocalDateTime.now();
                educationService.deleteEducation(education.get().getId());
                cvService.updateUpdatedDateOfCV(education.get().getCvId(), nowDate);
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(),
                        ResponseMessageConstants.DELETE_SUCCESSFULLY, education);
            }
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Học vấn trống.", null);
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ex.getMessage());
        }
    }

    @PutMapping("/update-work-experience")
    public ResponseData updateWorkExperienceById(@RequestBody WorkExperience updateWorkExperience) {
        try {
            Optional<WorkExperience> workExperience = workExperienceService.getWorkExperienceById(updateWorkExperience.getId());
            if (workExperience.isPresent()) {
                LocalDateTime nowDate = LocalDateTime.now();
                workExperienceService.updateWordExperience(updateWorkExperience);
                cvService.updateUpdatedDateOfCV(updateWorkExperience.getCvId(), nowDate);
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(),
                        ResponseMessageConstants.UPDATE_SUCCESSFULLY, updateWorkExperience);
            }
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Kinh nghiệm làm việc trống.");
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ex.getMessage());
        }
    }

    @DeleteMapping("/delete-work-experience")
    public ResponseData deleteWorkExperienceById(@RequestParam(value = "id") long id) {
        try {
            Optional<WorkExperience> workExperience = workExperienceService.getWorkExperienceById(id);
            if (workExperience.isPresent()) {
                LocalDateTime nowDate = LocalDateTime.now();
                workExperienceService.deleteWordExperience(workExperience.get());
                cvService.updateUpdatedDateOfCV(workExperience.get().getCvId(), nowDate);
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(),
                        ResponseMessageConstants.DELETE_SUCCESSFULLY, workExperience);
            }
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Kinh nghiệm làm việc trống.", null);
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ex.getMessage());
        }
    }

    //Certificate
    @PutMapping("/update-certificate")
    public ResponseData updateCertidicate(@RequestBody Certificate updateCertificate) {
        try {
            Optional<Certificate> certificate = certificateService.getCertificateById(updateCertificate.getId());
            if (certificate.isPresent()) {
                LocalDateTime nowDate = LocalDateTime.now();
                certificateService.updateService(updateCertificate);
                cvService.updateUpdatedDateOfCV(updateCertificate.getCvId(), nowDate);
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(),
                        ResponseMessageConstants.UPDATE_SUCCESSFULLY, updateCertificate);
            }
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Bằng cấp trống.");
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ex.getMessage());
        }
    }

    @DeleteMapping("/delete-certificate")
    public ResponseData deleteCertificateById(@RequestParam(value = "id") long id) {
        try {
            Optional<Certificate> certificate = certificateService.getCertificateById(id);
            if (certificate.isPresent()) {
                certificateService.deleteCertificate(certificate.get());
            }
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(),ResponseMessageConstants.DELETE_SUCCESSFULLY);
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ex.getMessage());
        }
    }

    //Language
    @PutMapping("/update-language")
    public ResponseData updateLanguage(@RequestBody Language updateLanguage) {
        try {
            Optional<Language> language = languageService.getLanguageById(updateLanguage.getId());
            if (language.isPresent()) {
                LocalDateTime nowDate = LocalDateTime.now();
                languageService.updateLanguage(updateLanguage);
                cvService.updateUpdatedDateOfCV(updateLanguage.getCvId(), nowDate);
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.UPDATE_SUCCESSFULLY, updateLanguage);
            }
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Ngoại ngữ trống.");
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ex.getMessage());
        }
    }

    @DeleteMapping("/delete-language")
    public ResponseData deleteLanguageById(@RequestParam(value = "id") long id) {
        try {
            Optional<Language> language = languageService.getLanguageById(id);
            if (language.isPresent()) {
                LocalDateTime nowDate = LocalDateTime.now();
                languageService.deleteLanguage(language.get());
                cvService.updateUpdatedDateOfCV(language.get().getCvId(), nowDate);
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(),
                        ResponseMessageConstants.DELETE_SUCCESSFULLY, language.get());
            }
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Ngoại ngữ trống.");
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ex.getMessage());
        }
    }

    //major level
    @PutMapping("/update-major-level")
    public ResponseData updateMajorLevel(@RequestBody MajorLevel updateMajorLevel) {
        try {
            Optional<MajorLevel> majorLevel = majorLevelService.getMajorLevelById(updateMajorLevel.getId());
            if (majorLevel.isPresent()) {
                LocalDateTime nowDate = LocalDateTime.now();
                majorLevelService.updateMajorLevel(updateMajorLevel);
                cvService.updateUpdatedDateOfCV(updateMajorLevel.getCvId(), nowDate);
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(),
                        ResponseMessageConstants.UPDATE_SUCCESSFULLY, updateMajorLevel);
            }
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Trình độ, chuyên môn trống.");
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ex.getMessage());
        }
    }

    @DeleteMapping("/delete-major-level")
    public ResponseData deleteMajorLevel(@RequestParam(value = "id") long id) {
        try {
            Optional<MajorLevel> majorLevel = majorLevelService.getMajorLevelById(id);
            if (majorLevel.isPresent()) {
                LocalDateTime nowDate = LocalDateTime.now();
                majorLevelService.deleteMajorLevel(majorLevel.get());
                cvService.updateUpdatedDateOfCV(majorLevel.get().getCvId(), nowDate);
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(),
                        ResponseMessageConstants.DELETE_SUCCESSFULLY, majorLevel);
            }
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Trình độ, chuyên môn trống.");
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ex.getMessage());
        }
    }

    //Other Skill
    @PutMapping("/update-other-skill")
    public ResponseData updateOtherSkill(@RequestBody OtherSkill updateOtherSkill) {
        try {
            Optional<OtherSkill> otherSkill = otherSkillService.getOtherSkillById(updateOtherSkill.getId());
            if (otherSkill.isPresent()) {
                LocalDateTime nowDate = LocalDateTime.now();
                otherSkillService.updateOtherSKill(updateOtherSkill);
                cvService.updateUpdatedDateOfCV(updateOtherSkill.getCvId(), nowDate);
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(),
                        ResponseMessageConstants.UPDATE_SUCCESSFULLY, updateOtherSkill);
            }
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Kỹ năng khác trống.");
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ex.getMessage());
        }
    }

    @DeleteMapping("/delete-other-skill")
    public ResponseData deleteOtherSkill(@RequestParam(value = "id") long id) {
        try {
            Optional<OtherSkill> otherSkill = otherSkillService.getOtherSkillById(id);
            if (otherSkill.isPresent()) {
                LocalDateTime nowDate = LocalDateTime.now();
                otherSkillService.deleteOtherSkill(otherSkill.get());
                cvService.updateUpdatedDateOfCV(otherSkill.get().getCvId(), nowDate);
                return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.DELETE_SUCCESSFULLY, otherSkill.get());
            }
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Kỹ năng khác trống.");
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ex.getMessage());
        }
    }

    @GetMapping("/get-cv-with-pay")
    public ResponseData getCvWithPay(@RequestParam long recruiterId, @RequestParam long cvId) {
        try {
            ViewCVWithPayResponse response = recruiterManageService.getCvWithPay(recruiterId, cvId);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), response.getMessage(),
                    response.getCvProfileResponse());
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ex.getMessage());
        }
    }

    @GetMapping("/preview-cv")
    public ResponseData previewCV(@RequestParam long recruiterId, @RequestParam long cvId) {
        try {
            ViewCVWithPayResponse response = new ViewCVWithPayResponse();
            if(profileViewerService.getByCvIdAndViewerIdOptional(cvId, recruiterId).isPresent()) {
                response = recruiterManageService.getCvWithPay(recruiterId, cvId);
            }else {
                response = recruiterManageService.previewCV(recruiterId, cvId);
            }
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), response.getMessage(),
                    response.getCvProfileResponse());
        } catch (Exception ex) {
            String msg = LogUtils.printLogStackTrace(ex);
            logger.error(msg);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ex.getMessage());
        }
    }
}
