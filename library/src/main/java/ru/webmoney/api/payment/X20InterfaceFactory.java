package ru.webmoney.api.payment;

import ru.webmoney.api.ru.webmoney.api.net.ConnectionSettings;

public class X20InterfaceFactory
{
    public static X20Interface createInstance(ConnectionSettings connectionSettings)
    {
        return new X20InterfaceImplementation(connectionSettings);
    }
}
