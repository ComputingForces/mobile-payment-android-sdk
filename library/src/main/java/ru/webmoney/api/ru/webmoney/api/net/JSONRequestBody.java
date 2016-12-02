package ru.webmoney.api.ru.webmoney.api.net;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;


public class JSONRequestBody implements  IRequestBody
{
    private final JSONObject object;
    private String charsetName;

    public JSONRequestBody(JSONObject object)
    {
        this.object = object;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException
    {
        if (null != object)
        {
            final OutputStreamWriter out;

            if (null != charsetName)
                out = new OutputStreamWriter(outputStream, charsetName);
            else
                out = new OutputStreamWriter(outputStream);

            String body = getString(object);
            out.write(body);
            out.close();
        }
    }

    protected String getString(JSONObject object)
    {
        return object.toString();
    }

    public JSONRequestBody setCharset(String charsetName)
    {
        this.charsetName = charsetName;
        return this;
    }

}
