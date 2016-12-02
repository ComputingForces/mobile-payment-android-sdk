package ru.webmoney.api.payment;


public enum ConfirmType
{
    SMS(1),
    USSD(2),
    INVOICE_ONLY(4);

    private final int code;

    ConfirmType(int code)
    {
        this.code = code;
    }

    public int getCode()
    {
        return this.code;
    }

    @Override
    public String toString()
    {
        return String.valueOf(code);
    }
}
