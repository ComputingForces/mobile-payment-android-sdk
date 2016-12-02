package ru.webmoney.mobile.payment.sample.tasks;

import android.content.Context;
import android.text.TextUtils;

import ru.webmoney.api.APIClient;
import ru.webmoney.api.APIException;
import ru.webmoney.api.payment.MerchantResponse;
import ru.webmoney.mobile.payment.sample.BuildConfig;


abstract class X20AsyncTask extends Task
{
    protected final APIClient apiClient;
    protected final Context context;

    X20AsyncTask(Context context, APIClient apiClient, ITaskCallback onResult, int requestCode, Object tag)
    {
        super(onResult, requestCode, tag);
        this.apiClient = apiClient;
        this.context = context;
    }

    protected String getErrorText(MerchantResponse response) throws APIException
    {
        String desc;

        desc = apiClient.getX20Interface().getErrorDescription(context, response.retval);
        if (!TextUtils.isEmpty(desc))
            return desc;

        desc = response.userDescription;
        if (!TextUtils.isEmpty(desc))
            return desc;

        desc = response.description;
        if (!TextUtils.isEmpty(desc))
            return desc;

        return null;
    }

    protected boolean handleResult(MerchantResponse response) throws APIException
    {
        if (null != response)
        {
            if (response.retval == 0)
                return true;

            APIException apiException = new APIException();
            apiException.setServerResult(response.retval);
            String errorText = getErrorText(response);
            if (null != errorText)
            {
                apiException.setServerMessage(errorText);
            }
            this.error = apiException;
        }
        return false;
    }
}
