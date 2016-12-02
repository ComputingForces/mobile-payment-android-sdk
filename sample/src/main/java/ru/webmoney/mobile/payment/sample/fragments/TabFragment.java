package ru.webmoney.mobile.payment.sample.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;

import ru.webmoney.api.payment.ClientNumberType;
import ru.webmoney.mobile.payment.sample.R;


public class TabFragment extends BaseFragment
{
    public static String FRAGMENT_TAG = "TabFragment";

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 3 ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View x =  inflater.inflate(R.layout.tab_layout, null);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.pager);
        final Context context = inflater.getContext();
        viewPager.setAdapter(new PagerAdapter(getChildFragmentManager(),
                context.getResources(),
                PreferenceManager.getDefaultSharedPreferences(context)));
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                    tabLayout.setupWithViewPager(viewPager);
                   }
        });
        return x;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (null != permissions && null != grantResults && null != viewPager)
        {
            final X20Fragment x20Fragment = (X20Fragment) getChildFragmentManager()
                    .findFragmentByTag("android:switcher:" + R.id.pager + ":" + viewPager.getCurrentItem());
            if (null != x20Fragment)
            {
                x20Fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    final static class PagerAdapter extends FragmentPagerAdapter
    {
        final SharedPreferences preferences;
        final  Resources resources;

        public PagerAdapter(FragmentManager fm, Resources resources, SharedPreferences preferences)
        {
            super(fm);
            this.preferences = preferences;
            this.resources  = resources;
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position)
        {
          switch (position)
          {
              case 0 :
              {

                  X20Fragment fragment = new X20Fragment();
                  long lmi_payment_no = preferences.getLong("lmi_payment_no", 0);
                  lmi_payment_no++;
                  preferences.edit().putLong("lmi_payment_no", lmi_payment_no).apply();
                  fragment.setData(
                          new Order(lmi_payment_no, 0.1f, new Date(), resources.getString(R.string.order_description, lmi_payment_no)),
                          preferences.getString("X20_account", null),
                          ClientNumberType.get(preferences.getInt("X20_сlient_number_type", ClientNumberType.phone.getCode()))
                          );

                  fragment.setOnCloseInterface(new X20Fragment.IOnClose()
                  {
                      @Override
                      public void onClose(String account, ClientNumberType сlientNumberType)
                      {
                          if (null != account || null != сlientNumberType)
                          {
                              SharedPreferences.Editor editor = preferences.edit();
                              if (null != account)
                                  editor.putString("X20_account", account);
                              if (null != сlientNumberType)
                                  editor.putInt("X20_сlient_number_type", сlientNumberType.getCode());
                              editor.apply();
                          }

                      }
                  });
                  return fragment;
              }
              case 1 : return new Other1Fragment();
              case 2 : return new Other2Fragment();
          }
        return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 :
                    return "X20";
                case 1 :
                    return "Other1";
                case 2 :
                    return "Other2";
            }
                return null;
        }
    }

}
