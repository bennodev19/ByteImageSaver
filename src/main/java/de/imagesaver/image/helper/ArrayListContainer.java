package de.imagesaver.image.helper;

import java.io.*;
import java.util.ArrayList;

public class ArrayListContainer implements Serializable
{
    private ArrayList<Object> objects;

    public ArrayListContainer(ArrayList<Object> objects)
    {
        this.objects = objects;
    }

    //==================================================================================================================
    //toByteArray
    //==================================================================================================================
    public byte[] toByteArray()
    {
        ByteArrayOutputStream fileOut = new ByteArrayOutputStream();
        try
        {
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(this);
            out.close();
            fileOut.close();
        } catch (IOException i)
        {
            throw new Error("Couldn't create ByteArray out of Object!");
        }

        return fileOut.toByteArray();
    }

    //==================================================================================================================
    //fromByteArray
    //==================================================================================================================
    public static ArrayList<Object> fromByteArray(byte[] input)
    {
        ArrayListContainer output = null;
        try
        {
            ByteArrayInputStream fileIn = new ByteArrayInputStream(input);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            output = (ArrayListContainer) in.readObject();
            in.close();
            fileIn.close();
        } catch (Exception e)
        {
            throw new Error("Could't code ByteArray to Object!");
        }

        return output.objects;
    }

    //==================================================================================================================
    //Getter
    //==================================================================================================================
    public ArrayList<Object> getObjects()
    {
        return objects;
    }
}

