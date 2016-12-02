package ru.webmoney.api.ru.webmoney.api.net;


import java.io.IOException;
import java.io.OutputStream;

public interface IRequestBody
{
    void write(OutputStream outputStream) throws IOException;
}

