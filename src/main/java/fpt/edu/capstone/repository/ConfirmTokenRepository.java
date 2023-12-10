package fpt.edu.capstone.repository;

import fpt.edu.capstone.entity.ConfirmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmTokenRepository extends JpaRepository<ConfirmToken, Long> {
    ConfirmToken findByConfirmationToken(String confirmationToken);

    ConfirmToken getByUserId(long userId);

    ConfirmToken getByConfirmationToken(String token);
}
