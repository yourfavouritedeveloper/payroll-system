package org.example.employeems.repository;

import org.example.employeems.entity.Employee;
import org.example.employeems.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByEmployee(Employee employee);

    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
