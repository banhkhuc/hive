package fpt.edu.capstone.repository;

import fpt.edu.capstone.dto.admin.user.CandidateManageResponse;
import fpt.edu.capstone.entity.Candidate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO public.candidate (user_id, gender, birth_date, search_history, country, full_name, address, social_link, avatar_url, wishlist_job_id_list, tap_history_id_list, is_need_job, experience_level, introduction) VALUES(?1, true, null, null, null, null, null, null, null, null, null, true, 0, null)", nativeQuery = true)
    void insertCandidate(long userId);


    @Query(value = "select * from Candidate c where c.user_id = ?1", nativeQuery = true)
    Optional<Candidate> findCandidateByUserId(long userId);

    boolean existsById(long id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE public.candidate SET gender=?1, birth_date=?2, country=?3, full_name=?4, address=?5, social_link=?6, introduction=?7 WHERE id=?8", nativeQuery = true)
    void updateCandidateInformation(boolean gender, LocalDateTime birthDate, String country, String fullName, String address, String socialLink,  String introduction, long id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE public.candidate SET gender=?1, birth_date=?2, country=?3, full_name=?4, address=?5, social_link=?6 WHERE user_id=?7", nativeQuery = true)
    void updateCVInformation(boolean gender, LocalDateTime birthDate, String country, String fullName, String address, String socialLink, long userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE public.candidate SET is_need_job = ?1 WHERE id = ?2", nativeQuery = true)
    void updateIsNeedJob(boolean newIsNeedJob, long id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE public.candidate SET avatar_url=?1 WHERE id=?2", nativeQuery = true)
    void updateAvatarUrl(String avatarId, long id);

    @Query(value = "select u.id as userId, c.id as candidateId, u.avatar as avatar, c.gender as gender, c.birth_date as birthDate, c.country as country, " +
            "c.full_name as fullName, u.username as userName, c.address as address, c.social_link as socialLink, c.is_need_job as isNeedJob, c.experience_level as experienceLevel, " +
            "c.introduction as introduction, u.email as email, u.role_id as roleId, u.is_active as isActive, r.name as roleName, u.is_deleted as isDeleted, u.last_login_time as lastLoginTime, " +
            "u.is_verified_email as isVerifiedEmail, u.created_at as createdAt, u.updated_at as updatedAt, u.is_locked as isLocked " +
            "from users u join candidate c on u.id = c.user_id " +
            "join roles r on u.role_id = r.id " +
            "where lower(u.username) like lower(concat('%',:username,'%')) and lower(u.email) like lower(concat('%',:email,'%')) " +
            "and lower(c.full_name) like  lower(concat('%',:fullName,'%')) " +
            "and (u.id =:userId or 0=:userId) " +
            "and u.is_locked=:isLocked", nativeQuery = true)
    Page<CandidateManageResponse> searchCandidateForAdmin(Pageable pageable, @Param("username") String username,
                                                          @Param("email") String email,@Param("fullName") String fullName,
                                                          @Param("userId") long userId, @Param("isLocked") boolean isLocked);

    @Query(value = "select * from candidate c where c.id = ?", nativeQuery = true)
    Candidate getCandidateById(long id);

    @Query(value = "INSERT INTO public.candidate (user_id, gender, birth_date, search_history, country, full_name, address, social_link, avatar_url, wishlist_job_id_list, tap_history_id_list, is_need_job, experience_level, introduction) " +
            "VALUES(?1, true, null, null, null, ?2, null, null, null, null, null, true, 0, null)", nativeQuery = true)
    void insertCandidateForRegister(long userId, String fullName);
}
