/*
 * MainActivity.java
 * This file is part of UsbController
 *
 * Copyright (C) 2015 - Younghwan Jang
 *
 * UsbController is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * UsbController is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Note that UsbController is a modified version from UsbController
 * by Manuel Di Cerbo. Please see the following URL.
 * <http://android.serverbox.ch/?p=549>
 *
 * You should have received a copy of the GNU General Public License
 * along with UsbController. If not, see <http://www.gnu.org/licenses/>.
 */
package com.yfaney.usbcontroller;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    private static UsbController sUsbController;

    SeekBar mSeekBar;
    TextView mTextProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(sUsbController == null){
            sUsbController = new UsbController(this, mConnectionHandler,
                    ResourceOfDevice.VID_NANO, ResourceOfDevice.PID_NANO2);
        }
        mTextProgress = (TextView)findViewById(R.id.textViewOutput1);
        mSeekBar = (SeekBar)findViewById(R.id.seekBar);
        //mSeekBar.setMax(150);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if(fromUser){
                    String msg = getBaseContext().getText(R.string.text_output_pin_label) + ":" + Integer.toString(progress * 100 / seekBar.getMax()) + "%";
                    mTextProgress.setText(msg);
                    if(sUsbController != null){
                        sUsbController.send((byte)(progress&0xFF));
                    }
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_enum){
            if(sUsbController == null) {
                mTextProgress.setText(R.string.text_output_pin_label);
                sUsbController = new UsbController(this, mConnectionHandler,
                        ResourceOfDevice.VID_NANO, ResourceOfDevice.PID_NANO2);
            }
            else{
                mTextProgress.setText(R.string.text_output_pin_label);
                sUsbController.stop();
                sUsbController = new UsbController(this, mConnectionHandler,
                        ResourceOfDevice.VID_NANO, ResourceOfDevice.PID_NANO2);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private final IUsbConnectionHandler mConnectionHandler = new IUsbConnectionHandler() {
        @Override
        public void onUsbStopped() {
            L.e("Usb stopped!");
        }

        @Override
        public void onErrorLooperRunningAlready() {
            L.e("Looper already running!");
        }

        @Override
        public void onDeviceNotFound() {
            if(sUsbController != null){
                sUsbController.stop();
                sUsbController = null;
            }
        }
    };
}
