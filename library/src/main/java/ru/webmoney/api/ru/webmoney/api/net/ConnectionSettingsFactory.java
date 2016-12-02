package ru.webmoney.api.ru.webmoney.api.net;


public final class ConnectionSettingsFactory
{
    public static ConnectionSettings createInstance()
    {
        return new DefaultConnectionSettings();
    }
}
