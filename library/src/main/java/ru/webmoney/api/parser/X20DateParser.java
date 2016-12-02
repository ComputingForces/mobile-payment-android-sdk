package ru.webmoney.api.parser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class X20DateParser
{
    public static Date parse(String src)
    {
        if (null != src)
        {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
            try
            {
                return sdf.parse(src);
            }
            catch (Throwable e)
            {
            }
        }
        return null;
    }
}
