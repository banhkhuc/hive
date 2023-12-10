package fpt.edu.capstone.service;

import fpt.edu.capstone.entity.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ImageService {
    boolean isValidFile(MultipartFile file, String type);

    Optional<Image> findAvatarByCompanyId(long companyId);

    Optional<Image> finById(long id);

    Image getAvatarRecruiter(long recruiterId);

    Image getAvatarCandidate(long candidateId);

    Optional<Image> getImageCompany(long companyId, boolean isAvatar);

    void deleteImageById(List<Long> deleteImageIdList);

    void saveImageCompany(boolean isAvatar, boolean isCoverImage, long companyId, List<String> uploadImageUrlList);

    List<Image> getCompanyImageList(long companyId, boolean isAvatar, boolean isCoverImage);
}
