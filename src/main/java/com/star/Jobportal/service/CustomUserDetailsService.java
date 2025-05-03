package com.star.Jobportal.service;

import com.star.Jobportal.Model.Users;
import com.star.Jobportal.Util.CustomUserDetails;
import com.star.Jobportal.repository.UsersRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//This implementation is used to load user-specific data during authentication.
@Service
public class CustomUserDetailsService implements UserDetailsService {
@Autowired
    UsersRespository usersRespository;
    //This method is called by Spring Security to load user-specific data based on the provided username.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user=usersRespository.findByEmail(username).orElseThrow(()->
                new UsernameNotFoundException("Could not found user"));

        System.out.println("users:"+user);
        return new CustomUserDetails(user);
    }
}
