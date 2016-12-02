package ru.webmoney.api.payment;

import org.json.JSONObject;

import ru.webmoney.api.utils.CheckSum;

public class ConfirmRequest extends MerchantRequest
{
    public long     wmInvoiceId;

    /*
        В данном поле передается цифровой код, который покупатель получил на мобильный телефон
        для подтверждения платежа. Если СМС не отправлялась покупателю (был отправлен USSD запрос или
        просто ожидается оплата покупателем ВМ-счета через мобильные программы управления кошельками),
        то здесь необходимо передать код со значением 0.
        В случае если все же СМС была отправлена покупателю, но покупатель оплатил ВМ -счет через программу
        управления кошельками, то передача здесь кода 0 все равно даст успешный результат,
        так как проверка кода просто не будет производиться. Если в данном параметре передать -1 и
        на момент выполнения запроса оплата еще не произошла, то счет будет отменен и оплата в дальнейшем будет невозможна.
     */
    public String   clientNumberCode;

    private String getSign()
    {
         final StringBuilder sb = new StringBuilder(64);
         sb.append(wmid).append(payeePurse).append(wmInvoiceId);
         if (null != clientNumberCode)
             sb.append(clientNumberCode);
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
            jsonObject.put("lmi_wminvoiceid", wmInvoiceId);

            if (null != clientNumberCode && 0 != clientNumberCode.length())
                jsonObject.put("lmi_clientnumber_code", clientNumberCode);

            switch (signType)
            {
                case sha256:
                    jsonObject.put("sha256", CheckSum.getSha256(getSign()));
                    break;
                case md5:
                    jsonObject.put("md5", CheckSum.getMD5(getSign()));
                    break;
            }
            if (null != lang) jsonObject.put("lang", lang);
            return jsonObject;
        }
        catch (Throwable e)
        {
            return null;
        }
    }

}