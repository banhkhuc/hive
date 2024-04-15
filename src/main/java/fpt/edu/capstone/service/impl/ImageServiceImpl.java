package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.Image;
import fpt.edu.capstone.repository.ImageRepository;
import fpt.edu.capstone.service.ImageService;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    //type = IMG || CV
    public boolean isValidFile(MultipartFile file, String type) {
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (type.equals("IMG")) {
            return Arrays.asList(new String[]{"jpeg", "jpg", "png"}).contains(fileExtension.toLowerCase());
        }
        return Arrays.asList(new String[]{"pdf"}).contains(fileExtension.toLowerCase());
    }

    public Image saveCompanyAvatar(MultipartFile file, String type, long companyId) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("Failed to store empty file");
        }
        if (!isValidFile(file, type)) {
            throw new RuntimeException("File is invalid");
        }
        float fileSizeInMB = file.getSize() / 1_000_000;
        if (fileSizeInMB > 25.0f) {
            throw new RuntimeException("File must  be <= 25mb");
        }
        Image image = new Image();
        image.setAvatar(true);
        image.setCompanyId(companyId);
        image.setName(StringUtils.cleanPath(file.getOriginalFilename()));
        image.setContentType(file.getContentType());
        image.setData(file.getBytes());

        return imageRepository.save(image);
    }

    public Optional<Image> findAvatarByCompanyId(long companyId) {
        return imageRepository.findAvatarByCompanyId(companyId);
    }

    public void updateAvatar(String id, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("Failed to store empty file");
        }
        if (!isValidFile(file, "IMG")) {
            throw new RuntimeException("File is invalid");
        }
        float fileSizeInMB = file.getSize() / 1_000_000;
        if (fileSizeInMB > 25.0f) {
            throw new RuntimeException("File must  be <= 25mb");
        }
        imageRepository.updateCompanyAvatar(file.getBytes(), true, id);
    }

    public Optional<Image> finById(long id) {
        return imageRepository.findById(id);
    }

    @Override
    public Image getAvatarRecruiter(long recruiterId) {
        return imageRepository.getAvatarRecruiter(recruiterId);
    }

    @Override
    public Image getAvatarCandidate(long candidateId) {
        return imageRepository.getAvatarCandidate(candidateId);
    }

    @Override
    public Optional<Image> getImageCompany(long companyId, boolean isAvatar) {
        return imageRepository.getImageCompany(companyId, isAvatar);
    }

    @Override
    public void deleteImageById(List<Long> deleteImageIdList) {
        if(deleteImageIdList.isEmpty()) {
            deleteImageIdList.add(0L);
        }
        imageRepository.deleteImageById(deleteImageIdList);
    }

    @Override
    public void saveImageCompany(boolean isAvatar, boolean isCoverImage, long companyId, List<String> uploadImageUrlList) {
        for(String url : uploadImageUrlList) {
            Image image = new Image();
            image.setCompanyId(companyId);
            image.setUrl(url);
            image.setAvatar(isAvatar);
            image.setCover(isCoverImage);
            if(image.isAvatar()) {
                Optional<Image> avaOp = imageRepository.getAvatarOfCompanyByCompanyId(companyId, true);
                if(avaOp.isPresent()) {
                    imageRepository.updateCompanyAvatarUrl(url, true, companyId);
                    return;
                }
            }
            if(image.isCover()) {
                Optional<Image> coverOp = imageRepository.getCoverImageOfCompanyByCompanyId(companyId, true);
                if(coverOp.isPresent()) {
                    imageRepository.updateCompanyCoverImage(url, true, companyId);
                    return;
                }
            }
            imageRepository.save(image);
        }
    }

    @Override
    public List<Image> getCompanyImageList(long companyId, boolean isAvatar, boolean isCoverImage) {
        return imageRepository.getCompanyImageList(companyId, isAvatar, isCoverImage);
    }
}
