package com.star.Jobportal.repository;

import com.star.Jobportal.Model.IRecruiterJobs;
import com.star.Jobportal.Model.JobPostActivity;
import com.star.Jobportal.Model.JobSeekerApply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository

public interface JobPostActivityRepository extends JpaRepository<JobPostActivity,Integer> {

    @Query(value = "SELECT COUNT(s.user_id) AS totalCandidates, j.job_post_id, j.job_title, l.id AS locationId, l.city, l.state, l.country, c.id AS companyId, c.name " +
            "FROM job_post_activity j " +
            "INNER JOIN job_location l ON j.job_location_id = l.id " +
            "INNER JOIN job_company c ON j.job_company_id = c.id " +
            "LEFT JOIN job_seeker_apply s ON s.job = j.job_post_id " +
            "WHERE j.posted_by_id = :recruiter " +
            "GROUP BY j.job_post_id, j.job_title, l.id, l.city, l.state, l.country, c.id, c.name", nativeQuery = true)
    List<IRecruiterJobs> getRecruiterJobs(@Param("recruiter") int recruiter);



    @Query(value = "SELECT * FROM job_post_activity j " +
            "INNER JOIN job_location l ON j.job_location_id = l.id " +
            "WHERE j.job_title LIKE %:job% " +
            "AND (l.city LIKE %:location% " +
            "OR l.country LIKE %:location% " +
            "OR l.state LIKE %:location%) " +
            "AND j.job_type IN (:type) " +
            "AND j.remote IN (:remote)",
            nativeQuery = true)
    List<JobPostActivity> searchWithoutDate(@Param("job") String job,
                                            @Param("location") String location,
                                            @Param("remote") List<String> remote,
                                            @Param("type") List<String> type);
    @Query(value = "SELECT * FROM job_post_activity j " +
            "INNER JOIN job_location l ON j.job_location_id = l.id " +
            "WHERE j.job_title LIKE %:job% " +
            "AND (l.city LIKE %:location% " +
            "OR l.country LIKE %:location% " +
            "OR l.state LIKE %:location%) " +
            "AND j.job_type IN (:type) " +
            "AND j.remote IN (:remote) " +
            "AND j.posted_date >= :searchDate",
            nativeQuery = true)
    List<JobPostActivity> search(@Param("job") String job,
                                 @Param("location") String location,
                                 @Param("remote") List<String> remote,
                                 @Param("type") List<String> type,
                                 @Param("searchDate") LocalDate searchDate);

    @Query(value="SELECT * FROM job_post_activity j "+
            "INNER JOIN job_location l ON j.job_location_id = l.id "+
            "WHERE j.job_type IN (:type)",
    nativeQuery = true)
    List<JobPostActivity> findByJobType(@Param("type") List<String> type);


    //List<JobSeekerApply> findByJob(JobPostActivity job);
}
