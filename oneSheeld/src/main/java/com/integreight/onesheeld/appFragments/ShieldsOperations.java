package com.integreight.onesheeld.appFragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.integreight.onesheeld.MainActivity;
import com.integreight.onesheeld.OneSheeldApplication;
import com.integreight.onesheeld.R;
import com.integreight.onesheeld.enums.UIShield;
import com.integreight.onesheeld.popup.ArduinoConnectivityPopup;
import com.integreight.onesheeld.shields.ShieldFragmentParent;
import com.integreight.onesheeld.utils.BaseContainerFragment;
import com.integreight.onesheeld.utils.ConnectingPinsView;
import com.integreight.onesheeld.utils.customviews.MultiDirectionSlidingDrawer;
import com.integreight.onesheeld.utils.customviews.OneSheeldTextView;

public class ShieldsOperations extends BaseContainerFragment {
    private View v;
    private static ShieldsOperations thisInstance;
    protected SelectedShieldsListFragment mFrag;
    private ShieldFragmentParent<?> mContent;
    private MainActivity activity;

    public static ShieldsOperations getInstance() {
        if (thisInstance == null) {
            thisInstance = new ShieldsOperations();
        }
        return thisInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_shields_operation, container,
                false);
        setRetainInstance(true);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        initView(savedInstanceState);
        super.onActivityCreated(savedInstanceState);
    }

    MultiDirectionSlidingDrawer pinsSlidingView;
    MultiDirectionSlidingDrawer settingsSlidingView;

    private void initView(Bundle savedInstanceState) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.pinsViewContainer,
                        ConnectingPinsView.getInstance()).commit();
        activity.enableMenu();
        ((CheckBox) getView().findViewById(R.id.isMenuOpening))
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton arg0,
                                                 boolean arg1) {
                        if (arg1) {
                            activity.disableMenu();
                        } else
                            activity.enableMenu();
                    }
                });
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                activity.openMenu();
            }
        }, 500);

        if (savedInstanceState == null) {
            FragmentTransaction t = activity.getSupportFragmentManager()
                    .beginTransaction();
            mFrag = SelectedShieldsListFragment.newInstance(activity);
            t.replace(R.id.selectedShieldsContainer, mFrag);
            t.commit();
        } else {
            mFrag = (SelectedShieldsListFragment) activity
                    .getSupportFragmentManager().findFragmentById(
                            R.id.menu_frame);
        }
        if (mContent == null) {
            mContent = mFrag.getShieldFragment(0);
            try {
                new Handler().post(new Runnable() {

                    @Override
                    public void run() {
                        TextView shieldName = (OneSheeldTextView) activity
                                .findViewById(R.id.shieldName);
                        shieldName
                                .setVisibility(((ShieldFragmentParent<?>) mContent).shieldName
                                        .equalsIgnoreCase(UIShield.SEVENSEGMENT_SHIELD
                                                .getName()) ? View.GONE
                                        : View.VISIBLE);
                        shieldName
                                .setText(((ShieldFragmentParent<?>) mContent).shieldName);
                    }
                });
            } catch (Exception e) {
            }
            activity.setTitle(mFrag.getUIShield(0).name + " Shield");
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.shieldsContainerFrame, mContent,
                            mContent.getControllerTag()).commit();
        }
        pinsSlidingView = (MultiDirectionSlidingDrawer) getView().findViewById(
                R.id.pinsViewSlidingView);
        settingsSlidingView = (MultiDirectionSlidingDrawer) getView()
                .findViewById(R.id.settingsSlidingView);
        getView().findViewById(R.id.pinsFixedHandler).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        pinsSlidingView.animateOpen();
                    }
                });
        pinsSlidingView
                .setOnDrawerOpenListener(new MultiDirectionSlidingDrawer.OnDrawerOpenListener() {

                    @Override
                    public void onDrawerOpened() {
                        if (getView() != null && settingsSlidingView != null)
                            if (settingsSlidingView.isOpened())
                                settingsSlidingView.animateOpen();
                        activity.disableMenu();
                    }
                });
        pinsSlidingView
                .setOnDrawerCloseListener(new MultiDirectionSlidingDrawer.OnDrawerCloseListener() {

                    @Override
                    public void onDrawerClosed() {
                        if (getView() != null && settingsSlidingView != null && getView().findViewById(R.id.isMenuOpening) != null)
                            if (!settingsSlidingView.isOpened()
                                    && !((CheckBox) getView().findViewById(
                                    R.id.isMenuOpening)).isChecked())
                                activity.enableMenu();
                    }
                });
        pinsSlidingView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return pinsSlidingView.isOpened();
            }
        });
        getView().findViewById(R.id.settingsFixedHandler).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (getView() != null && settingsSlidingView != null)
                            settingsSlidingView.animateOpen();
                    }
                });
        settingsSlidingView
                .setOnDrawerOpenListener(new MultiDirectionSlidingDrawer.OnDrawerOpenListener() {

                    @Override
                    public void onDrawerOpened() {
                        if (getView() != null && pinsSlidingView != null && pinsSlidingView.isOpened()) {
                            pinsSlidingView.animateOpen();
                        }
                        activity.disableMenu();
                    }
                });
        settingsSlidingView
                .setOnDrawerCloseListener(new MultiDirectionSlidingDrawer.OnDrawerCloseListener() {

                    @Override
                    public void onDrawerClosed() {
                        if (getView() != null && pinsSlidingView != null && getView().findViewById(
                                R.id.isMenuOpening) != null)
                            if (!pinsSlidingView.isOpened()
                                    && !((CheckBox) getView().findViewById(
                                    R.id.isMenuOpening)).isChecked())
                                activity.enableMenu();
                    }
                });
        settingsSlidingView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return settingsSlidingView.isOpened();
            }
        });
        ((ToggleButton) getView().findViewById(R.id.shieldStatus))
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (MainActivity.currentShieldTag != null)
                            ((OneSheeldApplication) activity.getApplication())
                                    .getRunningShields().get(
                                    MainActivity.currentShieldTag).isInteractive = isChecked;
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onAttach(Activity activity) {
        this.activity = (MainActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.shields_operation, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        mContent = null;
        super.onDestroy();
    }

    @Override
    public void onResume() {
        ((CheckBox) getView().findViewById(R.id.isMenuOpening))
                .setChecked(false);
        activity.getOnConnectionLostHandler().canInvokeOnCloseConnection = false;
        if (((OneSheeldApplication) activity.getApplication()).getAppFirmata() == null
                || !((OneSheeldApplication) activity.getApplication())
                .getAppFirmata().isOpen()) {
            activity.getOnConnectionLostHandler().connectionLost = true;
        }
        activity.getOnConnectionLostHandler().sendEmptyMessage(0);
        activity.closeMenu();
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (activity != null
                        && activity.findViewById(R.id.getAvailableDevices) != null)
                    activity.findViewById(R.id.getAvailableDevices)
                            .setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    activity.closeMenu();
                                    if (activity.getSupportFragmentManager()
                                            .getBackStackEntryCount() > 1) {
                                        activity.getSupportFragmentManager()
                                                .popBackStack();
                                        activity.getSupportFragmentManager()
                                                .executePendingTransactions();
                                    }
                                    activity.stopService();
                                    if (!ArduinoConnectivityPopup.isOpened) {
                                        ArduinoConnectivityPopup.isOpened = true;
                                        new ArduinoConnectivityPopup(activity)
                                                .show();
                                    }
                                }
                            });
            }
        }, 500);
        ((ViewGroup) activity.findViewById(R.id.getAvailableDevices))
                .getChildAt(1).setBackgroundResource(
                R.drawable.bluetooth_disconnect_button);
        ((ViewGroup) activity.findViewById(R.id.cancelConnection))
                .getChildAt(1).setBackgroundResource(R.drawable.back_button);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                activity.findViewById(R.id.cancelConnection)
                        .setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                boolean isMenuOpened = (activity.appSlidingMenu != null && activity.appSlidingMenu
                                        .isOpen())
                                        || (settingsSlidingView != null && settingsSlidingView
                                        .isOpened())
                                        || (pinsSlidingView != null && pinsSlidingView
                                        .isOpened());
                                activity.onBackPressed();
                                if (!isMenuOpened)
                                    activity.findViewById(R.id.cancelConnection)
                                            .setOnClickListener(
                                                    new View.OnClickListener() {

                                                        @Override
                                                        public void onClick(
                                                                View v) {
                                                            // TODO
                                                            // Auto-generated
                                                            // method stub

                                                        }
                                                    });
                            }
                        });
            }
        }, 500);
        super.onResume();
    }
}
