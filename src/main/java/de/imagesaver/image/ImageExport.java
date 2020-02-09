package de.imagesaver.image;

import de.imagesaver.image.helper.AES;
import de.imagesaver.image.helper.ArrayListContainer;
import de.imagesaver.image.helper.ByteImageSaver;

import java.io.*;
import java.util.ArrayList;

public class ImageExport
{
    boolean allowCrypting = false;
    boolean showLog = true;
    AES aes;
    ByteImageSaver byteImageSaver;

    //==================================================================================================================
    //Instances
    //==================================================================================================================
    public ImageExport(boolean allowCrypting, boolean color)
    {
        this.allowCrypting = allowCrypting;
        aes = new AES();
        byteImageSaver = new ByteImageSaver(showLog, !color);
    }

    public ImageExport(boolean allowCrypting, String KEY, boolean color)
    {
        this.allowCrypting = allowCrypting;
        aes = new AES(KEY);
        byteImageSaver = new ByteImageSaver(showLog, !color);
    }

    public ImageExport(boolean color)
    {
        aes = new AES();
        byteImageSaver = new ByteImageSaver(showLog, !color);
    }

    //==================================================================================================================
    //Object
    //==================================================================================================================
    public void exportObject(ArrayList<Object> objects, String imagePath)
    {
        //Object to ByteArray
        ArrayListContainer data = new ArrayListContainer(objects);
        byte[] byteArray = data.toByteArray();

        //Crypting
        if (allowCrypting) byteArray = aes.encrypt(byteArray);

        //Create ByteImage
        byteImageSaver.createImage(byteArray, imagePath);
    }

    public ArrayList<Object> importObject(String imagePath)
    {
        //ReadImage
        byte[] byteArray = byteImageSaver.readImage(imagePath);

        //Crypting
        if (allowCrypting) byteArray = aes.decrypt(byteArray);

        //ByteArray to Object
        ArrayList<Object> objects = ArrayListContainer.fromByteArray(byteArray);

        return objects;
    }

    //==================================================================================================================
    //String
    //==================================================================================================================
    public void exportString(String value, String imagePath)
    {
        //String to ByteArray
        byte[] byteArray = value.getBytes();

        //Crypting
        if (allowCrypting) byteArray = aes.encrypt(byteArray);
        debugByteArray(byteArray);

        //Create ByteImage
        logString("Started writing image");
        byteImageSaver.createImage(byteArray, imagePath);
    }

    public String importString(String imagePath)
    {
        //ReadImage
        logString("Started reading image");
        byte[] byteArray = byteImageSaver.readImage(imagePath);
        debugByteArray(byteArray);

        //Crypting
        if (allowCrypting) byteArray = aes.decrypt(byteArray);

        logString("Finish!");
        return new String(byteArray);
    }

    //==================================================================================================================
    //File
    //==================================================================================================================
    public void exportFile(String filePath, String imagePath)
    {

        File file = new File(filePath);

        byte[] byteArray = new byte[(int) file.length()];
        logString("Started reading file");
        try
        {
            //ReadFile
            FileInputStream fis = new FileInputStream(file);
            //File to ByteArray
            fis.read(byteArray);
            fis.close();
        } catch (IOException e)
        {
            throw new Error("Couldn't read File by path: " + filePath);
        }

        //Crypting
        if (allowCrypting) byteArray = aes.encrypt(byteArray);

        //Create ByteImage
        logString("Started writing image");
        byteImageSaver.createImage(byteArray, imagePath);
    }

    public void importFile(String newFilePath, String imagePath)
    {
        //ReadImage
        logString("Started reading image");
        byte[] byeArray = byteImageSaver.readImage(imagePath);

        //Crypting
        if (allowCrypting) byeArray = aes.decrypt(byeArray);

        logString("Started writing file");
        try
        {
            FileOutputStream fos = new FileOutputStream(newFilePath);
            fos.write(byeArray);
            fos.close();
        } catch (IOException e)
        {
            throw new Error("Couldn't write File by path: " + newFilePath);
        }
        logString("Finish!");
    }

    private void logString(String log)
    {
        if (showLog)
            System.out.println(log);
    }

    //TODO DELETE----------------------------------------------------------------------------------------------
    private void debugByteArray(byte[] bytes)
    {
        String byteString = "";
        String seperator = ", ";
        for (Byte b : bytes)
        {
            byteString += b + seperator;
        }
        byteString = byteString.substring(0, byteString.length() - seperator.length());

        System.out.println(byteString);
    }
    //TODO DELETE----------------------------------------------------------------------------------------------
}

