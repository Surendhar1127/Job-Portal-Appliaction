package com.star.Jobportal.Controller;

import com.star.Jobportal.Model.UserType;
import com.star.Jobportal.Model.Users;
import com.star.Jobportal.service.UserTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
@Controller
public class UserTypeController {

//    @Autowired
//    UserTypeService userTypeService;
//
//    @GetMapping("/register")
//    public String register(Model model){
//        List<UserType> userTypes=userTypeService.getAll();
//        System.out.println(userTypes);
//        model.addAttribute("getAllTypes",userTypes);
//        model.addAttribute("user",new Users());
//        return "register";
//    }
//@PostMapping("/register/new")
//    public String UserResgistration(@Valid Users users){
//        System.out.println("User::"+users);
//        return "dashboard";
//    }
}
