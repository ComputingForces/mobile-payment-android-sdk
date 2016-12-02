package ru.webmoney.api.payment;

public enum ClientNumberType
{
    phone(0),
    wmid(1),
    email(2);

    private final int code;

    ClientNumberType(int code)
    {
        this.code = code;
    }

    public static ClientNumberType get(int code)
    {
        switch (code)
        {
            case 0: return  phone;
            case 1: return  wmid;
            case 2: return  email;
        }
        return  phone;
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


