package com.star.Jobportal.Controller;

import com.star.Jobportal.Model.*;
import com.star.Jobportal.service.JobPostActivityService;
import com.star.Jobportal.service.JobSeekerApplyService;
import com.star.Jobportal.service.JobSeekerSaveService;
import com.star.Jobportal.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Controller
public class JobPostActivityController {
    @Autowired
    private UsersService usersService;
    @Autowired
    private JobPostActivityService jobPostActivityService;
@Autowired
    private JobSeekerApplyService jobSeekerApplyService;
@Autowired
private JobSeekerSaveService jobSeekerSaveService;

@GetMapping("/dashboard/")
    public String SearchJobs(Model model, @RequestParam(value = "job",required = false) String job,
                             @RequestParam(value = "location",required = false) String location,
                             @RequestParam(value = "partTime",required = false) String partTime,
                             @RequestParam(value = "fullTime",required = false) String fullTime,
                             @RequestParam(value = "freelance",required = false) String freelance,
                             @RequestParam(value = "remoteOnly",required = false) String remoteOnly,
                             @RequestParam(value = "officeOnly",required = false) String officeOnly,
                             @RequestParam(value = "partialRemote",required = false) String partialRemote,
                             @RequestParam(value = "today",required = false) boolean today,
                             @RequestParam(value = "days7",required = false) boolean days7,
                             @RequestParam(value = "days30",required = false) boolean days30
                             ){
//Checking or getting user chose to fetch data accordingly
    model.addAttribute("partTime", Objects.equals(partTime,"Part-Time"));
    model.addAttribute("fullTime", Objects.equals(fullTime,"Full-Time"));
    model.addAttribute("freelance", Objects.equals(freelance,"Freelance"));

    model.addAttribute("remoteOnly", Objects.equals(remoteOnly,"Remote-Only"));
    model.addAttribute("officeOnly", Objects.equals(officeOnly,"Office-Only"));
    model.addAttribute("partialRemote", Objects.equals(partialRemote,"Partial-Remote"));

    model.addAttribute("today",today );
    model.addAttribute("days7", days7);
    model.addAttribute("days30", days30);

    model.addAttribute("job", job);
    model.addAttribute("location", location);

    LocalDate searchDate=null;
    List<JobPostActivity> jobPost=null;
    boolean dateSearchFlag=true;
    boolean remote=true;
    boolean type=true;
System.out.println("Type"+fullTime);
    if(days30){
        searchDate=LocalDate.now().minusDays(30);
    }
    else if (days7){
        searchDate=LocalDate.now().minusDays(7);
    }
    else if (today){
        searchDate=LocalDate.now();
    }
    else {
        dateSearchFlag=false;
    }

    if(partTime==null && fullTime==null && freelance==null){
        partTime="Part-Time";
        fullTime="Full-Time";
        freelance="Freelance";
        remote=false;
    }

    if(remoteOnly==null && officeOnly==null && partialRemote==null){
        remoteOnly="Remote-Only";
        officeOnly="Office-Only";
        partialRemote="Partial-Remote";
        type=false;
    }

    if(!dateSearchFlag && !remote && !type && !StringUtils.hasText(job) && !StringUtils.hasText(location)){
        System.out.println("1");
        jobPost=jobPostActivityService.getAll();
    }
    else if (!type){
        System.out.println("2");
        jobPost=jobPostActivityService.findByType(Arrays.asList(partTime,fullTime,freelance));
    }
    else {
        System.out.println("3");
        jobPost=jobPostActivityService.search(job,location, Arrays.asList(partTime,fullTime,freelance),
                Arrays.asList(remoteOnly,officeOnly,partialRemote), searchDate);
    }
System.out.println("JobPost:"+jobPost);

    Object currentUserProfileDetails=usersService.getCurrentUserProfileDetails();

    System.out.println("currentUserProfileDetails:"+currentUserProfileDetails);

    Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
    if(!(authentication instanceof AnonymousAuthenticationToken)){
        String username=authentication.getName();
        model.addAttribute("username",username);
        if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))){
List<RecruiterJobsDto> recruiterJobsDtoList=jobPostActivityService.
        getRecruiterJobs(((RecruiterProfile)currentUserProfileDetails).getUserAccountId());
model.addAttribute("jobPost",recruiterJobsDtoList);
        }else{


List<JobSeekerApply> jobSeekerApplyList=jobSeekerApplyService.
        getCandidatesJobs((JobSeekerProfile) currentUserProfileDetails);

            System.out.println("jobSeekerApplyList:"+jobSeekerApplyList);

List<JobSeekerSave> jobSeekerSaveList=jobSeekerSaveService.
        getCandidatesJob((JobSeekerProfile) currentUserProfileDetails);

            System.out.println("jobSeekerSaveList:"+jobSeekerSaveList);

boolean exist;
boolean saved;

for(JobPostActivity jobPostActivity:jobPost){
    exist=false;
    saved=false;
    for(JobSeekerApply jobSeekerApply:jobSeekerApplyList) {
        System.out.println("jobPostActivity"+jobPostActivity.getJobPostId());
        System.out.println("jobSeekerApply"+jobSeekerApply.getJob().getPostedById());
        if (Objects.equals(jobPostActivity.getJobPostId(), jobSeekerApply.getJob().getJobPostId())) {
            System.out.println("Success");
            jobPostActivity.setIsActive(true);
            exist = true;
            break;
        }
    }
        for(JobSeekerSave jobSeekerSave:jobSeekerSaveList){
            if(Objects.equals(jobPostActivity.getPostedById(),jobSeekerSave.getJob().getJobPostId())){
                jobPostActivity.setIsSaved(true);
                saved=true;
                break;
            }
        }

        if(!exist){
            jobPostActivity.setIsActive(false);
        }

        if(!saved){
            jobPostActivity.setIsSaved(false);
        }

        model.addAttribute("jobPost",jobPost);
    }
}
        }

    model.addAttribute("user",currentUserProfileDetails);
        return "dashboard";
    }

@GetMapping("/dashboard/add")//dashboard/add
    public String addJob(Model model){
    model.addAttribute("jobPostActivity",new JobPostActivity());
    model.addAttribute("user",usersService.getCurrentUserProfileDetails());
    return "add-jobs";
    }
@PostMapping("/dashboard/addNew")
public String addNew(JobPostActivity jobPostActivity,Model model){
    Users users=usersService.getCurrentUser();
    if(users!=null){
        jobPostActivity.setPostedById(users);
    }
    jobPostActivity.setPostedDate(new Date());
    model.addAttribute("jobPostActivity",jobPostActivity);
    JobPostActivity savedUser=jobPostActivityService.addNew(jobPostActivity);
    return "redirect:/dashboard/";
}

@GetMapping("global-search/")
public String globalSearch(
        Model model, @RequestParam(value = "job",required = false) String job,
        @RequestParam(value = "location",required = false) String location,
        @RequestParam(value = "partTime",required = false) String partTime,
        @RequestParam(value = "fullTime",required = false) String fullTime,
        @RequestParam(value = "freelance",required = false) String freelance,
        @RequestParam(value = "remoteOnly",required = false) String remoteOnly,
        @RequestParam(value = "officeOnly",required = false) String officeOnly,
        @RequestParam(value = "partialRemote",required = false) String partialRemote,
        @RequestParam(value = "today",required = false) boolean today,
        @RequestParam(value = "days7",required = false) boolean days7,
        @RequestParam(value = "days30",required = false) boolean days30
){

    model.addAttribute("partTime", Objects.equals(partTime,"Part-Time"));
    model.addAttribute("fullTime", Objects.equals(fullTime,"Full-Time"));
    model.addAttribute("freelance", Objects.equals(freelance,"Freelance"));

    model.addAttribute("remoteOnly", Objects.equals(remoteOnly,"Remote-Only"));
    model.addAttribute("officeOnly", Objects.equals(officeOnly,"Office-Only"));
    model.addAttribute("partialRemote", Objects.equals(partialRemote,"Partial-Remote"));

    model.addAttribute("today",today );
    model.addAttribute("days7", days7);
    model.addAttribute("days30", days30);

    model.addAttribute("job", job);
    model.addAttribute("location", location);

    LocalDate searchDate=null;
    List<JobPostActivity> jobPost=null;
    boolean dateSearchFlag=true;
    boolean remote=true;
    boolean type=true;
    System.out.println("Type"+fullTime);
    if(days30){
        searchDate=LocalDate.now().minusDays(30);
    }
    else if (days7){
        searchDate=LocalDate.now().minusDays(7);
    }
    else if (today){
        searchDate=LocalDate.now();
    }
    else {
        dateSearchFlag=false;
    }

    if(partTime==null && fullTime==null && freelance==null){
        partTime="Part-Time";
        fullTime="Full-Time";
        freelance="Freelance";
        remote=false;
    }

    if(remoteOnly==null && officeOnly==null && partialRemote==null){
        remoteOnly="Remote-Only";
        officeOnly="Office-Only";
        partialRemote="Partial-Remote";
        type=false;
    }

    if(!dateSearchFlag && !remote && !type && !StringUtils.hasText(job) && !StringUtils.hasText(location)){
        System.out.println("1");
        jobPost=jobPostActivityService.getAll();
    }
    else if (!type){
        System.out.println("2");
        jobPost=jobPostActivityService.findByType(Arrays.asList(partTime,fullTime,freelance));
    }
    else {
        System.out.println("3");
        jobPost=jobPostActivityService.search(job,location, Arrays.asList(partTime,fullTime,freelance),
                Arrays.asList(remoteOnly,officeOnly,partialRemote), searchDate);
    }

    model.addAttribute("jobPost",jobPost);

 return "global-search";


}
}
