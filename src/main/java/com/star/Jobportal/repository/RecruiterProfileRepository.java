package com.star.Jobportal.repository;

import com.star.Jobportal.Model.RecruiterProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface RecruiterProfileRepository extends JpaRepository<RecruiterProfile,Integer> {
}
