package fpt.edu.capstone.repository;


import fpt.edu.capstone.entity.MajorLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface MajorLevelRepository extends JpaRepository<MajorLevel,Long> {
    @Query(value = "select * from major_level m where m.cv_id = ?1", nativeQuery = true)
    List<MajorLevel> getListMajorLevelByCvId(Long cvId);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO public.major_level(field_id, major_id, cv_id, level, status) VALUES(?1, ?2, ?3, ?4, ?5)", nativeQuery = true)
    void insertNewMajorLevel(long fieldId, long majorId, long cvId, String level, boolean status);

    @Transactional
    @Modifying
    @Query(value = "UPDATE public.major_level SET field_id=?1, major_id=?2, level=?3, status=?4 where id = ?5", nativeQuery = true)
    void updateNewMajorLevel(long fieldId, long majorId, String level, boolean status, long id);
}
