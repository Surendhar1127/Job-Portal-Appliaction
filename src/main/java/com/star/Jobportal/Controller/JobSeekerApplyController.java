package com.star.Jobportal.Controller;

import com.star.Jobportal.Model.*;
import com.star.Jobportal.service.*;
import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class JobSeekerApplyController {
    @Autowired
    private UsersService usersService;
    @Autowired
    private JobPostActivityService jobPostActivityService;
    @Autowired
    private JobSeekerApplyService jobSeekerApplyService;
    @Autowired
    private JobSeekerSaveService jobSeekerSaveService;
    @Autowired
    private RecruiterProfileService recruiterProfileService;
    @Autowired
    private jobSeekerProfileService jobSeekerProfileService;


    @GetMapping("/job-details-apply/{id}")
    public String display(@PathVariable("id") int id, Model model){
        JobPostActivity jobPostActivity=jobPostActivityService.getOne(id);
        List<JobSeekerApply> jobSeekerApplyList=jobSeekerApplyService.getJobsCandidates(jobPostActivity);
        List<JobSeekerSave> jobSeekerSaveList=jobSeekerSaveService.getJobCandidates(jobPostActivity);

        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();

        if(!(authentication instanceof AnonymousAuthenticationToken)){
            if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))){
                RecruiterProfile user=recruiterProfileService.getCurrentRecruiterProfile();
                if(user!=null){
                    model.addAttribute("applyList",jobSeekerApplyList);
                }

            }
            else {
                JobSeekerProfile user=jobSeekerProfileService.getCurrentRecruiterProfile();
                if(user!=null){
                    boolean exist=false;
                    boolean saved=false;
                    for(JobSeekerApply jobSeekerApply:jobSeekerApplyList){
                        if(jobSeekerApply.getUserId().getUserAccountId()==user.getUserAccountId()){
                            exist=true;
                            break;
                        }
                    }

                    for(JobSeekerSave jobSeekerSave:jobSeekerSaveList){
                        if(jobSeekerSave.getUserId().getUserAccountId()==user.getUserAccountId()){
                            saved=true;
                            break;
                        }
                    }
                    model.addAttribute("alreadyApplied",exist);
                    model.addAttribute("alreadySaved",saved);
                }
            }
        }
        model.addAttribute("jobDetails",jobPostActivity);
        model.addAttribute("user",usersService.getCurrentUserProfileDetails());
        return "job-details";
    }


    @PostMapping("dashboard/edit/{id}")
    public String editJob(@PathVariable int id,Model model){
JobPostActivity jobPostActivity=jobPostActivityService.getOne(id);

model.addAttribute("jobPostActivity",jobPostActivity);
model.addAttribute("user",usersService.getCurrentUserProfileDetails());
return "add-jobs";
    }


    @PostMapping("/job-details/apply/{id}")
    public String apply(@PathVariable int id,JobSeekerApply jobSeekerApply){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            String currentUser=authentication.getName();
            Optional<Users> users=usersService.findByEmail(currentUser);
            Optional<JobSeekerProfile> jobSeekerProfile=jobSeekerProfileService.getOne(users.get().getUserId());
            JobPostActivity jobPostActivity=jobPostActivityService.getOne(id);
            if(jobSeekerProfile.isPresent()  && jobPostActivity!=null){
                jobSeekerApply = new JobSeekerApply();
jobSeekerApply.setUserId(jobSeekerProfile.get());
jobSeekerApply.setJob(jobPostActivity);
jobSeekerApply.setApplyDate(new Date());
            }else {
                throw new RuntimeException("User not found");
            }

            jobSeekerApplyService.addNew(jobSeekerApply);
        }
        return "redirect:/dashboard/";
    }
}
