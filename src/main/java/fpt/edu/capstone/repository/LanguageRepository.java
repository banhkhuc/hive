package fpt.edu.capstone.repository;

import fpt.edu.capstone.entity.CV;
import fpt.edu.capstone.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface LanguageRepository extends JpaRepository<Language, Long> {
    @Query(value = "select * from Language l where l.cv_id = ?1", nativeQuery = true)
    List<Language> getListLanguageByCvId(Long cvId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO language (language, level, cv_id) VALUES(?1, ?2, ?3)", nativeQuery = true)
    void insertLanguage(String language, String level, long cvId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE public.language SET language=?1, level=?2 WHERE id=?3", nativeQuery = true)
    void updateLanguage(String language, String level, long id);


}
