package fpt.edu.capstone.service;

import fpt.edu.capstone.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LanguageService  {
    List<Language> getListLanguageByCvId(Long cvId);

    Language insertLanguage(Language newLanguage);

    void deleteLanguage(Language language);

    void updateLanguage(Language language);

    Optional<Language> getLanguageById(long id);
}
