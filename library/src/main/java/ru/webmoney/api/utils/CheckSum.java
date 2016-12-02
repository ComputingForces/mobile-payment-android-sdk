package ru.webmoney.api.utils;

public class CheckSum
{
    final static char[] hexArray      = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
    final static char[] hexArrayUpper = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    
    public static String toHexString(byte[] block)
    {
        final char[] hexChars = new char[block.length * 2];
        final int len = block.length;
        int k = 0;
        for (int i = 0; i < len; i++)
        {
            final byte byte0 = block[i];
            hexChars[k++] = hexArrayUpper[(byte0 >>> 4 & 0xf)];
            hexChars[k++] = hexArrayUpper[(byte0 & 0xf)];
        }
        return new String(hexChars);
    }
    
    public static String toHexString(byte[] block, boolean bUpperCase)
    {
        final char[] hexChars = new char[block.length * 2];
        final int len = block.length;
        
        final char[] _hexArray = bUpperCase ? hexArrayUpper : hexArray;
        
        int k = 0;
        for (int i = 0; i < len; i++)
        {
            final byte byte0 = block[i];
            hexChars[k++] = _hexArray[(byte0 >>> 4 & 0xf)];
            hexChars[k++] = _hexArray[(byte0 & 0xf)];
        }
        return new String(hexChars);
    }    


    public static byte[] hexStringToBytes(String s)
    {
        final int len = s.length();
        final byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) 
        {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                  + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    
    
    public static String getMD5(String input)
    {
        try
        {
            final byte[] source = input.getBytes("UTF-8");
            final java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.update(source);
            return toHexString(md.digest());
        }
        catch (final Throwable e)
        {
        }
        return null;
    }
    
    public static String getSha256(String input)
    {
        try
        {
            final byte[] source = input.getBytes("UTF-8");
            final java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            md.update(source);
            return CheckSum.toHexString(md.digest(), true);
        }
        catch (final Throwable e)
        {
        }
        return null;
    }
}


