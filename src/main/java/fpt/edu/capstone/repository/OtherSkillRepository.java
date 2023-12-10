package fpt.edu.capstone.repository;

import fpt.edu.capstone.entity.OtherSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface OtherSkillRepository extends JpaRepository<OtherSkill,Long> {
    @Query(value = "select * from other_skill o where o.cv_id = ?1", nativeQuery = true)
    List<OtherSkill> getListOtherSkillByCvId(Long cvId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO other_skill (skill_name, cv_id, level) VALUES(?1, ?2, ?3)", nativeQuery = true)
    void insertOtherSkill(String skillName, long cvId, String level);

    @Modifying
    @Transactional
    @Query(value = "UPDATE public.other_skill SET skill_name=?1, level=?2 WHERE id=?3", nativeQuery = true)
    void updateOtherSkill(String skillName, String level, long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM public.other_skill WHERE id=?1", nativeQuery = true)
    void deleteOtherSkill(long id);
}
