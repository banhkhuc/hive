package fpt.edu.capstone.repository;

import fpt.edu.capstone.entity.Fields;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public interface FieldsRepository extends JpaRepository<Fields, Long> {
    @Query(value = "SELECT * FROM public.fields", nativeQuery = true)
    List<Fields> getAllField();

    @Query(value = "select * from fields where id = ?", nativeQuery = true)
    Fields getById(long id);

    Optional<Fields> findById(long id);

    Optional<Fields> findByFieldName(String name);
}
