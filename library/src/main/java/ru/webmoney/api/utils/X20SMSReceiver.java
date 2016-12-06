package ru.webmoney.api.utils;


import android.os.Handler;

import java.util.regex.Pattern;

public class X20SMSReceiver extends SmsReceiver
{
    public X20SMSReceiver(Handler handler, int code)
    {
        super(new Pattern [] { Pattern.compile("^Kod\\:([0-9]{6,9})")}, handler, code);
    }
}
