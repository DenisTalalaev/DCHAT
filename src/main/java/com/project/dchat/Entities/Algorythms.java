package com.project.dchat.Entities;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;

public class Algorythms {

    public static byte[] imageToByteArray(Image image, String format) throws IOException {
        if(image == null) return null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), format, outputStream);
        return outputStream.toByteArray();
    }

    public static Image byteArrayToImage(byte[] bytes) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        return SwingFXUtils.toFXImage(ImageIO.read(inputStream), null);
    }

    public static String getFileExtension(File file) {
        String fileName = file.getName();
        String[] parts = fileName.split("\\.");
        if (parts.length > 1) {
            return parts[parts.length - 1];
        } else {
            return "";
        }
    }

    public static String readFileToString(File file) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(System.lineSeparator());
        }
        reader.close();
        return stringBuilder.toString();
    }

    public static void resetPassword(User sender, ArrayList<User> users) {
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if(user.getUsername().equals(sender.getUsername())){
                user.setPassword(sender.getPassword());
                return;
            }
        }
    }
}
