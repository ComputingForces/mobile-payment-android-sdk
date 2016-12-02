package ru.webmoney.api.utils;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver
{
    private final Handler handler;
    private final Pattern [] patterns;

    private final int code;

    public SmsReceiver(Pattern[] patterns, Handler handler, int code)
    {
        this.patterns = patterns;
        this.handler = handler;
        this.code = code;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle extras = intent.getExtras();
        if (extras == null)
            return;

        final Object[] pdus = (Object[]) extras.get("pdus");
        if (null == pdus)
            return;

        for (Object obj : pdus)
        {
            SmsMessage message = SmsMessage.createFromPdu((byte[]) obj);
            String text = message.getMessageBody();
            if (null != text && text.length()>0)
            {
                for (int j = 0; j < patterns.length; j++)
                {
                    Matcher m = patterns[j].matcher(text);
                    if (m.find())
                    {
                        handler.sendMessage(Message.obtain(handler, code, m.group(1)));
                        return;
                    }
                }
            }
        }
    }
}
