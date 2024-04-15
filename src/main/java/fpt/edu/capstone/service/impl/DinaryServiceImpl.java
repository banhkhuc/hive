package fpt.edu.capstone.service.impl;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import fpt.edu.capstone.dto.UploadFileRequest;
import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.utils.Enums;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class DinaryServiceImpl {
    private final Cloudinary cloudinaryConfig;

    public DinaryServiceImpl(Cloudinary cloudinaryConfig) {
        this.cloudinaryConfig = cloudinaryConfig;
    }

    private static final Logger logger = LoggerFactory.getLogger(DinaryServiceImpl.class);

    private  static final String[] IMAGE_TYPES = new String[]{"image/jpg","image/jpeg","image/png","image/bmp"};
    private static final String[] CV_TYPES = new String[]{
            "application/pdf",
            "application/msword",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    };
    private static final Map<Enums.FileUploadType,List<String>> TYPE_VALIDATORS = new HashMap<Enums.FileUploadType, List<String>>() {{
        put(Enums.FileUploadType.Image, Arrays.asList(IMAGE_TYPES));
        put(Enums.FileUploadType.CV, Arrays.asList(CV_TYPES));
    }};
    private long MAX_FILE_SIZE = 5242880;


    public String uploadFile(MultipartFile gif) {
        try {
            File uploadedFile = convertMultiPartToFile(gif);
            Map uploadResult = cloudinaryConfig.uploader().upload(uploadedFile, ObjectUtils.emptyMap());
            boolean isDeleted = uploadedFile.delete();
            return  uploadResult.get("url").toString().replace(".pdf", ".jpg");
        }catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    public String uploadFileToDinary(UploadFileRequest request, MultipartFile multipartFile) throws Exception {
        if(multipartFile == null) {
            throw new HiveConnectException(ResponseMessageConstants.CHOOSE_UPLOAD_FILE);
        }
        logger.info("file-size request:" + multipartFile.getSize());
        if (multipartFile.getSize() > MAX_FILE_SIZE) {
            logger.info("uploadImage - MAX_FILE_SIZE");
            throw new HiveConnectException(ResponseMessageConstants.UPLOAD_IMAGE_OVER_SIZE);
        }
        String fileName = uploadFile(multipartFile);
        System.out.println("Dinary=========  "+ fileName);
        return fileName;
    }
}
