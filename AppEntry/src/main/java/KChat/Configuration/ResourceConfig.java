package KChat.Configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "resource.img")
@Getter
@Setter
class ImgSrcConfig{
   private String pattern;
   private String location;
   private String path;
}

@Configuration
@ConfigurationProperties(prefix = "resource.file")
@Getter
@Setter
class FileSrcConfig{
    private String pattern;
    private String location;
    private String path;
}

@Configuration
public class ResourceConfig {
    private final ImgSrcConfig imgSrcConfig;
    private final FileSrcConfig fileSrcConfig;

    @Autowired
    public ResourceConfig(ImgSrcConfig imgSrcConfig,FileSrcConfig fileSrcConfig){
        this.imgSrcConfig = imgSrcConfig;
        this.fileSrcConfig = fileSrcConfig;
    }

    public ImgSrcConfig img(){
        return imgSrcConfig;
    }

    public FileSrcConfig file(){
        return fileSrcConfig;
    }
}
