package fpt.edu.capstone.repository;

import com.amazonaws.services.apigateway.model.Op;
import fpt.edu.capstone.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    @Query(value = "select * from image where company_id = ?1 and is_avatar = true", nativeQuery = true)
    Optional<Image> findAvatarByCompanyId(long companyId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE image SET data=?1, is_avatar = ?2 WHERE id=?3", nativeQuery = true)
    void updateCompanyAvatar(byte[] data,boolean isAvatar, String imageId);

    @Query(value = "select * from image i where i.recruiter_id = ? and i.is_avatar = true", nativeQuery = true)
    Image getAvatarRecruiter(long recruiterId);

    @Query(value = "select * from image i where i.candidate_id = ? and i.is_avatar = true", nativeQuery = true)
    Image getAvatarCandidate(long candidateId);

    @Query(value = "select * from image i where i.company_id = ?1 and i.is_avatar = ?2", nativeQuery = true)
    Optional<Image> getImageCompany(long companyId, boolean isAvatar);

    @Modifying
    @Query(value = "delete from image i where id in (:deleteImageIdList)", nativeQuery = true)
    void deleteImageById(List<Long> deleteImageIdList);

    @Query(value = "select * from image i where i.company_id = ?1 and is_avatar = ?2 and is_cover = ?3", nativeQuery = true)
    List<Image> getCompanyImageList(long companyId, boolean isAvatar, boolean isCoverImage);

    @Modifying
    @Transactional
    @Query(value = "Update image SET url = ?1 where is_avatar = ?2 and company_id = ?3", nativeQuery = true)
    void updateCompanyAvatarUrl(String url, boolean isAvatar, long companyId);

    @Query(value = "Select * from image where company_id = ?1 and is_avatar = ?2", nativeQuery = true)
    Optional<Image> getAvatarOfCompanyByCompanyId(long companyId, boolean isAvatar);

    @Query(value = "Select * from image where company_id = ?1 and is_cover = ?2", nativeQuery = true)
    Optional<Image> getCoverImageOfCompanyByCompanyId(long companyId, boolean isCoverImage);

    @Modifying
    @Transactional
    @Query(value = "Update image SET url = ?1 where is_cover = ?2 and company_id = ?3", nativeQuery = true)
    void updateCompanyCoverImage(String url, boolean isCoverImage, long companyId);
}
