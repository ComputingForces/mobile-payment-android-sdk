package ru.webmoney.api.payment;

import org.json.JSONObject;

public class TransactionResponse extends MerchantResponse
{
    public int realSmsType;

    static TransactionResponse fromJSON(JSONObject jsonObject)
    {
        if (null == jsonObject)
            return null;
        final TransactionResponse response = new TransactionResponse();
        final JSONObject operation = jsonObject.optJSONObject("operation");
        if (null != operation)
        {
            response.wmInvoiceId  = operation.optLong("wminvoiceid");
            response.wmTransId    = operation.optLong("wmtransid");
            response.realSmsType  = operation.optInt("realsmstype");
        }
        response.retval          = jsonObject.optInt("retval", -1);
        response.description     = jsonObject.optString("retdesc");
        response.userDescription = jsonObject.optString("userdesc");
        return response;
    }
}

