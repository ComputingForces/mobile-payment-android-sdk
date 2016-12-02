package ru.webmoney.mobile.payment.sample;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import ru.webmoney.mobile.payment.sample.fragments.TabFragment;
import ru.webmoney.mobile.payment.sample.fragments.X20Fragment;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentTransaction  fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.containerView, new TabFragment(), TabFragment.FRAGMENT_TAG).commit();
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (null != permissions && null != grantResults)
        {
            final TabFragment fragment = (TabFragment) getSupportFragmentManager()
                    .findFragmentByTag(TabFragment.FRAGMENT_TAG);
            if (null != fragment)
            {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

}