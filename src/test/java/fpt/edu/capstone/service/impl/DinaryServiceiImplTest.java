package fpt.edu.capstone.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import fpt.edu.capstone.dto.UploadFileRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DinaryServiceiImplTest {

    @Mock
    private Cloudinary mockCloudinaryConfig;

    private DinaryServiceImpl dinaryServiceImplUnderTest;

    @Before
    public void setUp() throws Exception {
        dinaryServiceImplUnderTest = new DinaryServiceImpl(mockCloudinaryConfig);
    }

    @Test
    public void testUploadFile() {
        final MultipartFile file = new MockMultipartFile("name.xlsx", "Hello World".getBytes());
        final Uploader uploader = new Uploader(new Cloudinary(new HashMap<>()), null);
        when(mockCloudinaryConfig.uploader()).thenReturn(uploader);
        final String result = dinaryServiceImplUnderTest.uploadFile(file);
        assertThat(result).isEqualTo("result");
    }

    @Test
    public void testUploadFileToDinary() throws Exception {
        final UploadFileRequest request = new UploadFileRequest();
        request.setType("type");
        request.setUploadFileName("uploadFileName");
        request.setFile(null);
        request.setTypeUpload("typeUpload");

        final MultipartFile multipartFile = new MockMultipartFile("sourceFile.xlsx", "Hello World".getBytes());
        final Uploader uploader = new Uploader(new Cloudinary(new HashMap<>()), null);
        when(mockCloudinaryConfig.uploader()).thenReturn(uploader);
        final String result = dinaryServiceImplUnderTest.uploadFileToDinary(request, multipartFile);
        assertThat(result).isEqualTo("result");
    }

    @Test
    public void testUploadFileToDinary_ThrowsException() {
        final UploadFileRequest request = new UploadFileRequest();
        request.setType("type");
        request.setUploadFileName("uploadFileName");
        request.setFile(null);
        request.setTypeUpload("typeUpload");
        final MultipartFile multipartFile = new MockMultipartFile("sourceFile.tmp", "Hello World".getBytes());
        final Uploader uploader = new Uploader(new Cloudinary(new HashMap<>()), null);
        when(mockCloudinaryConfig.uploader()).thenReturn(uploader);
        assertThatThrownBy(() -> dinaryServiceImplUnderTest.uploadFileToDinary(request, multipartFile))
                .isInstanceOf(Exception.class);
    }
}
