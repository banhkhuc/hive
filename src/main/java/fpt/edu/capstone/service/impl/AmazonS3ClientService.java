//package fpt.edu.capstone.service.impl;
//
//import fpt.edu.capstone.utils.Enums;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//
//@Service
//public class AmazonS3ClientService {
//
//    private  static final String[] IMAGE_TYPES = new String[]{"image/jpg","image/jpeg","image/png","image/bmp"};
//    private static final String[] CV_TYPES = new String[]{
//            "application/pdf",
//            "application/msword",
//            "application/vnd.ms-excel",
//            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
//            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
//    };
//    private static final Map<Enums.FileUploadType,List<String>> TYPE_VALIDATORS = new HashMap<Enums.FileUploadType, List<String>>() {{
//        put(Enums.FileUploadType.Image, Arrays.asList(IMAGE_TYPES));
//        put(Enums.FileUploadType.CV, Arrays.asList(CV_TYPES));
//    }};
//}
