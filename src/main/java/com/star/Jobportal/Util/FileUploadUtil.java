package com.star.Jobportal.Util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
@Component
public class FileUploadUtil {

    public static void saveFile(String uploadDir, String filename, MultipartFile multipartFile) throws IOException {
        Path uploadPath= Paths.get(uploadDir);//Converts "photos/recruiter123" to a Path object: uploadPath
        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);//Returns an InputStream to read the contents of the file.
        }

        try(InputStream inputStream=multipartFile.getInputStream();){
Path path=uploadPath.resolve(filename);//Combines the upload directory and filename to create the full file path.
System.out.println("FilePath:"+path);
System.out.println("File NAme:"+filename);
Files.copy(inputStream,path, StandardCopyOption.REPLACE_EXISTING);
        }catch (IOException ioe){
throw new IOException ("Colud not save file"+filename+ioe);
        }
    }
}
