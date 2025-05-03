package com.star.Jobportal.service;

import com.star.Jobportal.Model.*;
import com.star.Jobportal.repository.JobPostActivityRepository;
import com.star.Jobportal.repository.JobSeekerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class JobPostActivityService {
    @Autowired
private JobPostActivityRepository jobPostActivityRepository;
    public JobPostActivity addNew(JobPostActivity jobPostActivity){
        return jobPostActivityRepository.save(jobPostActivity);
    }

    public List<RecruiterJobsDto> getRecruiterJobs(int recruiter){
List<IRecruiterJobs> recruiterJobsDtos=jobPostActivityRepository.getRecruiterJobs(recruiter);

List<RecruiterJobsDto> recruiterJobsDtosList=new ArrayList<>();
for(IRecruiterJobs rec:recruiterJobsDtos){
    JobLocation jobLocation=new JobLocation(rec.getLocationId(),rec.getCity(),rec.getState(),rec.getCity());
    JobCompany jobCompany=new JobCompany(rec.getCompanyId(),rec.getName(),"");
    recruiterJobsDtosList.add(new RecruiterJobsDto(rec.getTotalCandidates(),rec.getJob_post_id(),rec.getJob_title(), jobLocation, jobCompany));
}
return recruiterJobsDtosList;
    }

    public JobPostActivity getOne(int id) {

        return jobPostActivityRepository.findById(id).orElseThrow(()->new RuntimeException("Job not Found"));

    }

    public List<JobPostActivity> getAll() {
List<JobPostActivity> findAll=jobPostActivityRepository.findAll();
        System.out.println("findAll"+findAll);
        return jobPostActivityRepository.findAll();

    }

    public List<JobPostActivity> search(String job, String location, List<String> type, List<String> remote, LocalDate searchDate) {
        List<JobPostActivity> findByDate=  (Objects.isNull(searchDate)?jobPostActivityRepository.searchWithoutDate(job,location,remote,type):
                        jobPostActivityRepository.search(job,location,remote,type,searchDate));
        System.out.println("findByDate"+findByDate);
        return Objects.isNull(searchDate)?jobPostActivityRepository.searchWithoutDate(job,location,remote,type):
                jobPostActivityRepository.search(job,location,remote,type,searchDate);
    }


    public List<JobPostActivity> findByType(List<String> type) {
        return jobPostActivityRepository.findByJobType(type);
    }
}
