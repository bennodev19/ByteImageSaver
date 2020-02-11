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

    /**
     * Sets whether the class show you informational ConsoleLogs or not
     *
     * @param showLog Sets whether the class show you informational ConsoleLogs or not
     */
    public void setShowLog(boolean showLog)
    {
        this.showLog = showLog;
    }

    //==================================================================================================================
    //Class
    //==================================================================================================================

    /**
     * Export/Import Strings/Objects/Files into images
     *
     * @param allowCrypting Sets whether the image should be encrypted or not
     * @param color         Sets whether the image should be colorful or not (Note: The image without colors needs more space)
     */
    public ImageExport(boolean allowCrypting, boolean color)
    {
        this.allowCrypting = allowCrypting;
        aes = new AES();
        byteImageSaver = new ByteImageSaver(showLog, !color);

        StringBuilder builder = new StringBuilder();

    }

    /**
     * Export/Import Strings/Objects/Files into images
     *
     * @param allowCrypting Sets whether the image should be encrypted or not (Note: The KEY is useless if you do not allow crypting)
     * @param KEY           A 16 byte StringKey which you have to define for encrypting your Data (Note: Only with that key you can decrypt your encrypted data)
     * @param color         Sets whether the image should be colorful or not (Note: The image without colors needs more space)
     */
    public ImageExport(boolean allowCrypting, String KEY, boolean color)
    {
        this.allowCrypting = allowCrypting;
        aes = new AES(KEY);
        byteImageSaver = new ByteImageSaver(showLog, !color);
    }

    /**
     * Export/Import Strings/Objects/Files into images
     *
     * @param color Sets whether the image should be colorful or not (Note: The image without colors needs more space)
     */
    public ImageExport(boolean color)
    {
        aes = new AES();
        byteImageSaver = new ByteImageSaver(showLog, !color);
    }

    //==================================================================================================================
    //Object
    //==================================================================================================================

    /**
     * Export Objects into a image
     * Note: Your Objects have to be Serializable(java.io.Serializable)
     *
     * @param objects   The object which you will export as an image
     * @param imagePath The path where the image should be saved
     */
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

    /**
     * Import Objects which has been read out of an image
     * Note: Your Objects have to be Serializable(java.io.Serializable)
     *
     * @param imagePath The path where the image is located
     * @return An ArrayList of Objects which has been read out of an image
     */
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

    /**
     * Export a String into a image
     *
     * @param value     The string which you will export as an image
     * @param imagePath The path where the image should be saved
     */
    public void exportString(String value, String imagePath)
    {
        //String to ByteArray
        byte[] byteArray = value.getBytes();

        //Crypting
        if (allowCrypting) byteArray = aes.encrypt(byteArray);

        //Create ByteImage
        logString("Started writing image");
        byteImageSaver.createImage(byteArray, imagePath);
    }

    /**
     * Import a String which has been read out of an image
     *
     * @param imagePath The path where the image is located
     * @return A String which has been read out from the image
     */
    public String importString(String imagePath)
    {
        //ReadImage
        logString("Started reading image");
        byte[] byteArray = byteImageSaver.readImage(imagePath);

        //Crypting
        if (allowCrypting) byteArray = aes.decrypt(byteArray);

        logString("Finish!");
        return new String(byteArray);
    }

    //==================================================================================================================
    //File
    //==================================================================================================================

    /**
     * Export a File into a image
     *
     * @param filePath  The path where your file is located
     * @param imagePath The path where the image should be saved
     */
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

    /**
     * Create a File which has been read out of an image
     *
     * @param imagePath   The path where the image is located
     * @param newFilePath The path where the new file should be saved
     */
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

    //==================================================================================================================
    //Output
    //==================================================================================================================
    private void logString(String log)
    {
        if (showLog)
            System.out.println(log);
    }
}

