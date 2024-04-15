package fpt.edu.capstone.repository;

import fpt.edu.capstone.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.lang.reflect.Field;
import java.util.List;

public interface MajorRepository extends JpaRepository<Major,Long> {
    @Query(value = "select m.major_name from Major m where m.id =:majorId", nativeQuery = true)
    String getNameByMajorId(@Param("majorId") long majorId);

    @Query(value = "select * from Major m where m.field_id = ?1", nativeQuery = true)
    List<Major> getAllMajorByFieldId(long fieldId);

    @Query(value = "select m.major_name from major m " +
            "left join major_level ml  on m.id = ml.major_id " +
            "left join cv c on c.id  = ml.cv_id " +
            "where c.id = ?1", nativeQuery = true)
    List <String> getMajorNameByCVId(long cvId);
}
