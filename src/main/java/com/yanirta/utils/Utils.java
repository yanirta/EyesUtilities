package com.yanirta.utils;

import com.sun.glass.ui.Size;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public abstract class Utils {

    public static void setClipboard(String copy) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        StringSelection strSel = new StringSelection(copy);
        clipboard.setContents(strSel, null);
    }

    public static synchronized void saveImage(BufferedImage image, File destinationFile) throws IOException {
        if (null != image) {
            ImageIO.write(image, "png", destinationFile);
        } else {
            System.out.println("Unable to process image");
        }
    }

    public static String getRFC1123Date() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

    public static synchronized void createAnimatedGif(List<BufferedImage> images, File target, int timeBetweenFrames) throws IOException {
        ImageOutputStream output = new FileImageOutputStream(target);
        GifSequenceWriter writer = null;

        Size max = getMaxSize(images);

        try {
            for (BufferedImage image : images) {
                BufferedImage normalized = new BufferedImage(max.width, max.height, image.getType());
                normalized.getGraphics().drawImage(image, 0, 0, null);
                if (writer == null) writer = new GifSequenceWriter(output, image.getType(), timeBetweenFrames, true);
                writer.writeToSequence(normalized);
            }
        } finally {
            writer.close();
            output.close();
        }
    }

    private static Size getMaxSize(List<BufferedImage> images) {
        Size max = new Size(0, 0);
        for (BufferedImage image : images) {
            if (max.height < image.getHeight()) max.height = image.getHeight();
            if (max.width < image.getWidth()) max.width = image.getWidth();
        }
        return max;
    }

    public static String toFolderName(String name) {
        String foldername = new String(name);
        foldername = foldername.replaceAll("https?:", "");
        foldername = foldername.replaceAll("www\\.", "");
        foldername = foldername.replaceAll("/", "");
        foldername = foldername.replaceAll("\\.", "_");
        return foldername;
    }
}
