package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.VietnamCountry;
import fpt.edu.capstone.repository.CountryRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CountryServiceImplTest {

    @Mock
    private CountryRepository mockCountryRepository;

    private CountryServiceImpl countryServiceImplUnderTest;

    @Before
    public void setUp() throws Exception {
        countryServiceImplUnderTest = new CountryServiceImpl(mockCountryRepository);
    }

    @Test
    public void testGetListCountry() {
        final List<VietnamCountry> vietnamCountries = Arrays.asList(new VietnamCountry(1L, "countryName"));
        when(mockCountryRepository.findAll()).thenReturn(vietnamCountries);
        final List<VietnamCountry> result = countryServiceImplUnderTest.getListCountry();
    }

    @Test
    public void testGetListCountry_CountryRepositoryReturnsNoItems() {
        when(mockCountryRepository.findAll()).thenReturn(Collections.emptyList());
        final List<VietnamCountry> result = countryServiceImplUnderTest.getListCountry();
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    public void testFindById() {
        final Optional<VietnamCountry> vietnamCountry = Optional.of(new VietnamCountry(1L, "countryName"));
        when(mockCountryRepository.findById(1L)).thenReturn(vietnamCountry);
        final Optional<VietnamCountry> result = countryServiceImplUnderTest.findById(1L);
    }

    @Test
    public void testFindById_CountryRepositoryReturnsAbsent() {
        when(mockCountryRepository.findById(1L)).thenReturn(Optional.empty());
        final Optional<VietnamCountry> result = countryServiceImplUnderTest.findById(1L);
        assertThat(result).isEmpty();
    }
}
