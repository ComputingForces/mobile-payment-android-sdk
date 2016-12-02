package ru.webmoney.api.ru.webmoney.api.net;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.webmoney.api.APIException;
import ru.webmoney.api.parser.IParser;

public class HTTPClient
{
   public static String REQUEST_METHOD_POST = "POST";
   public static String REQUEST_METHOD_GET = "GET";

   public static Object execute(String url, String requestMethod, ConnectionSettings settings, IParser parser, IRequestBody requestBody, RequestProperty ... properties) throws APIException
   {
       InputStream inputStream = null;

       try
       {
           HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();

           if (null != requestMethod)
                urlConnection.setRequestMethod(requestMethod);

           if (null != requestBody)
               urlConnection.setDoOutput(true);

           urlConnection.setUseCaches(false);
           urlConnection.setConnectTimeout(settings.getConnectTimeout());
           urlConnection.setReadTimeout(settings.getReadTimeout());

           if (null != properties && 0 != properties.length)
           for (RequestProperty property : properties)
               urlConnection.setRequestProperty(property.key , property.value);

           urlConnection.connect();

           if (null != requestBody)
           {
               requestBody.write(urlConnection.getOutputStream());
           }

           inputStream = urlConnection.getInputStream();
           int statusCode = urlConnection.getResponseCode();
           if (statusCode != 200)
           {
               throw new APIException(statusCode, urlConnection.getResponseMessage());
           }

           Object data = parser.parse(inputStream);
           if (parser.selfClose())
           {
               inputStream = null;
           }
           return data;
       }
       catch (Throwable e)
       {
           throw new APIException(e);
       }
       finally
       {
           if (null != inputStream)
           {
               try
               {
                   inputStream.close();
               }
               catch (Throwable ignored)
               {
               }
           }
       }
   }
}