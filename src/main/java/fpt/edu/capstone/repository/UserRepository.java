package fpt.edu.capstone.repository;

import fpt.edu.capstone.dto.register.CountRegisterUserResponse;
import fpt.edu.capstone.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);

    Optional<Users> findByEmail(String email);

    Optional <Users> findByUsernameAndIsDeleted(String account, int isDelete);

    @Query(value = "SELECT u FROM Users u where u.username =:username or u.email =:email")
    Optional <Users> checkExistedUserByUsernameOrEmail(@Param("username") String username,@Param("email") String email);

    @Query(value = "select * from users where id = ?", nativeQuery = true)
    Users getUserById(long userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE public.users SET avatar=?1 WHERE id=?2", nativeQuery = true)
    void updateAvatarUrl(String avatarId, long id);

    Users getByUsername(String username);

    Optional<Users> findByPhone(String phone);

    @Query(value = "select count(id) as numberUser, t1.role_id as roleId from " +
            "(select u.id, role_id, created_at from users u where date_trunc('day',created_at) = current_date) t1 " +
            "group by t1.role_id", nativeQuery = true)
    List<CountRegisterUserResponse> countUserRegisterToday();

    @Query(value = "select count(*) as numberUser, t1.role_id as roleId from " +
            "(select u.id, role_id from users u) t1 " +
            "group by t1.role_id", nativeQuery = true)
    List<CountRegisterUserResponse> countAllUsersRegister();

    @Query(value = "select count(id) as numberUser,  t1.role_id as roleId from " +
            "(select u.id, role_id, created_at from users u where created_at > current_date - interval '30' day ) t1 " +
            "group by t1.role_id", nativeQuery = true)
    List<CountRegisterUserResponse> countUserRegisterMonthAgo();

    @Query(value = "select * from users u where u.id= ?", nativeQuery = true)
    Users getById(long id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE public.users SET phone = ?1 WHERE id = ?2", nativeQuery = true)
    void updatePhoneNumber(String phone, long userId);

    Optional<Users> findByResetPasswordToken(String resetPasswordToken);

    @Transactional
    @Modifying
    @Query(value = "Update public.users set is_verified_phone = ?1 where id = ?2", nativeQuery = true)
    void updateIsVerifyPhone(boolean isVerifyPhone, long userId);

    @Query(value = "select * from users u where u.phone =:phone and u.id not in (:id) limit 1", nativeQuery = true)
    Users findByPhoneAndIdIsNotIn(@Param("phone") String phone, long id);

    Users findByUsernameAndEmail(String userName, String email);

    @Query("select s from Users s where s.username =:username or s.email =:username")
    Optional <Users> findUsersByUsernameOrEmail(@Param("username") String username);
}