package fpt.edu.capstone.repository;

import fpt.edu.capstone.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EducationRepository extends JpaRepository<Education,Long> {

    @Override
    Optional<Education> findById(Long aLong);

    @Query(value = "select * from Education e where e.cv_id = ?1", nativeQuery = true)
    List<Education> getListEducationByCvId(long cvId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO public.education (cv_id, school, major, start_date, end_date, description, is_studying) VALUES(?1, ?2, ?3, ?4, ?5, ?6, ?7)", nativeQuery = true)
    Education insertEducation(long cvId, String school, String major, LocalDateTime startDate, LocalDateTime endDate, String description, boolean isStudying);

    @Modifying
    @Transactional
    @Query(value = "UPDATE public.education SET school=?1, major=?2, start_date=?3, end_date=?4, description=?5, is_studying=?6 WHERE id=?7", nativeQuery = true)
    void updateEducation(String school, String major, LocalDateTime startDate, LocalDateTime endDate, String description, boolean isStudying, long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM public.education WHERE id=?1", nativeQuery = true)
    void deleteEducation(long id);
}
