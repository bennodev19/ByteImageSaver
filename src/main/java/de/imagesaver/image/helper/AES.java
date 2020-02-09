package de.imagesaver.image.helper;


import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;

public class AES
{
    private String KEY = "TheBestSecretKey";
    private final String ALGO = "AES/CBC/PKCS5Padding";

    public AES(String KEY){
        this.KEY = KEY;
    }

    public AES(){

    }

    public byte[] encrypt(byte[] value)
    {
        try
        {
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, getKey(KEY), new IvParameterSpec(new byte[16]));
            byte[] encrypted = cipher.doFinal(value);

            return encrypted;
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public byte[] decrypt(byte[] encrypted)
    {
        try
        {
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.DECRYPT_MODE, getKey(KEY), new IvParameterSpec(new byte[16]));
            byte[] decrypted = cipher.doFinal(encrypted);

            return decrypted;
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public SecretKeySpec getKey(String key) throws UnsupportedEncodingException
    {
        if (key.length() != 16)
        {
            throw new IllegalArgumentException("Invalid key size! Your key has to be exact 16 bytes(letters)!");
        }
        return new SecretKeySpec(key.getBytes("UTF-8"), "AES");
    }
}

