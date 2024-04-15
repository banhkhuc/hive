package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.Language;
import fpt.edu.capstone.repository.LanguageRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LanguageServiceImplTest {

    private LanguageServiceImpl languageServiceImplUnderTest;

    @Before
    public void setUp() throws Exception {
        languageServiceImplUnderTest = new LanguageServiceImpl();
        languageServiceImplUnderTest.languageRepository = mock(LanguageRepository.class);
    }

    @Test
    public void testGetListLanguageByCvId() {
        final List<Language> list = Arrays.asList(new Language(1L, "language", "level", 1L));
        when(languageServiceImplUnderTest.languageRepository.getListLanguageByCvId(1L)).thenReturn(list);
        final List<Language> result = languageServiceImplUnderTest.getListLanguageByCvId(1L);
    }

    @Test
    public void testGetListLanguageByCvId_LanguageRepositoryReturnsNoItems() {
        when(languageServiceImplUnderTest.languageRepository.getListLanguageByCvId(1L))
                .thenReturn(Collections.emptyList());
        final List<Language> result = languageServiceImplUnderTest.getListLanguageByCvId(1L);
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    public void testInsertLanguage() {
        final Language newLanguage = new Language(1L, "language", "level", 1L);
        final Language language = new Language(1L, "language", "level", 1L);
        when(languageServiceImplUnderTest.languageRepository.save(any(Language.class))).thenReturn(language);
        final Language result = languageServiceImplUnderTest.insertLanguage(newLanguage);
    }

    @Test
    public void testDeleteLanguage() {
        final Language language = new Language(1L, "language", "level", 1L);
        languageServiceImplUnderTest.deleteLanguage(language);
        verify(languageServiceImplUnderTest.languageRepository).delete(any(Language.class));
    }

    @Test
    public void testUpdateLanguage() {
        final Language language = new Language(1L, "language", "level", 1L);
        languageServiceImplUnderTest.updateLanguage(language);
        verify(languageServiceImplUnderTest.languageRepository).updateLanguage("language", "level", 1L);
    }

    @Test
    public void testGetLanguageById() {
        final Optional<Language> language = Optional.of(new Language(1L, "language", "level", 1L));
        when(languageServiceImplUnderTest.languageRepository.findById(1L)).thenReturn(language);
        final Optional<Language> result = languageServiceImplUnderTest.getLanguageById(1L);
    }

    @Test
    public void testGetLanguageById_LanguageRepositoryReturnsAbsent() {
        when(languageServiceImplUnderTest.languageRepository.findById(1L)).thenReturn(Optional.empty());
        final Optional<Language> result = languageServiceImplUnderTest.getLanguageById(1L);
        assertThat(result).isEmpty();
    }
}
