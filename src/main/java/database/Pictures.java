package database;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Pictures {
    public static String picturePath = Paths.get("").toAbsolutePath().toString()  +"\\pictures\\";

    public static String UploadPicture(MultipartFile blob, String pictureID){
        String path = Pictures.picturePath + pictureID + ".png";
        try {
            byte[] bytes = blob.getBytes();
            Files.write(Paths.get(path), bytes);
        } catch (IOException e) {
            return Utils.createResult("error", "Could not upload file.");
        }
        return Utils.createResult("successful", "File upladed.");
    }

    public static InputStreamResource GetPicture(String pictureID){
        String path = Pictures.picturePath + pictureID + ".png";

        try {
            BufferedImage img = ImageIO.read(new File(path));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", bos);
            return new InputStreamResource(new ByteArrayInputStream(bos.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
