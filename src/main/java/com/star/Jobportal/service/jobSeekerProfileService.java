package com.star.Jobportal.service;


import com.star.Jobportal.Model.JobSeekerProfile;
import com.star.Jobportal.Model.Users;
import com.star.Jobportal.repository.JobSeekerProfileRepository;
import com.star.Jobportal.repository.UsersRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class jobSeekerProfileService {
    @Autowired
    private JobSeekerProfileRepository jobSeekerProfileRepository;
    @Autowired
    private UsersRespository usersRespository;

    public Optional<JobSeekerProfile> getOne(int userId) {
        return jobSeekerProfileRepository.findById(userId);
    }

    public JobSeekerProfile addNew(JobSeekerProfile jobSeekerProfile) {
        return jobSeekerProfileRepository.save(jobSeekerProfile);
    }

    public JobSeekerProfile getCurrentRecruiterProfile() {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            String currentUser=authentication.getName();
            Users users=usersRespository.findByEmail(currentUser).orElseThrow(()->new UsernameNotFoundException("User not Found"));
            Optional<JobSeekerProfile> jobSeekerProfile=getOne(users.getUserId());
return jobSeekerProfile.orElse(null);
        }
        else return null;
    }
}
