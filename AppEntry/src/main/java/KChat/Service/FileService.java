package KChat.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.UUID;

@Service
public class FileService {
    @Value("${resource.img.path}")
    private String imgPath;
    @Value("${resource.file.path}")
    private String fileCachePath;

    private final Integer bufferSize = 4096;

    public String uploadImage(MultipartFile image){
        try {
            String fileName = image.getOriginalFilename();
            String suffix = fileName.substring(fileName.lastIndexOf('.'));
            return toUpload(image.getInputStream(),suffix,imgPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void removeImage(String imgName){
        toRemove(imgPath,imgName);
    }

    public void removeCacheFile(String fileName){
        toRemove(fileCachePath,fileName);
    }

    public String uploadCacheFile(MultipartFile file,String suffix){
        try {
            return toUpload(file.getInputStream(),suffix,fileCachePath);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    public byte[] getCacheFileBytes(String fileName){
        return toDownload(fileCachePath,fileName);
    }

    private byte[] toDownload(String root, String fileName){
        try {
            FileInputStream stream = new FileInputStream(String.format("%s/%s",root,fileName));
            byte[] res = stream.readAllBytes();
            stream.close();
            return res;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    private String toUpload(InputStream stream,String suffix, String root){
        String newFileName = null;
        try {
            newFileName = suffix.isEmpty()||suffix.isBlank() ? UUID.randomUUID().toString():
                    String.format("%s.%s",UUID.randomUUID(),suffix);
            FileOutputStream output = new FileOutputStream(String.format("%s/%s",root,newFileName));
            int len;
            byte[] buffer = new byte[bufferSize];
            while((len=stream.read(buffer))>0)
                output.write(buffer,0,len);
            output.close();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newFileName;
    }

    private void toRemove(String root,String fileName){
        try {
            File file = new File(String.format("%s/%s",root,fileName));
            if(file.exists()&&file.isFile())
                file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
