package fpt.edu.capstone.dto;

import fpt.edu.capstone.utils.Enums;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadFileRequest {
    private String type; //file extension
    private String uploadFileName; //
    private MultipartFile file;
    private String typeUpload;
}
