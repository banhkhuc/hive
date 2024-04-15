package fpt.edu.capstone.controller;

import fpt.edu.capstone.dto.common.ResponseMessageConstants;
import fpt.edu.capstone.entity.Candidate;
import fpt.edu.capstone.entity.Users;
import fpt.edu.capstone.service.CandidateService;
import fpt.edu.capstone.service.UserService;
import fpt.edu.capstone.service.impl.DinaryServiceImpl;
import fpt.edu.capstone.utils.Enums;
import fpt.edu.capstone.utils.ResponseData;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;


@RestController
@RequestMapping("/api/v1/files")
@AllArgsConstructor
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    private final UserService userService;

    private final CandidateService candidateService;

    private final DinaryServiceImpl dinaryService;

//    @PostMapping("/import-file")
//    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
//        try {
//            userImageService.saveFile(file);
//
//            return ResponseEntity.status(HttpStatus.OK)
//                    .body(String.format("File uploaded successfully: %s", file.getOriginalFilename()));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(String.format("Could not upload the file: %s!", file.getOriginalFilename()));
//        }
//    }

//    @GetMapping("/get")
//    public List<FileResponse> list() {
//        return userImageService.getAllFiles()
//                .stream()
//                .map(this::mapToFileResponse)
//                .collect(Collectors.toList());
//    }

//    private FileResponse mapToFileResponse(Avatar avatar) {
//        String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
//                .path("/files/")
//                .path(avatar.getId())
//                .toUriString();
//        FileResponse fileResponse = new FileResponse();
//        fileResponse.setId(avatar.getId());
//        fileResponse.setName(avatar.getName());
//        fileResponse.setContentType(avatar.getContentType());
//        fileResponse.setSize(avatar.getSize());
//        fileResponse.setUrl(downloadURL);
//
//        return fileResponse;
//    }

//    @GetMapping("{id}")
//    public ResponseEntity<byte[]> getFile(@PathVariable String id) {
//        Optional<Avatar> fileEntityOptional = userImageService.getFile(id);
//
//        if (!fileEntityOptional.isPresent()) {
//            return ResponseEntity.notFound()
//                    .build();
//        }
//
//        Avatar avatar = fileEntityOptional.get();
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + avatar.getName() + "\"")
//                .contentType(MediaType.valueOf(avatar.getContentType()))
//                .body(avatar.getData());
//    }


//    @PostMapping("/upload-image")
//    public ResponseData uploadImage(@RequestPart ImageRequest imageRequest, @RequestPart("file") MultipartFile file) {
//        try {
//            if (imageRequest.getType().toLowerCase().equals("avatar")) { //if is image avatar
//                Optional<Users> users = userService.findByIdOp(imageRequest.getUserId());
//                if (users.isPresent()) { //Check if user is existed
//                    Optional<Avatar> avatarImgSearched = userImageService.findAvatarByUserId(imageRequest.getUserId());
//                    if (avatarImgSearched.isPresent()) {
//                        userImageService.updateAvatar(avatarImgSearched.get().getId(), file);
//                        return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Update avatar successful", avatarImgSearched.get().getId());
//                    } else {
//                        Avatar avatar = userImageService.save(file, "IMG", imageRequest.getUserId());
//                        userService.updateAvatarUrl(avatar.getId(), imageRequest.getUserId());
//                        return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Update avatar successful", avatar.getId());
//                    }
//                }
//                return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), "Can not find this user", imageRequest.getUserId());
//            } else { //id company image
//                Optional<Company> companySearched = companyService.findById(imageRequest.getCompanyId());
//                if (companySearched.isPresent()) {
//                    Optional<Image> imageSearched = imageService.findAvatarByCompanyId(imageRequest.getCompanyId());
//                    if (imageSearched.isPresent()) {
//                        imageService.updateAvatar(imageSearched.get().getId(), file);
//                        return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "update avatar successful", imageSearched.get().getId());
//                    } else {
//                        Image image = imageService.saveCompanyAvatar(file, "IMG", imageRequest.getCompanyId());
//                        companyService.updateCompanyAvatarUrl(image.getId(), image.getCompanyId());
//                        return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Insert avatar successful", image.getId());
//                    }
//                } else {
//                    return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), "Can not find this company", imageRequest.getCompanyId());
//                }
//            }
//        } catch (Exception ex) {
//            return null;
//        }
//    }

//    @GetMapping("/get-image/{id}")
//    public ResponseEntity<byte[]> getImage(@PathVariable String id) {
//
//        Optional<Avatar> fileEntityOptional = userImageService.getFile(id);
//        Optional<Image> image = imageService.finById(id);
//
//        if (fileEntityOptional.isPresent()) {
//            Avatar avatar = fileEntityOptional.get();
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + avatar.getName() + "\"")
//                    .contentType(MediaType.valueOf(avatar.getContentType()))
//                    .body(avatar.getData());
//        }
//
//        if (image.isPresent()) {
//            Image image1 = image.get();
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image1.getName() + "\"")
//                    .contentType(MediaType.valueOf(image1.getContentType()))
//                    .body(image1.getData());
//        }
//
//        return ResponseEntity.notFound()
//                .build();
//    }

//    @PostMapping("/upload-file")
//    public ResponseData uploadImages(@RequestParam String path, @RequestParam String uploadFileName) {
//        try {
//            AWSCredentials credentials = new BasicAWSCredentials(
//                    "AKIAXYJP2KLRA46NTLAN",
//                    "RlosMR/wqvzZ/eNWCUgU1bw3EAJuZx46kXzkeg1Z"
//            );
//            AmazonS3 s3client = AmazonS3ClientBuilder
//                    .standard()
//                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
//                    .withRegion(Regions.US_WEST_1)
//                    .build();
//            String bucketName = "hive-connect-images";
//
////            if (s3client.doesBucketExist(bucketName)) {
////                logger.info("Bucket name is not available."
////                        + " Try again with a different Bucket name.");
////                s3client.createBucket(bucketName);
//////                throw new HiveConnectException("Try contact admin, bucket does not exist");
////            }
//            List<Bucket> buckets = s3client.listBuckets();
//            for (Bucket bucket : buckets) {
//                System.out.println(bucket.getName());
//            }
//
//            s3client.putObject(
//                    bucketName,
//                    "E:/test2.txt",
//                    new File("D:/test2.txt")
//            );
//
//            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS);
//        } catch (Exception e) {
//            String msg = LogUtils.printLogStackTrace(e);
//            logger.error(msg);
//            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
//        }
//    }

    //cần validate đuôi file
//    @PostMapping("/upload-file")
//    public ResponseData uploadImages(@ModelAttribute UploadFileRequest request) {
////        if(request.getFile().getSize() > 5242880) {
////            logger.info("uploadImage > " + 5242880);
////            throw new HiveConnectException(ResponseMessageConstants.MAX_IMAGE_SIZE);
////        }
////        float fileSizeInMB = request.getFile().getSize() / 1_000_000;
////        if (fileSizeInMB > 5.0f){
////            throw new RuntimeException("File must  be <= 5mb");
////        }
//        try {
//
//            String fileUrl = amazonS3ClientService.uploadFile(request, request.getFile());
//            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.SUCCESS, fileUrl);
//        } catch (Exception e) {
//            String msg = LogUtils.printLogStackTrace(e);
//            logger.error(msg);
//            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), e.getMessage());
//        }
//    }
    private boolean isValidFile(MultipartFile file, String type){
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        if(type.equals("image")) {
            return  Arrays.asList(new String[] {"jpeg","jpg","png"}).contains(fileExtension.toLowerCase()) ;
        }
        return  Arrays.asList(new String[] {"pdf","jpeg","jpg","png"}).contains(fileExtension.toLowerCase()) ;
    }
    @PostMapping("upload-file")
    public ResponseData uploadToDinary(@RequestParam MultipartFile file, @RequestParam String typeUpload){ //Type = "IMG" || CV
        try {
            if(!isValidFile(file, typeUpload)) {
                return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), "Tệp bạn tải lên không đúng định dạng cho phép");
            }
            if(file.getSize() > 30000000) {
                return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), "Tệp bạn tải lên vượt quá 30 MB");
            }
            String url = dinaryService.uploadFile(file);
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(),
                    ResponseMessageConstants.UPLOAD_FILE_SUCCESS, url);
        }catch (Exception ex) {
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ex.getMessage());
        }
    }

    @PostMapping("/import/profile/{id}")
    public ResponseData importProfile(@RequestPart("file") MultipartFile file, @PathVariable("id") long userId){
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet worksheet = workbook.getSheetAt(0);
            for (int i = 2 ; i < worksheet.getPhysicalNumberOfRows(); i ++){
                Candidate candidate = candidateService.findCandidateByUserId(userId).get();
                Users user = userService.getUserById(userId);

                XSSFRow row  = worksheet.getRow(i);
                DataFormatter formatter = new DataFormatter();
                String phone = formatter.formatCellValue(row.getCell(1));
                candidate.setFullName(row.getCell(0).getStringCellValue());
                user.setPhone(phone);
                if(row.getCell(2).getStringCellValue().equalsIgnoreCase(ResponseMessageConstants.GENDER_MALE)){
                    candidate.setGender(true);
                } else {
                    candidate.setGender(false);
                }
                candidate.setBirthDate(null);
                candidate.setCountry(row.getCell(4).getStringCellValue());
                candidate.setAddress(row.getCell(5).getStringCellValue());
                candidate.setSocialLink(row.getCell(6).getStringCellValue());
                candidateService.save(candidate);
            }
            return new ResponseData(Enums.ResponseStatus.SUCCESS.getStatus(), ResponseMessageConstants.UPLOAD_FILE_SUCCESS);
        } catch (Exception e){
            return new ResponseData(Enums.ResponseStatus.ERROR.getStatus(), ResponseMessageConstants.UPLOAD_FILE_FAILED);
        }
    }
}
