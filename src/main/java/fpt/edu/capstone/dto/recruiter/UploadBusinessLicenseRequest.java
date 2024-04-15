package fpt.edu.capstone.dto.recruiter;

import fpt.edu.capstone.dto.UploadFileRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class UploadBusinessLicenseRequest {
    private long recruiterId;
    private MultipartFile businessMultipartFile;
    private MultipartFile additionalMultipartFile;
}
