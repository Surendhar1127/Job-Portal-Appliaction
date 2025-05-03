package com.star.Jobportal.Controller;

import com.star.Jobportal.Model.JobPostActivity;
import com.star.Jobportal.Model.JobSeekerProfile;
import com.star.Jobportal.Model.JobSeekerSave;
import com.star.Jobportal.Model.Users;
import com.star.Jobportal.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class JobSeekerSaveController {
    @Autowired
    private jobSeekerProfileService JobSeekerProfileService;

    @Autowired
    private UsersService usersService;
    @Autowired
    private JobSeekerSaveService jobSeekerSaveService;
    @Autowired
    private JobPostActivityService jobPostActivityService;
    @Autowired
    private JobSeekerApplyService jobSeekerApplyService;


    @PostMapping("job-details/save/{id}")
    public String saveJob(@PathVariable("id") int id, JobSeekerSave jobSeekerSave){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            String currentUser=authentication.getName();
            Optional<Users> user=usersService.findByEmail(currentUser);
            Optional<JobSeekerProfile> jobSeekerProfile=JobSeekerProfileService.getOne(user.get().getUserId());
            JobPostActivity jobPostActivity=jobPostActivityService.getOne(id);
            if(jobSeekerProfile.isPresent() && jobPostActivity!=null){
                jobSeekerSave.setJob(jobPostActivity);
                jobSeekerSave.setUserId(jobSeekerProfile.get());

            }
            else {
                throw new RuntimeException("User not found");
            }
            jobSeekerSaveService.addNew(jobSeekerSave);
        }
        return "redirect:/dashboard/";
    }

    @GetMapping("saved-jobs/")
    public String SavedJobs(Model model){
        List<JobPostActivity> jobPostActivities=new ArrayList<>();
        Object currentUserProfile=usersService.getCurrentUserProfileDetails();
        List<JobSeekerSave> jobSeekerSaveList=jobSeekerSaveService.getCandidatesJob((JobSeekerProfile) currentUserProfile);

        for(JobSeekerSave jobSeekerSave:jobSeekerSaveList){
            System.out.println("jobPostActivities.add(jobSeekerSave.getJob()):"+jobSeekerSave);
            jobPostActivities.add(jobSeekerSave.getJob());
        }
model.addAttribute("jobPost",jobPostActivities);
        model.addAttribute("user",currentUserProfile);

        return "saved-jobs";

    }
}
