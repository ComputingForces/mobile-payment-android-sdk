package ru.webmoney.api.payment;

import org.json.JSONObject;

import java.util.Date;

import ru.webmoney.api.parser.X20DateParser;

public class ConfirmResponse extends MerchantResponse
{
    public float amount;
    public Date operDate;
    public String purpose;
    public String purseFrom;
    public String wmidFrom;
    public String smsSentState;


    static ConfirmResponse fromJSON(JSONObject jsonObject)
    {
        if (null == jsonObject)
            return null;
        ConfirmResponse response = new ConfirmResponse();
        JSONObject operation = jsonObject.optJSONObject("operation");
        if (null != operation)
        {
            response.wmInvoiceId = operation.optLong("wminvoiceid");
            response.wmTransId = operation.optLong("wmtransid");
            response.amount      = (float) operation.optDouble("amount");
            String operdate      = operation.optString("operdate");
            if (null != operdate)
                response.operDate = X20DateParser.parse(operdate);

            response.purpose = operation.optString("purpose");
            response.purseFrom = operation.optString("pursefrom");
            response.wmidFrom = operation.optString("wmidfrom");
        }
        response.retval         = jsonObject.optInt("retval", -1);
        response.description = jsonObject.optString("retdesc");
        response.userDescription = jsonObject.optString("userdesc");
        response.smsSentState   = jsonObject.optString("smssentstate");
        return response;
    }

    /*
      "operation":
     {
       "wminvoiceid":(int),
       "wmtransid":(int),
       "amount":(float),
       "operdate":(string),
       "purpose":(string),
       "pursefrom":(string),
       "wmidfrom":(string)
     },


   "retval":(int),
   "retdesc":(string),
   "userdesc":(string)
   "smssentstate":(string)
     */
}
