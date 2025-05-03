package com.star.Jobportal.repository;

import com.star.Jobportal.Model.JobPostActivity;
import com.star.Jobportal.Model.JobSeekerApply;
import com.star.Jobportal.Model.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface JobSeekerApplyRepository extends JpaRepository<JobSeekerApply,Integer> {

    List<JobSeekerApply> findByUserId(JobSeekerProfile userId);

    List<JobSeekerApply> findByJob(JobPostActivity job);
}
