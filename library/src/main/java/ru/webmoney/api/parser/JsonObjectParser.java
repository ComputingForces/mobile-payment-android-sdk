package ru.webmoney.api.parser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class JsonObjectParser implements IParser
{
    private String charset;

    @Override
    public Object parse(Object src)
    {
        Reader reader = null;
        InputStream is;
        try
        {
            is = (InputStream) src;

            InputStreamReader inputStreamReader = (null != charset) ?
                    new InputStreamReader(is, charset) :
                    new InputStreamReader(is);
            BufferedReader r = new BufferedReader(inputStreamReader);
            StringBuilder total = new StringBuilder(is.available());
            String line;
            while ((line = r.readLine()) != null)
            {
                total.append(line);
            }
            return new JSONObject(total.toString());

        }
        catch (Throwable e)
        {

        }
        finally
        {
            if (null != reader)
            {
                try
                {
                    reader.close();
                }
                catch (Throwable ignored)
                {
                }
            }
        }
        return null;
    }

    @Override
    public boolean supportStream()
    {
        return true;
    }

    @Override
    public boolean selfClose()
    {
        return true;
    }

    public JsonObjectParser setCharset(String charset)
    {
        this.charset = charset;
        return this;
    }


}
