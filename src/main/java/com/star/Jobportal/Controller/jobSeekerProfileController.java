package com.star.Jobportal.Controller;

import com.star.Jobportal.Model.JobPostActivity;
import com.star.Jobportal.Model.JobSeekerProfile;
import com.star.Jobportal.Model.Skills;
import com.star.Jobportal.Model.Users;
import com.star.Jobportal.Util.FileDownloadUtil;
import com.star.Jobportal.Util.FileUploadUtil;
import com.star.Jobportal.repository.UsersRespository;
import com.star.Jobportal.service.UsersService;
import com.star.Jobportal.service.jobSeekerProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/job-seeker-profile")
public class jobSeekerProfileController {

    @Autowired
    private jobSeekerProfileService jobSeekerProfileService;
    @Autowired
    private UsersService usersService;
    @Autowired
private UsersRespository usersRespository;
@GetMapping("/")
    public String jobSeekerProfile(Model model){
    JobSeekerProfile jobSeekerProfile=new JobSeekerProfile();
    Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
    List<Skills> skillsList=new ArrayList<>();
    if(!(authentication instanceof AnonymousAuthenticationToken)){
       Users users=usersRespository.findByEmail(authentication.getName()).orElseThrow(
               ()->new UsernameNotFoundException("User Not Found"));

       Optional<JobSeekerProfile> jobSeekerProfile1=jobSeekerProfileService.getOne(users.getUserId());
       if(jobSeekerProfile1.isPresent()){
           jobSeekerProfile=jobSeekerProfile1.get();
           if(jobSeekerProfile.getSkills().isEmpty()){
               skillsList.add(new Skills());
               jobSeekerProfile.setSkills(skillsList);
           }
       }
        model.addAttribute("skills", skillsList);
        model.addAttribute("profile", jobSeekerProfile);


    }


        return "job-seeker-profile";
    }
@PostMapping("/addNew")
    public String addNew(JobSeekerProfile jobSeekerProfile, @RequestParam("image")MultipartFile image,
                         @RequestParam("pdf") MultipartFile pdf,
                         Model model){

        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)) {
            Users users = usersRespository.findByEmail(authentication.getName()).orElseThrow(
                    () -> new UsernameNotFoundException("User Not Found"));
            jobSeekerProfile.setUserId(users);
            jobSeekerProfile.setUserAccountId(users.getUserId());
        }
        List<Skills> skillsList=new ArrayList<>();
        model.addAttribute("profilee",jobSeekerProfile);
        model.addAttribute("skills",skillsList);

        for (Skills skills : jobSeekerProfile.getSkills()) {
            skills.setJobSeekerProfile(jobSeekerProfile);
        }

        String imageName="";
        String resumeName="";

        if(!Objects.equals(image.getOriginalFilename(),"")){
            imageName= StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
            jobSeekerProfile.setProfilePhoto(imageName);
        }

        if(!Objects.equals(pdf.getOriginalFilename(),"")){
            resumeName=StringUtils.cleanPath(Objects.requireNonNull(pdf.getOriginalFilename()));
            jobSeekerProfile.setResume(resumeName);
        }

        JobSeekerProfile jobSeekerProfile1=jobSeekerProfileService.addNew(jobSeekerProfile);

        try{
            String uploadDir = "photos/candidate" + jobSeekerProfile.getUserAccountId();
            if(!Objects.equals(image.getOriginalFilename(),"")){
                FileUploadUtil.saveFile(uploadDir,imageName,image);
            }
            if(!Objects.equals(pdf.getOriginalFilename(),"")){
                FileUploadUtil.saveFile(uploadDir,resumeName,pdf);
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        return "redirect:/dashboard/";
    }

    @GetMapping("/{id}")
public String candidateProfile(@PathVariable int id,Model model){
        Optional<JobSeekerProfile> jobPostActivity=jobSeekerProfileService.getOne(id);
        model.addAttribute("profile",jobPostActivity.get());
        return "job-seeker-profile";
}

@GetMapping("/downloadResume")
public ResponseEntity<?> downloadResume(@RequestParam("fileName") String name,@RequestParam("userID") String userId) throws IOException {
FileDownloadUtil  fileDownloadUtil=new FileDownloadUtil();
        Resource resource=null;

        try{
            resource=fileDownloadUtil.getFileResource("photos/candidate"+userId,name);

        }catch (IOException exception){
            return ResponseEntity.badRequest().build();
        }

        if(resource==null){
            new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        }

    String contentType = "application/octet-stream";//Helps client(browser) to find which type of file is being sent
    String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";//file is handled as binary stream ,
    // ensuring it is downloaded rather than displayed,without this file might open new tab and might be displayed without downloading

    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)//Content-Disposition header ensures that the downloaded file has a meaningful and correct name. Without it, the browser might use a default or generic name, which can be confusing for the user.
            .body(resource);
}

}
