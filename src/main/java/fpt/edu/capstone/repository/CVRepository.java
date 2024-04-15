package fpt.edu.capstone.repository;

import fpt.edu.capstone.dto.CV.IFindCVResponse;
import fpt.edu.capstone.entity.CV;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CVRepository extends JpaRepository<CV, Long> {

    @Query(value = "select * from cv c where c.candidate_id = ?1", nativeQuery = true)
    List<CV> findCvByCandidateId(Long candidateId);

    CV getByCandidateId(long candidateId);

    @Query(value = "SELECT * FROM public.cv WHERE id=?1", nativeQuery = true)
    Optional<CV> findCvById(long id);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO public.cv (candidate_id, is_deleted, summary, created_at, updated_at) VALUES( ?1, ?2, ?3, ?4, ?5)", nativeQuery = true)
    void insertCv(Long candidateId, Long isDeleted, String summary, LocalDateTime createAt, LocalDateTime updateAt);

    @Modifying
    @Transactional
    @Query(value = "UPDATE public.cv SET summary=?2 WHERE id=?1", nativeQuery = true)
    void updateSummary(long cvId, String newSummary);

    @Modifying
    @Transactional
    @Query(value = "UPDATE public.cv SET updated_at =?2 WHERE id=?1", nativeQuery = true)
    void updateUpdatedDateOfCV(long id, LocalDateTime updatedDate);

    @Query(value = "select * from cv where id = ?1 and candidate_id = ?2", nativeQuery = true)
    Optional<CV> findByIdAndCAndCandidateId(long id, long candidateId);

    //dưới một năm kinh nghiệm
//    @Query(value = "with t1 as ( " +
//            "select c.id as cv_id, age(end_date, start_date) as experience_year from cv c " +
//            "join candidate ca on c.candidate_id = ca.id " +
//            "left join work_experience we on c.id = we.cv_id " +
//            "left join major_level ml on c.id = ml.cv_id " +
//            "where (ca.address like concat('%',:candidateAddress,'%') or :candidateAddress is null or :candidateAddress = '') " +
//            "and ml.major_id in (select m.id from major m where m.major_name like concat('%',:techStack,'%'))" +
//            ") " +
//            "select t1.cv_id as cvId, sum(experience_year) as sumExperienceYear from t1  " +
//            "group by cv_id " +
//            "having ((extract(year from sum(experience_year))::numeric(9,2)) < :experienceYearSearch1 and :experienceOption = 1) " +
//            "or ((extract(year from sum(experience_year))::numeric(9,2)) >= :experienceYearSearch1 " +
//            "and (extract(year from sum(experience_year))::numeric(9,2)) < :experienceYearSearch2 and :experienceOption = 2) " +
//            "or ((extract(year from sum(experience_year))::numeric(9,2)) >= :experienceYearSearch2 and :experienceOption = 3) " +
//            "or :experienceOption = 0",                                                                                                             //search all experience year
//            nativeQuery = true)
    @Query(value = "select distinct c.id id from cv c " +
            "join candidate ca on c.candidate_id = ca.id " +
            "left join work_experience we on c.id = we.cv_id " +
            "left join major_level ml on c.id = ml.cv_id " +
            "where (ca.address like concat('%', :candidateAddress,'%') or :candidateAddress is null or :candidateAddress = '') " +
            "and ml.major_id in (select m.id from major m where m.major_name like concat('%', :techStack, '%')) ", nativeQuery = true)
    Page<IFindCVResponse> findCvForRecruiter(Pageable pageable, //@Param("experienceOption") int experienceOption,
//                                             @Param("experienceYearSearch1") int experienceYearSearch1,
//                                             @Param("experienceYearSearch2") int experienceYearSearch2,
                                             @Param("candidateAddress") String candidateAddress,
                                             @Param("techStack") String techStack);

    @Query(value = "with t1 as ( " +
            "select c.id cv_id, age(end_date, start_date) experience_year from cv c " +
            "join candidate ca on c.candidate_id = ca.id " +
            "left join work_experience we on c.id = we.cv_id " +
            "left join major_level ml on c.id = ml.cv_id " +
            "where (ca.address like concat('%',:candidateAddress,'%') or :candidateAddress is null or :candidateAddress = '') " +
            "and ml.major_id in (select m.id from major m where m.major_name like concat('%',:techStack,'%'))" +
            ") " +
            "select t1.cv_id cvId, sum(experience_year) sumExperienceYear from t1  " +
            "group by cv_id " +
            "having ((extract(year from sum(experience_year))) < :experienceYearSearch1 and :experienceOption = 1) " +
            "or ((extract(year from sum(experience_year))) >= :experienceYearSearch1 " +
            "and (extract(year from sum(experience_year))) < :experienceYearSearch2 and :experienceOption = 2) " +
            "or ((extract(year from sum(experience_year))) >= :experienceYearSearch2 and :experienceOption = 3) " +
            "or :experienceOption = 0",                                                                                                             //search all experience year
            nativeQuery = true)
//    @Query(value = "  select t1.cv_id as cvId, sum(experience_year) as sumExperienceYear from " +
//            "  (select c.id as cv_id, age(end_date, start_date) as experience_year from cv c  \n" +
//            "        join candidate ca on c.candidate_id = ca.id  \n" +
//            "        left join work_experience we on c.id = we.cv_id  \n" +
//            "        left join major_level ml on c.id = ml.cv_id  \n" +
//            "        where (ca.address like concat('%',:candidateAddress,'%') or :candidateAddress is null or :candidateAddress = '')  \n" +
//            "        and ml.major_id in (select m.id from major m where m.major_name like concat('%',:techStack,'%'))\n" +
//            "  ) t1  " +
//            "        group by cv_id  " +
//            "        having ((extract(year from sum(experience_year))::numeric(9,2)) < :experienceYearSearch1 and :experienceOption = 1)  \n" +
//            "        or ((extract(year from sum(experience_year))::numeric(9,2)) >= :experienceYearSearch1  \n" +
//            "        and (extract(year from sum(experience_year))::numeric(9,2)) < :experienceYearSearch2 and :experienceOption = 2)  \n" +
//            "        or ((extract(year from sum(experience_year))::numeric(9,2)) >= :experienceYearSearch2 and :experienceOption = 3)  \n" +
//            "        or :experienceOption = 0")
    Page<IFindCVResponse> findCvTest(Pageable pageable, @Param("experienceOption") int experienceOption,
                                             @Param("experienceYearSearch1") int experienceYearSearch1,
                                             @Param("experienceYearSearch2") int experienceYearSearch2,
                                             @Param("candidateAddress") String candidateAddress,
                                             @Param("techStack") String techStack);

    @Query(value = "select distinct c from CV c " +
            "join Candidate ca on c.candidateId  = ca.id " +
            "left join MajorLevel ml on c.id  = ml.cvId " +
            "left join Major m on m.id = ml.majorId " +
            "where (lower(ca.address) like lower (concat('%',:candidateAddress,'%')) or :candidateAddress is null or :candidateAddress = '') " +
            "and (lower(m.majorName) like lower (concat('%',:techStack,'%')) or :techStack is null or :techStack = '')  " +
            "and (lower(c.totalExperienceYear) like lower (concat('%',:experience,'%')) or :experience is null or :experience = '') " +
            "and ca.isNeedJob = true")
    Page<CV> findCVFilter(Pageable pageable,@Param("experience") String experience,
                          @Param("candidateAddress") String candidateAddress,@Param("techStack") String techStack);

    @Query(value = "select count(*) from cv c", nativeQuery = true)
    int countCV();
}
