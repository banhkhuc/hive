package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.detail_package.CreatePackageRequest;
import fpt.edu.capstone.dto.detail_package.DetailPackageResponse;
import fpt.edu.capstone.entity.Banner;
import fpt.edu.capstone.entity.DetailPackage;
import fpt.edu.capstone.entity.RentalPackage;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.repository.DetailPackageRepository;
import fpt.edu.capstone.service.BannerService;
import fpt.edu.capstone.service.RentalPackageService;
import fpt.edu.capstone.utils.ResponseDataPagination;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DetailPackageServiceImplTest {

    @Mock
    private ModelMapper mockModelMapper;
    @Mock
    private DetailPackageRepository mockDetailPackageRepository;
    @Mock
    private RentalPackageService mockRentalPackageService;
    @Mock
    private BannerService mockBannerService;

    private DetailPackageServiceImpl detailPackageServiceImplUnderTest;

    @Before
    public void setUp() throws Exception {
        detailPackageServiceImplUnderTest = new DetailPackageServiceImpl(mockModelMapper, mockDetailPackageRepository,
                mockRentalPackageService, mockBannerService);
    }
    
    private DetailPackage detailPackage(){
        DetailPackage detailPackage = new DetailPackage(1L, 1L, "detailNamePackage", 12345L,
                123L, "timeExpired", "description", false, false, false, 100,false,false, false);
        return detailPackage;
    }
    
    private CreatePackageRequest packageRequest = new CreatePackageRequest(1L, "detailNameTest", 12345L,
            123L, "timeExpired", "description", false, false, 100, false, false, false);
    
    private Banner banner(){
        Banner banner = new Banner(1L, 1L, 12345L, 123L, "timeExpired",
                "title", "description", "image", false, false, 
                false, false, false, false, false, false);
        return banner;
    }

    @Test
    public void testGetListDetailPackageFilter_DetailPackageRepositoryReturnsNoItems() {
        when(mockDetailPackageRepository.getListFilter(any(Pageable.class), eq("name"),eq("timeExpired"),eq("benefit"), eq(1L), eq(false)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
        final Page<Banner> bannerPage = new PageImpl<>(Arrays.asList(banner()));
        when(mockBannerService.getListFilter(any(Pageable.class), eq("name"),eq("timeExpired"),eq("benefit"), eq(1L), eq(false)))
                .thenReturn(bannerPage);
        final ResponseDataPagination result = detailPackageServiceImplUnderTest.getListDetailPackageFilter(1, 10, "name",
                "timeExpired","benefit",  1L, false);
    }

    @Test
    public void testGetListDetailPackageFilter_BannerServiceReturnsNoItems() {
        final Page<DetailPackage> packagePage = new PageImpl<>(Arrays.asList(detailPackage()));
        when(mockDetailPackageRepository.getListFilter(any(Pageable.class), eq("name"),eq("timeExpired"),eq("benefit"), eq(1L), eq(false)))
                .thenReturn(packagePage);
        when(mockBannerService.getListFilter(any(Pageable.class), eq("name"),eq("timeExpired"),eq("benefit"), eq(1L), eq(false)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
        final ResponseDataPagination result = detailPackageServiceImplUnderTest.getListDetailPackageFilter(1, 10, "name",
                "timeExpired","benefit",  1L, false);
    }

    @Test
    public void testFindById() {
        final Optional<DetailPackage> detailPackage = Optional.of(detailPackage());
        when(mockDetailPackageRepository.findById(1L)).thenReturn(detailPackage);
        final DetailPackage result = detailPackageServiceImplUnderTest.findById(1L);
    }

    @Test
    public void testFindById_DetailPackageRepositoryReturnsAbsent() {
        when(mockDetailPackageRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> detailPackageServiceImplUnderTest.findById(1L))
                .isInstanceOf(HiveConnectException.class);
    }

    @Test
    public void testUpdateDetailPackage() {
        final DetailPackage detailPackage = detailPackage();
        final Optional<DetailPackage> detailPackage1 = Optional.of(detailPackage());
        when(mockDetailPackageRepository.checkExistByDetailName("detailName", 1L)).thenReturn(detailPackage1);
        final Optional<DetailPackage> detailPackage2 = Optional.of(detailPackage());
        when(mockDetailPackageRepository.findById(1L)).thenReturn(detailPackage2);
        final DetailPackage detailPackage3 = detailPackage();
        when(mockDetailPackageRepository.saveAndFlush(any(DetailPackage.class))).thenReturn(detailPackage3);
        detailPackageServiceImplUnderTest.updateDetailPackage(detailPackage);
        verify(mockDetailPackageRepository).saveAndFlush(any(DetailPackage.class));
    }

    @Test
    public void testUpdateDetailPackage_DetailPackageRepositoryCheckExistByDetailNameReturnsAbsent() {
        final DetailPackage detailPackage = detailPackage();
        when(mockDetailPackageRepository.checkExistByDetailName("detailName", 1L)).thenReturn(Optional.empty());
        final Optional<DetailPackage> detailPackage1 = Optional.of(detailPackage());
        when(mockDetailPackageRepository.findById(1L)).thenReturn(detailPackage1);
        final DetailPackage detailPackage2 = detailPackage();
        when(mockDetailPackageRepository.saveAndFlush(any(DetailPackage.class))).thenReturn(detailPackage2);
        detailPackageServiceImplUnderTest.updateDetailPackage(detailPackage);
        verify(mockDetailPackageRepository).saveAndFlush(any(DetailPackage.class));
    }

    @Test
    public void testUpdateDetailPackage_DetailPackageRepositoryFindByIdReturnsAbsent() {
        final DetailPackage detailPackage = detailPackage();
        final Optional<DetailPackage> detailPackage1 = Optional.of(detailPackage());
        when(mockDetailPackageRepository.checkExistByDetailName("detailName", 1L)).thenReturn(detailPackage1);
        when(mockDetailPackageRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> detailPackageServiceImplUnderTest.updateDetailPackage(detailPackage))
                .isInstanceOf(HiveConnectException.class);
    }

    @Test
    public void testCreateNormalPackage() {
        final CreatePackageRequest request = packageRequest;
        final Optional<RentalPackage> optional = Optional.of(
                new RentalPackage(1L, "packageGroup", "description", false));
        when(mockRentalPackageService.findById(1L)).thenReturn(optional);
        final Optional<DetailPackage> detailPackage = Optional.of(detailPackage());
        when(mockDetailPackageRepository.findByDetailName("detailName")).thenReturn(detailPackage);
        final DetailPackage detailPackage1 = detailPackage();
        when(mockModelMapper.map(any(Object.class), eq(DetailPackage.class))).thenReturn(detailPackage1);
        final DetailPackage detailPackage2 = detailPackage();
        when(mockDetailPackageRepository.save(any(DetailPackage.class))).thenReturn(detailPackage2);
        final Optional<DetailPackage> detailPackage3 = Optional.of(detailPackage());
        when(mockDetailPackageRepository.findById(1L)).thenReturn(detailPackage3);
        detailPackageServiceImplUnderTest.createNormalPackage(request);
        verify(mockDetailPackageRepository).save(any(DetailPackage.class));
    }

    @Test
    public void testCreateNormalPackage_RentalPackageServiceReturnsAbsent() {
        final CreatePackageRequest request = packageRequest;
        when(mockRentalPackageService.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> detailPackageServiceImplUnderTest.createNormalPackage(request))
                .isInstanceOf(HiveConnectException.class);
    }

    @Test
    public void testCreateNormalPackage_DetailPackageRepositoryFindByDetailNameReturnsAbsent() {
        final CreatePackageRequest request = packageRequest;
        final Optional<RentalPackage> optional = Optional.of(
                new RentalPackage(1L, "packageGroup", "description", false));
        when(mockRentalPackageService.findById(1L)).thenReturn(optional);
        when(mockDetailPackageRepository.findByDetailName("detailName")).thenReturn(Optional.empty());
        final DetailPackage detailPackage = detailPackage();
        when(mockModelMapper.map(any(Object.class), eq(DetailPackage.class))).thenReturn(detailPackage);
        final DetailPackage detailPackage1 = detailPackage();
        when(mockDetailPackageRepository.save(any(DetailPackage.class))).thenReturn(detailPackage1);
        final Optional<DetailPackage> detailPackage2 = Optional.of(detailPackage());
        when(mockDetailPackageRepository.findById(1L)).thenReturn(detailPackage2);
        detailPackageServiceImplUnderTest.createNormalPackage(request);
        verify(mockDetailPackageRepository).save(any(DetailPackage.class));
    }

    @Test
    public void testCreateNormalPackage_DetailPackageRepositoryFindByIdReturnsAbsent() {
        final CreatePackageRequest request = packageRequest;
        final Optional<RentalPackage> optional = Optional.of(
                new RentalPackage(1L, "packageGroup", "description", false));
        when(mockRentalPackageService.findById(1L)).thenReturn(optional);
        final Optional<DetailPackage> detailPackage = Optional.of(detailPackage());
        when(mockDetailPackageRepository.findByDetailName("detailName")).thenReturn(detailPackage);
        final DetailPackage detailPackage1 = detailPackage();
        when(mockModelMapper.map(any(Object.class), eq(DetailPackage.class))).thenReturn(detailPackage1);
        final DetailPackage detailPackage2 = detailPackage();
        when(mockDetailPackageRepository.save(any(DetailPackage.class))).thenReturn(detailPackage2);
        when(mockDetailPackageRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> detailPackageServiceImplUnderTest.createNormalPackage(request))
                .isInstanceOf(HiveConnectException.class);
        verify(mockDetailPackageRepository).save(any(DetailPackage.class));
    }

    @Test
    public void testDeleteDetailPackage() {
        final Optional<DetailPackage> detailPackage = Optional.of(detailPackage());
        when(mockDetailPackageRepository.findById(1L)).thenReturn(detailPackage);
        final DetailPackage detailPackage1 = detailPackage();
        when(mockDetailPackageRepository.save(any(DetailPackage.class))).thenReturn(detailPackage1);
        detailPackageServiceImplUnderTest.deleteDetailPackage(1L);
        verify(mockDetailPackageRepository).save(any(DetailPackage.class));
    }

    @Test
    public void testDeleteDetailPackage_DetailPackageRepositoryFindByIdReturnsAbsent() {
        when(mockDetailPackageRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> detailPackageServiceImplUnderTest.deleteDetailPackage(1L))
                .isInstanceOf(HiveConnectException.class);
    }

    @Test
    public void testGetDetailPackageInfor() {
        final DetailPackage detailPackage = detailPackage();
        when(mockDetailPackageRepository.findByRentalPackageIdAndId(1L, 1L)).thenReturn(detailPackage);
        final Banner banner = banner();
        final DetailPackageResponse result = detailPackageServiceImplUnderTest.getDetailPackageInfor(1L, 1L);
    }

    @Test
    public void testGetListDetailPackageFilter() {
        final Page<DetailPackage> detailPackages = new PageImpl<>(Arrays.asList(detailPackage()));
        when(mockDetailPackageRepository.getListFilter(any(Pageable.class), eq("name"), eq("timeExpired"),
                eq("benefit"), eq(1L), eq(false))).thenReturn(detailPackages);
        final Page<Banner> banners = new PageImpl<>(Arrays.asList(banner()));
        when(mockBannerService.getListFilter(any(Pageable.class), eq("name"), eq("timeExpired"), eq("benefit"), eq(1L),
                eq(false))).thenReturn(banners);
        final ResponseDataPagination result = detailPackageServiceImplUnderTest.getListDetailPackageFilter(1, 10, "name",
                "timeExpired", "benefit", 1L, false);
    }
}
