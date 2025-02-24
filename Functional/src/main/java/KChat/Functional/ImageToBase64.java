package KChat.Functional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ImageToBase64 {
    private String rootPath;

    public ImageToBase64(String rootPath) {
        this.rootPath = rootPath;
    }

    private String imageToBase64(String imgName) throws IOException {
        var input=new FileInputStream(rootPath+imgName);
        var src=input.readAllBytes();
        input.close();
        return Base64.getEncoder().encodeToString(src);
    }

    private String imageToBase64(byte[] imgBytes) throws IOException {
        return Base64.getEncoder().encodeToString(imgBytes);
    }

    public String polish(String imgName) throws IOException {
        String suffix = imgName.substring(imgName.lastIndexOf('.')+1);
        return String.format("data:image/%s;base64,%s",suffix,imageToBase64(imgName));
    }

    public void removeImage(String imageName) throws IOException{
        File file=new File(rootPath+imageName);
        if(!file.exists())throw new IOException("不存在的文件无法删除");
        file.delete();
    }
    public void storeImage(byte[] imgBytes,String imgName) throws IOException
    {
        FileOutputStream stream = new FileOutputStream(rootPath+imgName);
        stream.write(imgBytes);
        stream.close();
    }
    public String getRootPath(){
        return rootPath;
    }
}
