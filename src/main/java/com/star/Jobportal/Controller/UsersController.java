package com.star.Jobportal.Controller;

import com.star.Jobportal.Model.UserType;
import com.star.Jobportal.Model.Users;
import com.star.Jobportal.service.UserTypeService;
import com.star.Jobportal.service.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class UsersController {

    @Autowired
    UserTypeService userTypeService;
    @Autowired
    UsersService usersService;

    @GetMapping("/register")
    public String register(Model model){
        List<UserType> userTypes=userTypeService.getAll();
        System.out.println(userTypes);
        model.addAttribute("getAllTypes",userTypes);
        model.addAttribute("user",new Users());
        return "register";
    }
    @PostMapping("/register/new")
    public String UserResgistration(@Valid Users users,Model model){
        Optional<Users> optionalUsers=usersService.getUserByEmail(users.getEmail());

        if(optionalUsers.isPresent()){
            model.addAttribute("error","Email is Already Exist,try to login or register with other email  ");
            List<UserType> userTypes=userTypeService.getAll();
            System.out.println(userTypes);
            model.addAttribute("getAllTypes",userTypes);
            model.addAttribute("user",new Users());
            return "register";
        }
        System.out.println("User::"+users);
        usersService.newUser(users);
        return "redirect:/dashboard/";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null){
            new SecurityContextLogoutHandler().logout(request,response,authentication);
        }
        return "redirect:/";
    }
}
