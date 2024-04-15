package fpt.edu.capstone.service.impl;

import fpt.edu.capstone.entity.Fields;
import fpt.edu.capstone.repository.FieldsRepository;
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
public class FieldsServiceImplTest {

    @Mock
    private FieldsRepository mockFieldsRepository;

    private FieldsServiceImpl fieldsServiceImplUnderTest;

    @Before
    public void setUp() throws Exception {
        fieldsServiceImplUnderTest = new FieldsServiceImpl(mockFieldsRepository);
    }

    @Test
    public void testGetAllField() {
        final List<Fields> list = Arrays.asList(new Fields(1L, "fieldName", "status"));
        when(mockFieldsRepository.getAllField()).thenReturn(list);
        final List<Fields> result = fieldsServiceImplUnderTest.getAllField();
    }

    @Test
    public void testGetAllField_FieldsRepositoryReturnsNoItems() {
        when(mockFieldsRepository.getAllField()).thenReturn(Collections.emptyList());
        final List<Fields> result = fieldsServiceImplUnderTest.getAllField();
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    public void testExistById() {
        when(mockFieldsRepository.existsById(1L)).thenReturn(false);
        final boolean result = fieldsServiceImplUnderTest.existById(1L);
        assertThat(result).isFalse();
    }

    @Test
    public void testGetById() {
        when(mockFieldsRepository.getById(1L)).thenReturn(new Fields(1L, "fieldName", "status"));
        final Fields result = fieldsServiceImplUnderTest.getById(1L);
    }

    @Test
    public void testFindById() {
        final Optional<Fields> fields = Optional.of(new Fields(1L, "fieldName", "status"));
        when(mockFieldsRepository.findById(1L)).thenReturn(fields);
        final Optional<Fields> result = fieldsServiceImplUnderTest.findById(1L);
    }

    @Test
    public void testFindById_FieldsRepositoryReturnsAbsent() {
        when(mockFieldsRepository.findById(1L)).thenReturn(Optional.empty());
        final Optional<Fields> result = fieldsServiceImplUnderTest.findById(1L);
        assertThat(result).isEmpty();
    }
    @Test
    public void testFindByName() {
        final Optional<Fields> fields = Optional.of(new Fields(1L, "fieldName", "status"));
        when(mockFieldsRepository.findByFieldName("name")).thenReturn(fields);
        final Optional<Fields> result = fieldsServiceImplUnderTest.findByName("name");
    }

    @Test
    public void testFindByName_FieldsRepositoryReturnsAbsent() {
        when(mockFieldsRepository.findByFieldName("name")).thenReturn(Optional.empty());
        final Optional<Fields> result = fieldsServiceImplUnderTest.findByName("name");
        assertThat(result).isEmpty();
    }
}
