package com.star.Jobportal.Controller;

import com.star.Jobportal.Model.RecruiterProfile;
import com.star.Jobportal.Model.Users;
import com.star.Jobportal.Util.FileUploadUtil;
import com.star.Jobportal.repository.UsersRespository;
import com.star.Jobportal.service.RecruiterProfileService;
import com.star.Jobportal.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/recruiter-profile")
public class RecruiterProfileController {
    @Autowired
    UsersService usersService;
    @Autowired
    UsersRespository usersRespository;
    @Autowired
    RecruiterProfileService recruiterProfileService;
    @Autowired
    FileUploadUtil fileUploadUtil;

    @GetMapping("/")
    public String recruiterProfile(Model model){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            String currentUser=authentication.getName();
            Users users=usersRespository.findByEmail(currentUser).orElseThrow(()
            -> new UsernameNotFoundException("User not found"));
            Optional<RecruiterProfile> recruiterProfile=recruiterProfileService.getOne(users.getUserId());
            if(!recruiterProfile.isEmpty()){
                model.addAttribute("profile",recruiterProfile.get());
            }
        }
        return "recruiter_profile";
    }

    @PostMapping("/addNew")

    public String addNew(RecruiterProfile recruiterProfile, @RequestParam("image") MultipartFile multipartFile,
                         Model model){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            String currentUser=authentication.getName();
            Users users=usersRespository.findByEmail(currentUser).orElseThrow(()
                    -> new UsernameNotFoundException("User not found"));
            recruiterProfile.setUserId(users);
            recruiterProfile.setUserAccountId(users.getUserId());
        }
        model.addAttribute("profile",recruiterProfile);

        String fileName="";
        if(!multipartFile.getOriginalFilename().equals("")){
            fileName= StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            System.out.println("filename:"+fileName);
            recruiterProfile.setProfilePhoto(fileName);
        }
RecruiterProfile saveUser=recruiterProfileService.addNew(recruiterProfile);

        String uploadDir="photos/recruiter"+saveUser.getUserAccountId();

        try{
FileUploadUtil.saveFile(uploadDir,fileName,multipartFile);
        }catch (Exception e){
            e.printStackTrace();
        }
return "redirect:/dashboard/";
    }

}
