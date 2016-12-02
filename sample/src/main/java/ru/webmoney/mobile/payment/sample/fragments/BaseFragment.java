package ru.webmoney.mobile.payment.sample.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import ru.webmoney.mobile.payment.sample.R;

public class BaseFragment extends Fragment
{
    protected void showSnackBar(CharSequence text, View parent, boolean isLong, int bkColor, int textColor)
    {
        try
        {
            final Snackbar snackbar = Snackbar.make(parent, text, isLong ? Snackbar.LENGTH_LONG: Snackbar.LENGTH_SHORT);
            final View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(bkColor);
            snackbar.setActionTextColor(textColor);
            snackbar.show();
        }
        catch (Throwable e)
        {
        }
    }

    protected  void showError(CharSequence message, final DialogInterface.OnClickListener listener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.error_dialog_title)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(message)
                .setCancelable(false);
        builder.setPositiveButton(android.R.string.ok, listener);
        try
        {
            builder.create().show();
        }
        catch (Throwable e)
        {
        }
    }


    protected  void showInfo(CharSequence message, final DialogInterface.OnClickListener listener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.info_dialog_title)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setMessage(message)
                .setCancelable(false);
        builder.setPositiveButton(android.R.string.ok, listener);
        try
        {
            builder.create().show();
        }
        catch (Throwable e)
        {
        }
    }

    protected void hideSoftKeyboard()
    {
        Activity activity = getActivity();
        if (null != activity && ! activity.isFinishing())
        {
            final View v = activity.getCurrentFocus();
            if (null != v)
                hideSoftKeyboard(v);
        }
    }


    protected void hideSoftKeyboard(View view)
    {
        if (null != view)
        {
            final InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (null != imm)
                imm.hideSoftInputFromWindow(view.getWindowToken(),
                 0
                );
        }
    }

    protected void restore()
    {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .detach(this)
                .attach(this)
                .commit();
    }
}
