package com.star.Jobportal.repository;

import com.star.Jobportal.Model.JobPostActivity;
import com.star.Jobportal.Model.JobSeekerProfile;
import com.star.Jobportal.Model.JobSeekerSave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobSeekerSaveRepository extends JpaRepository<JobSeekerSave,Integer> {

    List<JobSeekerSave> findByUserId(JobSeekerProfile userId);

    List<JobSeekerSave> findByJob(JobPostActivity job);
}
