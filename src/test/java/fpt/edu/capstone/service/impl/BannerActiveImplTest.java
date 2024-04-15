package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.dto.recruiter.BannerPositionDetailResponse;
import fpt.edu.capstone.entity.BannerActive;
import fpt.edu.capstone.exception.HiveConnectException;
import fpt.edu.capstone.repository.BannerActiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BannerActiveImplTest {

    @Mock
    private BannerActiveRepository mockBannerActiveRepository;

    private BannerActiveImpl bannerActiveImplUnderTest;

    @Before
    public void setUp() throws Exception {
        bannerActiveImplUnderTest = new BannerActiveImpl(mockBannerActiveRepository);
    }
    
    private BannerActive bannerActive (){
        BannerActive bannerActive = new BannerActive(1L, "imageUrl", 1L, "displayPosition", false,
                "approvalStatus", LocalDateTime.of(2021, 10, 1, 0, 0, 0));
        return bannerActive;
    }
    @Test
    public void testGetBannersByPosition() {
        final List<BannerActive> bannerActives = Arrays.asList(bannerActive());
        when(mockBannerActiveRepository.getBannerByPosition("displayPosition", "status")).thenReturn(bannerActives);
        final List<BannerActive> result = bannerActiveImplUnderTest.getBannersByPosition("displayPosition");
    }

    @Test
    public void testGetBannersByPosition_BannerActiveRepositoryReturnsNoItems() {
        when(mockBannerActiveRepository.getBannerByPosition("displayPosition", "status"))
                .thenReturn(Collections.emptyList());
        final List<BannerActive> result = bannerActiveImplUnderTest.getBannersByPosition("displayPosition");
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    public void testGetAllByPaymentId() {
        final List<BannerActive> bannerActives = Arrays.asList(bannerActive());
        when(mockBannerActiveRepository.getAllByPaymentId(1L)).thenReturn(bannerActives);
        final List<BannerActive> result = bannerActiveImplUnderTest.getAllByPaymentId(1L);
    }

    @Test
    public void testGetAllByPaymentId_BannerActiveRepositoryReturnsNoItems() {
        when(mockBannerActiveRepository.getAllByPaymentId(1L)).thenReturn(Collections.emptyList());
        final List<BannerActive> result = bannerActiveImplUnderTest.getAllByPaymentId(1L);
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    public void testGetAllBannerByPaymentId() {
        when(mockBannerActiveRepository.getBannerActiveByPaymentId(1L)).thenReturn(Arrays.asList());
        final List<BannerPositionDetailResponse> result = bannerActiveImplUnderTest.getAllBannerByPaymentId(1L);
    }

    @Test
    public void testGetAllBannerByPaymentId_BannerActiveRepositoryReturnsNoItems() {
        when(mockBannerActiveRepository.getBannerActiveByPaymentId(1L)).thenReturn(Collections.emptyList());
        final List<BannerPositionDetailResponse> result = bannerActiveImplUnderTest.getAllBannerByPaymentId(1L);
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    public void testFindByPaymentIdAndPosition() {
        final BannerActive bannerActive = new BannerActive(1L, "imageUrl", 1L, "displayPosition", false,
                "approvalStatus", LocalDateTime.of(2021, 10, 1, 0, 0, 0));
        when(mockBannerActiveRepository.findByPaymentIdAndDisplayPosition(1L, "position")).thenReturn(bannerActive);
        final BannerActive result = bannerActiveImplUnderTest.findByPaymentIdAndPosition(1L, "position");
    }

//    @Test
//    public void testGetAllBannerForApproval() {
//        final Page<BannerActive> bannerActives = new PageImpl<>(Arrays.asList(bannerActive()));
//        when(mockBannerActiveRepository.getAllBannerForApproval(any(Pageable.class),null,null,null)).thenReturn(bannerActives);
//        final Page<BannerActive> result = bannerActiveImplUnderTest.getAllBannerForApproval(PageRequest.of(1, 10),null,null,null);
//    }

    @Test
    public void testGetAllBannerForApproval_BannerActiveRepositoryReturnsNoItems() {
        when(mockBannerActiveRepository.getAllBannerForApproval(any(Pageable.class),null,null,null))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
        final Page<BannerActive> result = bannerActiveImplUnderTest.getAllBannerForApproval(PageRequest.of(1, 10),null,null,null);
    }

    @Test
    public void testFindById() throws Exception {
        final Optional<BannerActive> bannerActive = Optional.of(bannerActive());
        when(mockBannerActiveRepository.findById(1L)).thenReturn(bannerActive);
        final BannerActive result = bannerActiveImplUnderTest.findById(1L);
    }

    @Test
    public void testFindById_BannerActiveRepositoryReturnsAbsent() {
        when(mockBannerActiveRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bannerActiveImplUnderTest.findById(1L)).isInstanceOf(HiveConnectException.class);
    }

    @Test
    public void testGetAllBannerForApproval() {
        final Page<BannerActive> bannerActives = new PageImpl<>(Arrays.asList(bannerActive()));
        when(mockBannerActiveRepository.getAllBannerForApproval(any(Pageable.class), eq("screenName"),
                eq(LocalDateTime.of(2020, 1, 1, 0, 0, 0)), eq(LocalDateTime.of(2020, 1, 1, 0, 0, 0))))
                .thenReturn(bannerActives);

        final Page<BannerActive> result = bannerActiveImplUnderTest.getAllBannerForApproval(PageRequest.of(0, 1),
                "screenName", LocalDateTime.of(2020, 1, 1, 0, 0, 0), LocalDateTime.of(2020, 1, 1, 0, 0, 0));

    }
}
