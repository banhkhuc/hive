package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.Language;
import fpt.edu.capstone.repository.LanguageRepository;
import fpt.edu.capstone.service.LanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LanguageServiceImpl implements LanguageService {

    @Autowired
    LanguageRepository languageRepository;

    @Override
    public List<Language> getListLanguageByCvId(Long cvId) {
        return languageRepository.getListLanguageByCvId(cvId);
    }

    @Override
    public Language insertLanguage(Language newLanguage) {
        return languageRepository.save(newLanguage);
    }

    @Override
    public void deleteLanguage(Language language) {
        languageRepository.delete(language);
    }

    @Override
    public void updateLanguage(Language language) {
        languageRepository.updateLanguage(language.getLanguage(), language.getLevel(), language.getId());
    }

    @Override
    public Optional<Language> getLanguageById(long id) {
        return languageRepository.findById(id);
    }

}
