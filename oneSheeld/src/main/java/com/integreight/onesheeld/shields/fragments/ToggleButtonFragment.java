package com.integreight.onesheeld.shields.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.integreight.firmatabluetooth.ArduinoFirmata;
import com.integreight.onesheeld.R;
import com.integreight.onesheeld.enums.ArduinoPin;
import com.integreight.onesheeld.model.ArduinoConnectedPin;
import com.integreight.onesheeld.shields.ShieldFragmentParent;
import com.integreight.onesheeld.shields.controller.ToggleButtonShield;
import com.integreight.onesheeld.utils.ConnectingPinsView;
import com.integreight.onesheeld.utils.ConnectingPinsView.OnPinSelectionListener;
import com.integreight.onesheeld.utils.customviews.OneSheeldToggleButton;

public class ToggleButtonFragment extends
        ShieldFragmentParent<ToggleButtonFragment> {
    OneSheeldToggleButton toggleButtonButton;
    Button connectButton;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.toggle_button_shield_fragment_layout,
                container, false);

        return v;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toggleButtonButton = (OneSheeldToggleButton) v
                .findViewById(R.id.toggle_button_shield_button_toggle_button);
        if ((ToggleButtonShield) getApplication().getRunningShields().get(
                getControllerTag()) == null) {
            getApplication().getRunningShields().put(getControllerTag(),
                    new ToggleButtonShield(activity, getControllerTag()));
        }
        toggleButtonButton
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        // TODO Auto-generated method stub
                        ((ToggleButtonShield) getApplication()
                                .getRunningShields().get(getControllerTag()))
                                .setButton(isChecked);

                    }
                });
        if ((ToggleButtonShield) getApplication().getRunningShields().get(
                getControllerTag()) != null)
            toggleButtonButton.setEnabled(true);
    }

    @Override
    public void onStart() {
        if (getApplication().getRunningShields().get(getControllerTag()) == null) {
            if (!reInitController())
                return;
        }
        ConnectingPinsView.getInstance().reset(
                getApplication().getRunningShields().get(getControllerTag()),
                new OnPinSelectionListener() {

                    @Override
                    public void onSelect(ArduinoPin pin) {
                        if (pin != null) {
                            ((ToggleButtonShield) getApplication()
                                    .getRunningShields()
                                    .get(getControllerTag()))
                                    .setConnected(new ArduinoConnectedPin(
                                            pin.microHardwarePin,
                                            ArduinoFirmata.OUTPUT));
                            ((ToggleButtonShield) getApplication()
                                    .getRunningShields()
                                    .get(getControllerTag()))
                                    .setButton(toggleButtonButton.isChecked());
                            toggleButtonButton.setEnabled(true);
                        }

                    }

                    @Override
                    public void onUnSelect(ArduinoPin pin) {
                    }
                });
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void doOnServiceConnected() {
    }

}
