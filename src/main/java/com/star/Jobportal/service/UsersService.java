package com.star.Jobportal.service;

import com.star.Jobportal.Model.JobSeekerProfile;
import com.star.Jobportal.Model.RecruiterProfile;
import com.star.Jobportal.Model.Users;
import com.star.Jobportal.repository.JobSeekerProfileRepository;
import com.star.Jobportal.repository.RecruiterProfileRepository;
import com.star.Jobportal.repository.UsersRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UsersService {
@Autowired
   private UsersRespository usersRespository;
    @Autowired
private JobSeekerProfileRepository jobSeekerProfileRepository;
    @Autowired
private RecruiterProfileRepository recruiterProfileRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

public Users newUser(Users users){
    users.setActice(true);
    users.setRegistrationDate(new Date(System.currentTimeMillis()));
    users.setPassword(passwordEncoder.encode(users.getPassword()));
    System.out.println("Register Date:"+ users.getRegistrationDate(new Date(System.currentTimeMillis())));
    Users savedUser=usersRespository.save(users);

    int userTypeId=users.getUserTypeId().getUserTypeId();
    if(userTypeId==1){
        recruiterProfileRepository.save(new RecruiterProfile(savedUser));
    }else {
        jobSeekerProfileRepository.save(new JobSeekerProfile(savedUser));
    }
    return savedUser;
}

public Optional<Users> getUserByEmail(String email){
    return usersRespository.findByEmail(email);
}

    public Object getCurrentUserProfileDetails() {

        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            String username=authentication.getName();
            Users users=usersRespository.findByEmail(username).orElseThrow(()->
                    new UsernameNotFoundException("User not found"));

            int userId=users.getUserId();

            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
                RecruiterProfile recruiterProfile=recruiterProfileRepository.findById(userId)
                        .orElse(new RecruiterProfile());
                return recruiterProfile;
            }else{
                JobSeekerProfile jobSeekerProfile=jobSeekerProfileRepository.findById(userId)
                        .orElse(new JobSeekerProfile());
                return jobSeekerProfile;

            }
        }
        return null;
    }

    public Users getCurrentUser() {
    Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
    if(!(authentication instanceof AnonymousAuthenticationToken)){
        String username=authentication.getName();
        Users users=usersRespository.findByEmail(username).orElseThrow(()->
                new UsernameNotFoundException("User not found"));
        return users;
    }
    return null;

    }

    public Optional<Users> findByEmail(String currentUser) {
    return Optional.ofNullable(usersRespository.findByEmail(currentUser).orElseThrow(() -> new UsernameNotFoundException("user not found")));
    }
}
