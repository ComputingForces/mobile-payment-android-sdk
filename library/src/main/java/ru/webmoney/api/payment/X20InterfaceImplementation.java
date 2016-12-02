package ru.webmoney.api.payment;

import android.content.Context;
import android.content.res.Resources;

import org.json.JSONObject;

import ru.webmoney.api.APIException;
import ru.webmoney.api.parser.JsonObjectParser;
import ru.webmoney.api.ru.webmoney.api.net.ConnectionSettings;
import ru.webmoney.api.ru.webmoney.api.net.HTTPClient;
import ru.webmoney.api.ru.webmoney.api.net.JSONRequestBody;
import ru.webmoney.api.ru.webmoney.api.net.RequestProperty;

class X20InterfaceImplementation implements X20Interface
{
    private ConnectionSettings connectionSettings;

    private static String BASE_URL = "https://merchant.webmoney.ru/conf/xml/";

    private RequestProperty [] properties =
            {
               new RequestProperty("User-Agent", "Android"),
               new RequestProperty("Content-Type", "text/json")
               //new RequestProperty("Content-Type", "application/json; charset=utf-8")
            };

    X20InterfaceImplementation(ConnectionSettings connectionSettings)
    {
        this.connectionSettings = connectionSettings;
    }


    @Override
    public TransactionResponse createTransaction(TransactionRequest request) throws APIException
    {
        JSONObject result = (JSONObject)

        HTTPClient.execute(BASE_URL + "XMLTransRequest.asp",
                           HTTPClient.REQUEST_METHOD_POST,
                           connectionSettings,
                           new JsonObjectParser().setCharset("windows-1251"),
                           new JSONRequestBody(request.toJSON()).setCharset("windows-1251"),
                           properties
                        );
        return TransactionResponse.fromJSON(result);
    }

    @Override
    public ConfirmResponse confirmTransaction(ConfirmRequest request) throws APIException
    {
        JSONObject result = (JSONObject)
                HTTPClient.execute(BASE_URL + "XMLTransConfirm.asp",
                        HTTPClient.REQUEST_METHOD_POST,
                        connectionSettings,
                        new JsonObjectParser().setCharset("windows-1251"),
                        new JSONRequestBody(request.toJSON()),
                        properties
                );
        return ConfirmResponse.fromJSON(result);
    }

    @Override
    public String getErrorDescription(Context context, int retval) throws APIException
    {
        Resources resources = context.getResources();
        final String id;
        if (retval < 0)
        {
            id = "x20_e_n" + retval;
        }
        else
        {
            id = "x20_e_" + retval;
        }

        final int r = resources.getIdentifier(id, "string", context.getPackageName());
        if (r > 0)
            return resources.getString(r);

        return null;
        //return resources.getString(R.string.unknown_error);
    }
}