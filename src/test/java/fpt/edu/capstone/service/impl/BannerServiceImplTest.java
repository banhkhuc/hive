package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.banner.ConfigBannerRequest;
import fpt.edu.capstone.dto.banner.UpdateBannerRequest;
import fpt.edu.capstone.entity.Banner;
import fpt.edu.capstone.entity.RentalPackage;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.repository.BannerRepository;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BannerServiceImplTest {

    @Mock
    private ModelMapper mockModelMapper;
    @Mock
    private BannerRepository mockBannerRepository;
    @Mock
    private RentalPackageService mockRentalPackageService;

    private BannerServiceImpl bannerServiceImplUnderTest;

    @Before
    public void setUp() throws Exception {
        bannerServiceImplUnderTest = new BannerServiceImpl(mockModelMapper, mockBannerRepository,
                mockRentalPackageService);
    }
    private Banner banner(){
        Banner banner = new Banner(1L, 123L, 12345678L, 1L, "timeExpired", "title", "description", "image", false, false, false, false,
                false, false, false, false);
        return banner;
    }

    private UpdateBannerRequest request(){
        UpdateBannerRequest request = new UpdateBannerRequest(1L, 1, 12345678L, 1L, "timeExpired", "titleNe",
                "description", "image", true, true, false, false, false, false, false);
        return request;
    }
    
    private ConfigBannerRequest bannerRequest(){
        ConfigBannerRequest bannerRequest = new ConfigBannerRequest(1, 12345678L, 1L, "timeExpired", "ten goi chua ton tai", "description",
                "image", true, false, false, false, false, false, false);
        return bannerRequest;
    }

    private RentalPackage rentalPackage(){
        RentalPackage rentalPackage = new RentalPackage();
        rentalPackage.setId(123L);
        rentalPackage.setPackageGroup("packageGroup");
        rentalPackage.setDescription("description");
        rentalPackage.setStatus(false);
        return rentalPackage;
    }
    @Test
    public void testGetAllBanner() {
        final List<Banner> bannerList = Arrays.asList(banner());
        when(mockBannerRepository.findAll()).thenReturn(bannerList);
        final List<Banner> result = bannerServiceImplUnderTest.getAllBanner();
    }

    @Test
    public void testGetAllBanner_BannerRepositoryReturnsNoItems() {
        when(mockBannerRepository.findAll()).thenReturn(Collections.emptyList());
        final List<Banner> result = bannerServiceImplUnderTest.getAllBanner();
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    public void testDeleteBanner() {
        final Optional<Banner> banner = Optional.of(banner());
        when(mockBannerRepository.findById(1L)).thenReturn(banner);
        final Banner banner1 = banner();
        when(mockBannerRepository.save(any(Banner.class))).thenReturn(banner1);
        bannerServiceImplUnderTest.deleteBanner(1L);
        verify(mockBannerRepository).save(any(Banner.class));
    }

    @Test
    public void testDeleteBanner_BannerRepositoryFindByIdReturnsAbsent() {
        when(mockBannerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bannerServiceImplUnderTest.deleteBanner(1L)).isInstanceOf(HiveConnectException.class);
    }

    @Test
    public void testFindById() throws Exception {
        final Optional<Banner> banner = Optional.of(banner());
        when(mockBannerRepository.findById(1L)).thenReturn(banner);
        final Banner result = bannerServiceImplUnderTest.findById(1L);
    }

    @Test
    public void testFindById_BannerRepositoryReturnsAbsent() {
        when(mockBannerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bannerServiceImplUnderTest.findById(1L)).isInstanceOf(HiveConnectException.class);
    }

    @Test
    public void testInsertBanner() {
        final ConfigBannerRequest request = bannerRequest();
        final Optional<RentalPackage> optional = Optional.of(
                new RentalPackage(1L, "packageGroup", "description", false));
        when(mockRentalPackageService.findById(1L)).thenReturn(optional);
        final List<Banner> bannerList = Arrays.asList(banner());
        when(mockBannerRepository.getBannersByTitle("title")).thenReturn(bannerList);
        final Banner banner = banner();
        when(mockModelMapper.map(any(Object.class), eq(Banner.class))).thenReturn(banner);
        final Banner banner1 = banner();
        when(mockBannerRepository.save(any(Banner.class))).thenReturn(banner1);
        final Optional<Banner> banner2 = Optional.of(banner());
        when(mockBannerRepository.findById(1L)).thenReturn(banner2);
        final Banner result = bannerServiceImplUnderTest.insertBanner(request);
        verify(mockBannerRepository).save(any(Banner.class));
    }

    @Test
    public void testInsertBanner_RentalPackageServiceReturnsAbsent() {
        final ConfigBannerRequest request = bannerRequest();
        when(mockRentalPackageService.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bannerServiceImplUnderTest.insertBanner(request))
                .isInstanceOf(HiveConnectException.class);
    }

    @Test
    public void testInsertBanner_BannerRepositoryGetBannersByTitleReturnsNull() {
        final ConfigBannerRequest request = bannerRequest();
        final Optional<RentalPackage> optional = Optional.of(rentalPackage());
        when(mockRentalPackageService.findById(1L)).thenReturn(optional);
        when(mockBannerRepository.getBannersByTitle("title")).thenReturn(null);
        final Banner banner = banner();
        when(mockModelMapper.map(any(Object.class), eq(Banner.class))).thenReturn(banner);
        final Banner banner1 = banner();
        when(mockBannerRepository.save(any(Banner.class))).thenReturn(banner1);
        final Optional<Banner> banner2 = Optional.of(banner());
        when(mockBannerRepository.findById(1L)).thenReturn(banner2);
        final Banner result = bannerServiceImplUnderTest.insertBanner(request);
        verify(mockBannerRepository).save(any(Banner.class));
    }

    @Test
    public void testInsertBanner_BannerRepositoryGetBannersByTitleReturnsNoItems() {
        final ConfigBannerRequest request = bannerRequest();
        final Optional<RentalPackage> optional = Optional.of(
                new RentalPackage(1L, "packageGroup", "description", false));
        when(mockRentalPackageService.findById(1L)).thenReturn(optional);
        when(mockBannerRepository.getBannersByTitle("title")).thenReturn(Collections.emptyList());
        final Banner banner = banner();
        when(mockModelMapper.map(any(Object.class), eq(Banner.class))).thenReturn(banner);
        final Banner banner1 = banner();
        when(mockBannerRepository.save(any(Banner.class))).thenReturn(banner1);
        final Optional<Banner> banner2 = Optional.of(banner());
        when(mockBannerRepository.findById(1L)).thenReturn(banner2);
        final Banner result = bannerServiceImplUnderTest.insertBanner(request);
        verify(mockBannerRepository).save(any(Banner.class));
    }

    @Test
    public void testInsertBanner_BannerRepositoryFindByIdReturnsAbsent() {
        final ConfigBannerRequest request = bannerRequest();
        final Optional<RentalPackage> optional = Optional.of(
                new RentalPackage(1L, "packageGroup", "description", false));
        when(mockRentalPackageService.findById(1L)).thenReturn(optional);
        final List<Banner> bannerList = Arrays.asList(banner());
        when(mockBannerRepository.getBannersByTitle("title")).thenReturn(bannerList);
        final Banner banner = banner();
        when(mockModelMapper.map(any(Object.class), eq(Banner.class))).thenReturn(banner);
        final Banner banner1 = banner();
        when(mockBannerRepository.save(any(Banner.class))).thenReturn(banner1);
        when(mockBannerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bannerServiceImplUnderTest.insertBanner(request))
                .isInstanceOf(HiveConnectException.class);
        verify(mockBannerRepository).save(any(Banner.class));
    }

    @Test
    public void testUpdateBanner() {
        final UpdateBannerRequest request = request();
        final Optional<Banner> banner = Optional.of(banner());
        when(mockBannerRepository.findById(1L)).thenReturn(banner);
        final Optional<RentalPackage> optional = Optional.of(
                new RentalPackage(1L, "packageGroup", "description", false));
        when(mockRentalPackageService.findById(1L)).thenReturn(optional);
        final List<Banner> bannerList = Arrays.asList(banner());
        when(mockBannerRepository.checkExistBannerByTitle("title", 1L)).thenReturn(bannerList);
        final Banner banner1 = banner();
        when(mockBannerRepository.save(any(Banner.class))).thenReturn(banner1);
        bannerServiceImplUnderTest.updateBanner(request);
        verify(mockBannerRepository).save(any(Banner.class));
    }

    @Test
    public void testUpdateBanner_BannerRepositoryFindByIdReturnsAbsent() {
        final UpdateBannerRequest request = request();
        when(mockBannerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bannerServiceImplUnderTest.updateBanner(request))
                .isInstanceOf(HiveConnectException.class);
    }

    @Test
    public void testUpdateBanner_RentalPackageServiceReturnsAbsent() {
        final UpdateBannerRequest request = request();
        final Optional<Banner> banner = Optional.of(banner());
        when(mockBannerRepository.findById(1L)).thenReturn(banner);
        when(mockRentalPackageService.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bannerServiceImplUnderTest.updateBanner(request))
                .isInstanceOf(HiveConnectException.class);
    }

    @Test
    public void testUpdateBanner_BannerRepositoryCheckExistBannerByTitleReturnsNull() {
        final UpdateBannerRequest request = request();
        final Optional<Banner> banner = Optional.of(banner());
        when(mockBannerRepository.findById(1L)).thenReturn(banner);
        final Optional<RentalPackage> optional = Optional.of(
                new RentalPackage(1L, "packageGroup", "description", false));
        when(mockRentalPackageService.findById(1L)).thenReturn(optional);
        when(mockBannerRepository.checkExistBannerByTitle("title", 1L)).thenReturn(null);
        final Banner banner1 = banner();
        when(mockBannerRepository.save(any(Banner.class))).thenReturn(banner1);
        bannerServiceImplUnderTest.updateBanner(request);
        verify(mockBannerRepository).save(any(Banner.class));
    }

    @Test
    public void testUpdateBanner_BannerRepositoryCheckExistBannerByTitleReturnsNoItems() {
        final UpdateBannerRequest request = request();
        final Optional<Banner> banner = Optional.of(banner());
        when(mockBannerRepository.findById(1L)).thenReturn(banner);
        final Optional<RentalPackage> optional = Optional.of(
                new RentalPackage(1L, "packageGroup", "description", false));
        when(mockRentalPackageService.findById(1L)).thenReturn(optional);
        when(mockBannerRepository.checkExistBannerByTitle("title", 1L)).thenReturn(Collections.emptyList());
        final Banner banner1 = banner();
        when(mockBannerRepository.save(any(Banner.class))).thenReturn(banner1);
        bannerServiceImplUnderTest.updateBanner(request);
        verify(mockBannerRepository).save(any(Banner.class));
    }

    @Test
    public void testGetBannerByFilter_BannerRepositoryReturnsNoItems() {
        when(mockBannerRepository.getBannerByFilter(any(Pageable.class), eq("title"), eq("timeExpired"),eq("benefit") ,eq(3L), eq(false)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
        final ResponseDataPagination result = bannerServiceImplUnderTest.getBannerByFilter(1, 10, "title", false, "timeExpired","benefit");
    }

    @Test
    public void testGetListFilter_BannerRepositoryReturnsNoItems() {
        when(mockBannerRepository.getBannerByFilter(any(Pageable.class), eq("name"),eq("timeExpired"),eq("benefit"), eq(1L), eq(false)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
        final Page<Banner> result = bannerServiceImplUnderTest.getListFilter(PageRequest.of(1, 10), "name","","", 1L, false);
    }

    @Test
    public void testFindByRentalPackageIdAndId() {
        final Banner banner = banner();
        when(mockBannerRepository.findByRentalPackageIdAndId(1L, 1L)).thenReturn(banner);
        final Banner result = bannerServiceImplUnderTest.findByRentalPackageIdAndId(1L, 1L);
    }

    @Test
    public void testGetBannerByFilter() {
        final Page<Banner> banners = new PageImpl<>(Arrays.asList(banner()));
        when(mockBannerRepository.getBannerByFilter(any(Pageable.class), eq("title"), eq("timeExpired"), eq("section"),
                eq(3L), eq(false))).thenReturn(banners);
        final ResponseDataPagination result = bannerServiceImplUnderTest.getBannerByFilter(1, 10, "title", false,
                "timeExpired", "section");
    }

    @Test
    public void testGetListFilter() {
        final Page<Banner> banners = new PageImpl<>(Arrays.asList(banner()));
        when(mockBannerRepository.getBannerByFilter(any(Pageable.class), eq("name"), eq("timeExpired"), eq("benefit"),
                eq(0L), eq(false))).thenReturn(banners);
        final Page<Banner> result = bannerServiceImplUnderTest.getListFilter(PageRequest.of(1, 10), "name",
                "timeExpired", "benefit", 0L, false);
    }
}
