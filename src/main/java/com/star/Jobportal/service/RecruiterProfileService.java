package com.star.Jobportal.service;

import com.star.Jobportal.Model.RecruiterProfile;
import com.star.Jobportal.Model.Users;
import com.star.Jobportal.repository.RecruiterProfileRepository;
import com.star.Jobportal.repository.UsersRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RecruiterProfileService {
    @Autowired
    RecruiterProfileRepository recruiterProfileRepository;
    @Autowired
    private UsersRespository usersRespository;

    public Optional<RecruiterProfile> getOne(Integer id){
        return recruiterProfileRepository.findById(id);

    }


    public RecruiterProfile addNew(RecruiterProfile recruiterProfile) {
        return recruiterProfileRepository.save(recruiterProfile);
    }

    public RecruiterProfile getCurrentRecruiterProfile() {

        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            String currentUser=authentication.getName();
           Users users= usersRespository.findByEmail(currentUser).orElseThrow(()->
                    new UsernameNotFoundException("User not found"));

           Optional<RecruiterProfile> recruiterProfile=getOne(users.getUserId());
           return recruiterProfile.orElse(null);
        }
        else return null;
    }
}
