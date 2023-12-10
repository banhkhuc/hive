package fpt.edu.capstone.repository;

import fpt.edu.capstone.dto.admin.AdminResponse;
import fpt.edu.capstone.dto.admin.user.AdminManageResponse;
import fpt.edu.capstone.entity.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    @Query("select a from Admin a")
    Page<Admin> getListAdminByFilter(Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO public.admins (user_id, full_name) VALUES(?1, ?2)", nativeQuery = true)
    void insertAdmin(long userId, String fullName);

    Optional <Admin> findByUserId(long userId);

    @Query(value = "select u.id as userId, u.username as userName, u.email as email, u.role_id as roleId, " +
            "r.name as roleName, u.is_deleted as isDeleted, u.last_login_time as lastLoginTime, " +
            "u.avatar as avatar, u.is_active as isActive, a.id as adminId, a.full_name as fullName, u.is_locked as isLocked " +
            "from users u join admins a on u.id = a.user_id " +
            "join roles r on u.role_id = r.id " +
            "where lower(u.username) like lower(concat('%',:username,'%')) and lower(u.email) like lower(concat('%',:email,'%')) " +
            "and a.full_name like lower(concat('%',:fullName,'%')) " +
            "and (u.id =:userId or 0=:userId) " +
            "and u.is_locked =:isLocked", nativeQuery = true)
    Page<AdminManageResponse> searchAdmin(Pageable pageable, @Param("username") String username,
                                          @Param("email") String email,@Param("fullName") String fullName,
                                          @Param("userId") long userId, @Param("isLocked") boolean isLocked);
}
