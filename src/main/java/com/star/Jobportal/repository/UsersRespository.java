package com.star.Jobportal.repository;

import com.star.Jobportal.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRespository extends JpaRepository<Users,Integer> {

    Optional<Users> findByEmail(String email);
}
