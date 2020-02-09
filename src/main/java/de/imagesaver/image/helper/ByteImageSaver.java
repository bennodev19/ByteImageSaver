package de.imagesaver.image.helper;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class ByteImageSaver
{
    //AlphaValue
    private int pixelHasAllValues = 255;
    private int pixelHasOneValue = 254;
    private int pixelHasNoValue = 253;

    boolean showLog;
    boolean storeValuesInOnePixel;

    public ByteImageSaver(boolean showLog, boolean storeValuesInOnePixel)
    {
        this.showLog = showLog;
        this.storeValuesInOnePixel = storeValuesInOnePixel;
    }

    //==================================================================================================================
    //ReadImage
    //==================================================================================================================
    public byte[] readImage(String path)
    {
        try
        {
            ArrayList<Byte> bytes = new ArrayList<Byte>();

            //Read Image
            BufferedImage image = null;
            try
            {
                image = ImageIO.read(new File(path));
            } catch (IOException e)
            {
                throw new Error("Couldn't read image by path: " + path);
            }

            int height = image.getHeight();
            int width = image.getWidth();

            //Encode Image
            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    //Pixel
                    int p = image.getRGB(x, y);

                    //get alpha
                    int a = (p >> 24) & 0xff;

                    //If picture includes no values
                    if (a == pixelHasNoValue) continue;

                    //If picture includes only one value
                    if (a == pixelHasOneValue)
                    {
                        int value = (p >> 16) & 0xff;
                        value -= 128;
                        bytes.add((byte) value);
                    }

                    //If picture includes 3 values
                    if (a == pixelHasAllValues)
                    {
                        //get red
                        int r = (p >> 16) & 0xff;
                        r -= 128;
                        bytes.add((byte) r);

                        //get green
                        int g = (p >> 8) & 0xff;
                        g -= 128;
                        bytes.add((byte) g);

                        //get blue
                        int b = p & 0xff;
                        b -= 128;
                        bytes.add((byte) b);
                    }
                }
                DisplayProcent(y, height);
            }

            //Cast ByteList to ByteArray
            byte[] byteArray = new byte[bytes.size()];
            for (int i = 0; i < bytes.size(); i++)
            {
                byteArray[i] = bytes.get(i);
            }

            return byteArray;
        } catch (Exception e)
        {
            throw new Error("Couldn't read image!");
        }
    }

    //==================================================================================================================
    //CreateImage
    //==================================================================================================================
    public void createImage(byte[] bytes, String path)
    {
        try
        {
            int size = (int) Math.ceil(Math.sqrt((double) bytes.length / 3));
            if (storeValuesInOnePixel) size =  (int) Math.ceil(Math.sqrt(bytes.length));
            int width = size;
            int height = size;

            int[] pix = new int[width * height];

            int pixelIndex = 0;
            int byteIndex = 0;
            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    int red = 0;
                    int green = 0;
                    int blue = 0;
                    int alpha = pixelHasNoValue;

                    // + 128 da bei Java ab 128 die werte negativ werden -> -1 = 129
                    if ((byteIndex + 3) < bytes.length && !storeValuesInOnePixel)
                    {
                        //Fill pixel with 3 values
                        red = (int) bytes[byteIndex] + 128;
                        byteIndex++;
                        green = (int) bytes[byteIndex] + 128;
                        byteIndex++;
                        blue = (int) bytes[byteIndex] + 128;
                        byteIndex++;
                        alpha = pixelHasAllValues;
                    }
                    else
                    {
                        if (byteIndex < bytes.length)
                        {
                            //Fill pixel with one value
                            int value = (int) bytes[byteIndex] + 128;
                            red = value;
                            green = value;
                            blue = value;
                            byteIndex++;
                            alpha = pixelHasOneValue;
                        }
                        else
                        {
                            //Fill pixel with dummy data
                            Random random = new Random();
                            if (!storeValuesInOnePixel)
                            {
                                red = random.nextInt(255);
                                green = random.nextInt(255);
                                blue = random.nextInt(255);
                            }
                            else
                            {
                                int value = random.nextInt(255);
                                red = value;
                                green = value;
                                blue = value;
                            }
                            alpha = pixelHasNoValue;
                        }
                    }

                    //Create Pixel
                    pix[pixelIndex] = (alpha << 24) | (red << 16) | (green << 8) | blue;
                    pixelIndex++;
                }
                DisplayProcent(y, height);
            }

            //Create Image
            Image image = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, pix, 0, width));
            BufferedImage bufferedImage = toBufferedImage(image);

            //WriteImage
            try
            {
                ImageIO.write(bufferedImage, "png", new File(path));
            } catch (IOException e)
            {
                throw new Error("Couldn't save image!");
            }
        } catch (Exception e)
        {
            throw new Error("Couldn't create image!");
        }
    }

    //==================================================================================================================
    //Helper
    //==================================================================================================================
    private BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered de.imagesaver.image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the de.imagesaver.image on to the buffered de.imagesaver.image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered de.imagesaver.image
        return bimage;
    }

    int lastValue = -1;

    private void DisplayProcent(int y, int height)
    {
        if (!showLog) return;

        //Procent
        int procent = (int) ((double) y / height * 100);
        if (lastValue != procent)
        {
            lastValue = procent;
            System.out.println("Procent: " + procent + "%");
        }
    }
}

