package ru.webmoney.api.payment;


import org.json.JSONObject;

import ru.webmoney.api.utils.CheckSum;

public class TransactionRequest extends MerchantRequest
{
    public long paymentNo;
    public float paymentAmount;
    public String paymentDesc;
    public String clientNumber;
    public ClientNumberType clientNumberType;
    public ConfirmType smsType;
    public long shopId;
    public boolean isEmulated;

    private String getSign(String number)
    {
        StringBuilder sb = new StringBuilder(64).append(wmid).append(payeePurse).append(paymentNo)
                .append(number).append(clientNumberType.getCode());
        if (null != secretKeyX20)
           sb.append(secretKeyX20);
        return sb.toString();
    }

    public JSONObject toJSON()
    {
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put("wmid", wmid);
            jsonObject.put("lmi_payee_purse", payeePurse);
            jsonObject.put("lmi_payment_no", paymentNo);
            jsonObject.put("lmi_payment_amount", JSONObject.numberToString(paymentAmount));
            if (null != paymentDesc) jsonObject.put("lmi_payment_desc", paymentDesc);
            String number = clientNumber;
            if (clientNumberType == ClientNumberType.phone)
                number = number.replaceAll("[^0-9]", "");

            jsonObject.put("lmi_clientnumber", number);
            jsonObject.put("lmi_clientnumber_type", clientNumberType.getCode());
            jsonObject.put("lmi_sms_type", smsType.getCode());
            switch (signType)
            {
                case sha256:
                     jsonObject.put("sha256", CheckSum.getSha256(getSign(number)));
                     break;
                case md5:
                     jsonObject.put("md5", CheckSum.getMD5(getSign(number)));
                     break;
            }

            if (null != lang) jsonObject.put("lang", lang);
            if (shopId > 0) jsonObject.put("lmi_shop_id", shopId);

            jsonObject.put("emulated_flag", isEmulated ? 1 : 0);
            return jsonObject;
        }
        catch (Throwable e)
        {
            return null;
        }
    }
}
