package com.star.Jobportal.service;

import com.star.Jobportal.Model.UserType;
import com.star.Jobportal.Model.Users;
import com.star.Jobportal.repository.UserTypeRespository;
import com.star.Jobportal.repository.UsersRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserTypeService {
@Autowired
    UserTypeRespository userTypeRespository;

public List<UserType> getAll(){
//    try {
//        return new ResponseEntity<>(usersRespository.findAll(), HttpStatus.OK);
//    }catch (Exception e){
//        e.getStackTrace();
//    }
System.out.println(userTypeRespository.findAll());
    return userTypeRespository.findAll();
}


}
