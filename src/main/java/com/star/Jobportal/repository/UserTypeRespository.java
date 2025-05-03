package com.star.Jobportal.repository;

import com.star.Jobportal.Model.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface UserTypeRespository extends JpaRepository<UserType,Integer> {
}
