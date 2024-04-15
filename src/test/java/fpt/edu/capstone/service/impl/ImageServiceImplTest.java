package fpt.edu.capstone.service.impl;

import com.amazonaws.services.apigateway.model.Op;
import fpt.edu.capstone.entity.Image;
import fpt.edu.capstone.repository.ImageRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
public class ImageServiceImplTest {

    @Mock
    private ImageRepository mockImageRepository;

    private ImageServiceImpl imageServiceImplUnderTest;

    @Before
    public void setUp() throws Exception {
        imageServiceImplUnderTest = new ImageServiceImpl(mockImageRepository);
    }
    private Image imageEntity = new Image(1L, "name", "url", 1L,
            0, false, "contentType", "content".getBytes(), false);
    @Test
    public void testIsValidFile() {
        final MultipartFile file = new MockMultipartFile("sourceFile.tmp", "Hello World".getBytes());
        final boolean result = imageServiceImplUnderTest.isValidFile(file, "jpg");
        assertThat(result).isFalse();
    }

    @Test
    public void testSaveCompanyAvatar() throws Exception {
        final MultipartFile file = new MockMultipartFile("sourceFile.tmp", "Hello World".getBytes());
        final Image image = imageEntity;
        when(mockImageRepository.save(any(Image.class))).thenReturn(image);
        final Image result = imageServiceImplUnderTest.saveCompanyAvatar(file, "IMG", 1L);
    }

    @Test
    public void testSaveCompanyAvatar_ThrowsIOException() {
        final MultipartFile file = null;
        final Image image = imageEntity;
        when(mockImageRepository.save(any(Image.class))).thenReturn(image);
        assertThatThrownBy(() -> imageServiceImplUnderTest.saveCompanyAvatar(file, "jpg", 1L))
                .isInstanceOf(IOException.class);
    }

    @Test
    public void testFindAvatarByCompanyId() {
        final Optional<Image> image = Optional.of(imageEntity);
        when(mockImageRepository.findAvatarByCompanyId(1L)).thenReturn(image);
        final Optional<Image> result = imageServiceImplUnderTest.findAvatarByCompanyId(1L);
    }

    @Test
    public void testFindAvatarByCompanyId_ImageRepositoryReturnsAbsent() {
        when(mockImageRepository.findAvatarByCompanyId(1L)).thenReturn(Optional.empty());
        final Optional<Image> result = imageServiceImplUnderTest.findAvatarByCompanyId(1L);
        assertThat(result).isEmpty();
    }

    @Test
    public void testUpdateAvatar() throws Exception {
        final MultipartFile file = new MockMultipartFile("sourceFile.tmp", "Hello World".getBytes());
        imageServiceImplUnderTest.updateAvatar("id", file);
        verify(mockImageRepository).updateCompanyAvatar(any(byte[].class), eq(true), eq("id"));
    }

    @Test
    public void testUpdateAvatar_ThrowsIOException() {
        final MultipartFile file = null;
        assertThatThrownBy(() -> imageServiceImplUnderTest.updateAvatar("id", file)).isInstanceOf(IOException.class);
        verify(mockImageRepository).updateCompanyAvatar(any(byte[].class), eq(true), eq("id"));
    }

    @Test
    public void testFinById() {
        final Optional<Image> image = Optional.of(imageEntity);
        when(mockImageRepository.findById(1L)).thenReturn(image);
        final Optional<Image> result = imageServiceImplUnderTest.finById(1L);
    }

    @Test
    public void testFinById_ImageRepositoryReturnsAbsent() {
        when(mockImageRepository.findById(1L)).thenReturn(Optional.empty());
        final Optional<Image> result = imageServiceImplUnderTest.finById(1L);
        assertThat(result).isEmpty();
    }

    @Test
    public void testGetAvatarRecruiter() {
        final Image image = imageEntity;
        when(mockImageRepository.getAvatarRecruiter(1L)).thenReturn(image);
        final Image result = imageServiceImplUnderTest.getAvatarRecruiter(1L);
    }

    @Test
    public void testGetAvatarCandidate() {
        final Image image = imageEntity;
        when(mockImageRepository.getAvatarCandidate(1L)).thenReturn(image);
        final Image result = imageServiceImplUnderTest.getAvatarCandidate(1L);
    }

    @Test
    public void testGetImageCompany() {
        final Optional<Image> image = Optional.ofNullable(imageEntity);
        when(mockImageRepository.getImageCompany(1L, false)).thenReturn(image);
        final Optional<Image> result = imageServiceImplUnderTest.getImageCompany(1L, false);
    }

    @Test
    public void testDeleteImageById() {
        imageServiceImplUnderTest.deleteImageById(Arrays.asList(1L));
        verify(mockImageRepository).deleteImageById(Arrays.asList(1L));
    }

    @Test
    public void testSaveImageCompany() {
        final Image image = imageEntity;
        when(mockImageRepository.save(any(Image.class))).thenReturn(image);
        imageServiceImplUnderTest.saveImageCompany(false, false, 1L, Arrays.asList("value"));
        verify(mockImageRepository).save(any(Image.class));
    }

    @Test
    public void testGetCompanyImageList() {
        final List<Image> images = Arrays.asList(imageEntity);
        when(mockImageRepository.getCompanyImageList(1L, false, false)).thenReturn(images);
        final List<Image> result = imageServiceImplUnderTest.getCompanyImageList(1L, false, false);
    }

    @Test
    public void testGetCompanyImageList_ImageRepositoryReturnsNoItems() {
        when(mockImageRepository.getCompanyImageList(1L, false, false)).thenReturn(Collections.emptyList());
        final List<Image> result = imageServiceImplUnderTest.getCompanyImageList(1L, false, false);
        assertThat(result).isEqualTo(Collections.emptyList());
    }
}
