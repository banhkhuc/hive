package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.RentalPackage;
import fpt.edu.capstone.repository.RentalPackageServiceRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RentalPackageServiceImplTest {

    @Mock
    private RentalPackageServiceRepository mockRentalPackageServiceRepository;

    private RentalPackageServiceImpl rentalPackageServiceImplUnderTest;

    @Before
    public void setUp() {
        rentalPackageServiceImplUnderTest = new RentalPackageServiceImpl(mockRentalPackageServiceRepository);
    }

    private RentalPackage rentalPackage(){
        RentalPackage rentalPackage = new RentalPackage();
        rentalPackage.setId(1L);
        rentalPackage.setPackageGroup("packageGroup");
        rentalPackage.setDescription("description");
        rentalPackage.setStatus(false);
        return rentalPackage;
    }

    @Test
    public void testFindAll() {
        final List<RentalPackage> rentalPackageList = Arrays.asList(rentalPackage());
        when(mockRentalPackageServiceRepository.findAll()).thenReturn(rentalPackageList);
        final List<RentalPackage> result = rentalPackageServiceImplUnderTest.findAll();
    }

    @Test
    public void testFindAll_RentalPackageServiceRepositoryReturnsNoItems() {
        when(mockRentalPackageServiceRepository.findAll()).thenReturn(Collections.emptyList());
        final List<RentalPackage> result = rentalPackageServiceImplUnderTest.findAll();
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    public void testExistById() {
        when(mockRentalPackageServiceRepository.existsById(1L)).thenReturn(false);
        final boolean result = rentalPackageServiceImplUnderTest.existById(1L);
        assertThat(result).isFalse();
    }

    @Test
    public void testExistByName() {
        when(mockRentalPackageServiceRepository.existsByPackageGroup("groupName")).thenReturn(false);
        final boolean result = rentalPackageServiceImplUnderTest.existByName("groupName");
        assertThat(result).isFalse();
    }

    @Test
    public void testSaveRentalPackage() {
        final RentalPackage rentalPackage = rentalPackage();
        final RentalPackage rentalPackage1 = rentalPackage();
        when(mockRentalPackageServiceRepository.save(any(RentalPackage.class))).thenReturn(rentalPackage1);
        rentalPackageServiceImplUnderTest.saveRentalPackage(rentalPackage);
        verify(mockRentalPackageServiceRepository).save(any(RentalPackage.class));
    }

    @Test
    public void testFindById() {
        final Optional<RentalPackage> optional = Optional.of(rentalPackage());
        when(mockRentalPackageServiceRepository.findById(1L)).thenReturn(optional);
        final Optional<RentalPackage> result = rentalPackageServiceImplUnderTest.findById(1L);
    }

    @Test
    public void testFindById_RentalPackageServiceRepositoryReturnsAbsent() {
        when(mockRentalPackageServiceRepository.findById(1L)).thenReturn(Optional.empty());
        final Optional<RentalPackage> result = rentalPackageServiceImplUnderTest.findById(1L);
        assertThat(result).isEmpty();
    }

    @Test
    public void testGetRentalPackageName() {
        when(mockRentalPackageServiceRepository.getRentalPackageName(1L)).thenReturn("result");
        final String result = rentalPackageServiceImplUnderTest.getRentalPackageName(1L);
        assertThat(result).isEqualTo("result");
    }
}
