package ru.webmoney.mobile.payment.sample.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;

import ru.webmoney.api.APIClient;
import ru.webmoney.api.APIException;
import ru.webmoney.api.DefaultAPIClient;
import ru.webmoney.api.payment.ClientNumberType;
import ru.webmoney.api.payment.ConfirmRequest;
import ru.webmoney.api.payment.ConfirmResponse;
import ru.webmoney.api.payment.ConfirmType;
import ru.webmoney.api.payment.MerchantRequest;
import ru.webmoney.api.payment.SignType;
import ru.webmoney.api.payment.TransactionRequest;
import ru.webmoney.api.payment.TransactionResponse;
import ru.webmoney.api.utils.SmsReceiver;
import ru.webmoney.api.utils.X20SMSReceiver;
import ru.webmoney.mobile.payment.sample.R;
import ru.webmoney.mobile.payment.sample.Settings;
import ru.webmoney.mobile.payment.sample.tasks.ITaskCallback;
import ru.webmoney.mobile.payment.sample.tasks.TransactionConfirmAsyncTask;
import ru.webmoney.mobile.payment.sample.tasks.TransactionRequestAsyncTask;

public class X20Fragment extends BaseFragment implements ITaskCallback
{
    private Order order;
    private Button[] runButtons;
    private Button confirmButton;
    private EditText editTextAccount, editTextCode;
    private APIClient apiClient;

    private final static int RC_CREATE_TRANSACTION = 0;
    private final static int RC_CONFIRM_TRANSACTION = 1;

    public final static int PERMISSION_READ_SMS = 9;

    private Spinner accountTypeSpinner;
    private ViewGroup body;
    private boolean smsAccessAsked;
    private ConfirmType tempConfirmType;
    private String tempCode;
    private String savedAccount;
    private ClientNumberType savedClientNumberType;

    private IOnClose onCloseInterface;

    public X20Fragment setOrder(Order order)
    {
        this.order = order;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.x20_layout, null);
        initView(rootView);
        return rootView;
    }

    public void setData(Order order, String initAccount, ClientNumberType initClientNumberType)
    {
        this.order = order;
        savedAccount = initAccount;
        savedClientNumberType = initClientNumberType;
    }


    private void initView(View rootView)
    {
        TextView amountTextView = (TextView) rootView.findViewById(R.id.amount);

        DecimalFormat decimalFormatter = new DecimalFormat();
        decimalFormatter.setGroupingSize(3);
        decimalFormatter.setGroupingUsed(true);

        DecimalFormatSymbols newSymbols = new DecimalFormatSymbols();
        newSymbols.setGroupingSeparator(' ');
        newSymbols.setDecimalSeparator('.');

        decimalFormatter.setDecimalFormatSymbols(newSymbols);

        char purse = Settings.PURSE.charAt(0);
        amountTextView.setText(decimalFormatter.format(order.amount) + " WM" + purse);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        TextView orderTextView = (TextView) rootView.findViewById(R.id.order_info);
        orderTextView.setText(getString(R.string.order_info, order.number, sdf.format(order.date)));

        View body = rootView.findViewById(R.id.body);
        this.body = (ViewGroup) body;

        final Button keeperPaymentButton = (Button) body.findViewById(R.id.invoice_only);
        initButton(keeperPaymentButton, ConfirmType.INVOICE_ONLY, R.string.invoice_only_payment_title, R.string.invoice_only_payment_subtitle);

        final Button smsPaymentButton = (Button) body.findViewById(R.id.sms_payment);

        int subTitleResId;
        switch (purse)
        {
            case 'R':
                subTitleResId = R.string.subtile_sms_ussd_r;
                break;
            case 'Z':
                subTitleResId = R.string.subtile_sms_ussd_z;
                break;
            case 'E':
                subTitleResId = R.string.subtile_sms_ussd_e;
                break;
            case 'U':
                subTitleResId = R.string.subtile_sms_ussd_u;
                break;
            default:
                subTitleResId = R.string.subtile_sms_ussd_r;
        }
        initButton(smsPaymentButton, ConfirmType.SMS, R.string.sms_payment_title, subTitleResId);

        final Button ussdPaymentButton = (Button) body.findViewById(R.id.ussd_payment);
        initButton(ussdPaymentButton, ConfirmType.USSD, R.string.ussd_payment_title, subTitleResId);

        final Button[] buttons = new Button[]{keeperPaymentButton, smsPaymentButton, ussdPaymentButton};
        this.runButtons = buttons;
        final int limit = 10;

        editTextAccount = (EditText) body.findViewById(R.id.account);
        final TextView accountHelpTextView = (TextView) body.findViewById(R.id.account_help);

        final AccountEditTextWatcher accountEditTextWatcher = new AccountEditTextWatcher(limit, buttons);
        editTextAccount.addTextChangedListener(accountEditTextWatcher);

        accountTypeSpinner = (Spinner) body.findViewById(R.id.account_type);
        //http://stackoverflow.com/questions/34316074/spinner-inner-padding-is-larger-on-android-6-0-1


        AccountDescription [] accountDescriptions =
        new AccountDescription[]
        {
                new AccountDescription(ClientNumberType.phone, getString(R.string.x20_payment_type_phone), getString(R.string.x20_payment_type_phone_hint), getString(R.string.account_phone_help), 9, InputType.TYPE_CLASS_PHONE),
                new AccountDescription(ClientNumberType.wmid, getString(R.string.x20_payment_type_wmid), getString(R.string.x20_payment_type_wmid_hint), getString(R.string.account_wmid_help), 12, InputType.TYPE_CLASS_NUMBER),
                new AccountDescription(ClientNumberType.email, getString(R.string.x20_payment_type_email), getString(R.string.x20_payment_type_email), getString(R.string.account_email_help), 6, InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
        };

        ArrayAdapter<AccountDescription> spinnerArrayAdapter = new ArrayAdapter<>(accountTypeSpinner.getContext(),
                //android.R.layout.simple_spinner_item,
                //R.layout.support_simple_spinner_dropdown_item,
                R.layout.spinner_item, accountDescriptions
                );
        spinnerArrayAdapter.setDropDownViewResource(
                //android.R.layout.simple_spinner_dropdown_item
                //R.layout.support_simple_spinner_dropdown_item
                R.layout.spinner_dropdown_item);
        accountTypeSpinner.setAdapter(spinnerArrayAdapter);
        accountTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l)
            {
                AccountDescription item = (AccountDescription) adapterView.getAdapter().getItem(position);
                editTextAccount.setHint(item.hint);
                accountHelpTextView.setText(item.help);
                editTextAccount.setInputType(item.inputType);
                accountEditTextWatcher.setLimit(item.minAccountLen);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
            }
        });

        if (null != savedClientNumberType)
        {
            for (int i = 0; i < accountDescriptions.length; i++)
            {
                if (accountDescriptions[i].type == savedClientNumberType)
                {
                    accountTypeSpinner.setSelection(i);
                    break;
                }
            }
        }

        if (null != savedAccount)
            editTextAccount.setText(savedAccount);
    }

    @Override
    public void onPreExecute(int requestCode, Object tag)
    {
        hideSoftKeyboard();
        showProgress(true);
        switch (requestCode)
        {
            case RC_CREATE_TRANSACTION:
                for (Button button : runButtons)
                {
                    button.setEnabled(false);
                }
                break;
            case RC_CONFIRM_TRANSACTION:
                if (null != confirmButton)
                {
                    confirmButton.setEnabled(false);
                }
                break;
        }
    }

    private void showProgress(boolean visible)
    {
        final View view = getView();
        if (null != view)
        {
            view.findViewById(R.id.progress).setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }


    @Override
    public void onPostExecute(int requestCode, boolean bSuccess, Object tag)
    {
        showProgress(false);
        switch (requestCode)
        {
            case RC_CREATE_TRANSACTION:
                for (Button button : runButtons)
                {
                    button.setEnabled(true);
                }
                break;

            case RC_CONFIRM_TRANSACTION:
                if (null != confirmButton)
                {
                    confirmButton.setEnabled(true);
                }
                break;
        }
    }

    @Override
    public void onSuccess(int requestCode, Object data, Object tag)
    {
        switch (requestCode)
        {
            case RC_CREATE_TRANSACTION:
                onRequestResult((ConfirmType) tag, (TransactionResponse) data);
                break;
            case RC_CONFIRM_TRANSACTION:
                onConfirmResponse((ConfirmResponse) data);
                break;
        }
    }


    @Override
    public void onError(int requestCode, APIException exception, Object tag)
    {
        if (null != exception)
        {
            //android.widget.Toast.makeText(this.getContext(), exception.getMessage(), android.widget.Toast.LENGTH_LONG).show();
            //showSnackBar(exception.getMessage(), body, true, Color.RED, Color.WHITE);
            showError(exception.getMessage(), null);
        }
    }

    public X20Fragment setOnCloseInterface(IOnClose onCloseInterface)
    {
        this.onCloseInterface = onCloseInterface;
        return this;
    }


    private final static class AccountDescription
    {
        ClientNumberType type;
        String description;
        String hint;
        String help;
        int minAccountLen;
        int inputType;


        public AccountDescription(ClientNumberType type, String description, String hint, String help, int minAccountLen, int inputType)
        {
            this.type = type;
            this.description = description;
            this.hint = hint;
            this.help = help;
            this.minAccountLen = minAccountLen;
            this.inputType = inputType;
        }

        @Override
        public String toString()
        {
            return description;
        }
    }

    private void initButton(Button button, final ConfirmType confirmType, int titleResId, int subTitleResId)
    {
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                transactionRequest(confirmType);
            }
        });

        final Resources resources = getResources();
        final CharSequence textEnabled = getButtonText(resources, titleResId, subTitleResId, true);
        final CharSequence textDisabled = getButtonText(resources, titleResId, subTitleResId, false);
        button.setEnabled(false);
        button.setTag(textEnabled);
        button.setText(textDisabled);
    }

    private CharSequence getButtonText(Resources resources, int titleResId, int subTitleResId, boolean bEnabled)
    {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append(resources.getString(titleResId));
        sb.setSpan(new ForegroundColorSpan(resources.getColor(bEnabled ? R.color.dark_blue : R.color.button_title_disabled)), 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(new AbsoluteSizeSpan(16, true), 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.append('\n');
        final int len = sb.length();
        sb.append(resources.getString(subTitleResId));
        sb.setSpan(new ForegroundColorSpan(resources.getColor(R.color.subtitle)), len, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(new AbsoluteSizeSpan(14, true), len, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(new StyleSpan(Typeface.NORMAL), len, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    private APIClient getAPIClient()
    {
        if (null == apiClient)
        {
            apiClient = new DefaultAPIClient();
        }
        return apiClient;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSION_READ_SMS:
                if (null != permissions && null != grantResults)
                    handlePermissions(permissions, grantResults);
                break;
        }
    }


    public void handlePermissions(String[] permissions, int[] grantResults)
    {
        final int len;
        if ((len = permissions.length) == grantResults.length)
        {
            int s = 0;
            for (int i = 0; i < len; i++)
            {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                   s++;
            }
            doTransactionRequest(tempConfirmType, s == len);
        }
    }


    void onGetCode(String code)
    {
        if (null != editTextCode)
        {
            editTextCode.setText(code);
        }
        else
        {
            tempCode = code;
        }
    }


    private SmsReceiver smsReceiver;

    private void unRegisterSMSReceiver()
    {
        if (null != smsReceiver)
        {
            try
            {
                getActivity().unregisterReceiver(smsReceiver);
            }
            catch (Throwable e)
            {
            }
            finally
            {
                smsReceiver = null;
            }
        }
    }


    private void registerSMSReceiver(Handler handler)
    {
        unRegisterSMSReceiver();
        try
        {
            smsReceiver = new X20SMSReceiver(handler, 0);
            IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
            intentFilter.setPriority(999);
            getActivity().registerReceiver(smsReceiver, intentFilter);
        }
        catch (Throwable e)
        {
            System.err.print(e);
        }
    }

    @Override
    public void onDetach()
    {
        unRegisterSMSReceiver();
        if (null != onCloseInterface)
        {
            String account =  (null != editTextAccount) ?  editTextAccount.getText().toString() : savedAccount;
            ClientNumberType сlientNumberType = savedClientNumberType;

            if (null != accountTypeSpinner)
            {
                AccountDescription description = (AccountDescription) accountTypeSpinner.getSelectedItem();
                if (null != description)
                    сlientNumberType = description.type;
            }
            onCloseInterface.onClose(account, сlientNumberType);
        }
        super.onDetach();
    }

    boolean transactionRequest(final ConfirmType confirmType)
    {
        if (confirmType == ConfirmType.SMS)
        {
            //see also MainActivity onRequestPermissionsResult
            if (false == checkPermission(Manifest.permission.READ_SMS))
            {
                if (false == smsAccessAsked)
                {
                    tempConfirmType = confirmType;
                    ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.RECEIVE_SMS}, PERMISSION_READ_SMS);
                    smsAccessAsked = true;
                    return true;
                }
                else
                {
                    doTransactionRequest(confirmType, false);
                    return true;
                }
            }
            doTransactionRequest(confirmType, true);
            return true;
        }

        doTransactionRequest(confirmType, false);
        return true;
    }


    private void doTransactionRequest(final ConfirmType confirmType, boolean registerSMSReceiver)
    {
        if (registerSMSReceiver)
        {
            registerSMSReceiver(new Handler()
                                {
                                    @Override
                                    public void handleMessage(Message msg)
                                    {
                                        onGetCode((String) msg.obj);
                                    }
                                }
            );
        }        TransactionRequest request = new TransactionRequest();
        fillMerchantRequest(request);

        request.paymentNo = order.number;
        request.paymentAmount = order.amount;
        request.paymentDesc = order.description;
        request.clientNumber = editTextAccount.getText().toString();
        AccountDescription accountDescription = (AccountDescription) accountTypeSpinner.getSelectedItem();
        request.clientNumberType = accountDescription.type;
        request.smsType = confirmType;
        request.shopId = Settings.shopId;
        request.isEmulated = Settings.emulated;

        new TransactionRequestAsyncTask(getContext(), getAPIClient(), this, RC_CREATE_TRANSACTION, confirmType).execute(request);
    }


    private static final class AccountEditTextWatcher implements TextWatcher
    {
        private int limit;
        private final Button[] buttons;

        public AccountEditTextWatcher(int limit, Button[] buttons)
        {
            this.limit = limit;
            this.buttons = buttons;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int before, int count)
        {
        }

        public void setLimit(int limit)
        {
            this.limit = limit;
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count)
        {
            boolean enabled = charSequence.length() >= limit;
            for (Button button : buttons)
            {
                if (button.isEnabled() != enabled)
                {
                    CharSequence text = (CharSequence) button.getTag();
                    button.setTag(button.getText());
                    button.setText(text);
                    button.setEnabled(enabled);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable)
        {
        }
    }

    private void onRequestResult(ConfirmType confirmType, TransactionResponse response)
    {
        switch (confirmType)
        {
            case SMS:
                 switchToInputSMS(response);
                 break;

            case USSD:
            case INVOICE_ONLY:
                 switchToConfirmScreen(confirmType, response);
                 break;
        }
    }

    private void switchToInputSMS(final TransactionResponse response)
    {
        closeTransactionScreen();
        View layout = getActivity().getLayoutInflater().inflate(R.layout.x20_layout_input_sms, body);
        final EditText editText = (EditText) layout.findViewById(R.id.input_sms_code);
        editTextCode = editText;
        final Button button = (Button) layout.findViewById(R.id.confirm_sms_button);
        button.setEnabled(false);
        this.confirmButton = button;
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                confirmTransaction(editText.getText().toString(), response);
            }
        });

        editText.setImeOptions(editText.getImeOptions() | EditorInfo.IME_ACTION_DONE);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            private boolean checkDoneKeyEvent(int actionId, KeyEvent event)
            {
                return ((EditorInfo.IME_ACTION_DONE  == actionId)
                        ||
                        (
                         EditorInfo.IME_ACTION_UNSPECIFIED == actionId
                            && null != event
                            && KeyEvent.KEYCODE_ENTER == event.getKeyCode()
                            && KeyEvent.ACTION_DOWN   == event.getAction()
                        )
                );
            }

            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event)
            {
                if (checkDoneKeyEvent(actionId, event))
                {
                    final Editable editable = editText.getText();
                    if (editable.length() > 0)
                    {
                        confirmTransaction(editable.toString(), response);
                        return true;
                    }
                }
                return false;
            }
        });

        //button.setTag(editText);
        editText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                boolean enabled = charSequence.length() > 4;
                if (button.isEnabled() != enabled)
                {
                    button.setEnabled(enabled);
                }
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
            }
        });

        if (null != tempCode)
        {
            editText.setText(tempCode);
            button.setEnabled(true);
            tempCode = null;
        }
    }

    private void fillMerchantRequest(MerchantRequest request)
    {
        request.wmid = Settings.WMID;
        request.payeePurse = Settings.PURSE;
        request.secretKeyX20 = Settings.SecretKeyX20;
        request.signType = SignType.sha256;
        request.lang = Settings.language;
    }

    private boolean checkPermission(String permission)
    {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
            return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(getActivity(), permission);
        return true;
    }

    boolean confirmTransaction(String smsCode, TransactionResponse response)
    {
        ConfirmRequest request = new ConfirmRequest();

        fillMerchantRequest(request);
        request.wmInvoiceId = response.wmInvoiceId;
        request.clientNumberCode = smsCode;
        new TransactionConfirmAsyncTask(getContext(), getAPIClient(), this, RC_CONFIRM_TRANSACTION, null).execute(request);
        return true;
    }


    private void onConfirmResponse(ConfirmResponse data)
    {
        //showSnackBar(getText(R.string.order_confirmed), body, true, getResources().getColor(R.color.dark_blue), Color.WHITE);
        //restore();

        showInfo(getString(R.string.order_confirmed), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                restore();
            }
        });
    }

    private void closeTransactionScreen()
    {
        body.removeAllViews();
        savedAccount = (null != editTextAccount) ?  editTextAccount.getText().toString() : null;
        if (null != accountTypeSpinner)
        {
            AccountDescription description = (AccountDescription) accountTypeSpinner.getSelectedItem();
            if (null != description)
            {
                savedClientNumberType = description.type;
            }
        }
    }


    private void switchToConfirmScreen(ConfirmType confirmType, final TransactionResponse response)
    {
        closeTransactionScreen();
        View layout = getActivity().getLayoutInflater().inflate(R.layout.x20_layout_confirm_invoice, body);
        TextView textView = (TextView) layout.findViewById(R.id.intro);
        final String intro;
        if (ConfirmType.INVOICE_ONLY == confirmType)
            intro = getString(R.string.invoice_confirm_text);
        else
            intro = getString(R.string.ussd_confirm_text);
        textView.setText(intro);

        final Button button = (Button) layout.findViewById(R.id.confirm_sms_button);
        this.confirmButton = button;
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                confirmTransaction("0", response);
            }
        });
    }

    interface IOnClose
    {
        void onClose(String account, ClientNumberType сlientNumberType);
    }


}

