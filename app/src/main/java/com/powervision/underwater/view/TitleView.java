package com.powervision.underwater.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.powervision.powersdk.callback.RayCallback;
import com.powervision.powersdk.callback.SystemStatusCallback;
import com.powervision.powersdk.param.SystemStatusParam;
import com.powervision.powersdk.sdk.PowerSDK;
import com.powervision.underwater.R;
import com.powervision.underwater.base.BaseHandleReference;

import java.text.DecimalFormat;

import static android.content.Context.WIFI_SERVICE;

/**
 *顶部view
 */

public class TitleView extends LinearLayout implements View.OnClickListener {

    private static final String TAG = TitleView.class.getSimpleName();

    private Context mContext;
    private WifiManager wm;         //WifiManager在包 android.net.wifi.WifiManager中
    private WifiInfo wi;            // WifiInfo在包android.net.wifi.WifiInfo中

    private TextView powerrayBatteryTextView;
    private TextView baseBatteryTextView;
    private ImageView wifiStrView;
    private TextView connectTextView;


    private TitleHandler titleHandler = new TitleHandler(this);
    private PowerSDK mPowerSDK;
    private int currentPowerRayBatteryRemain;
    private int currentBaseStationBatteryRemain;
    private int currentWifiStr;

    //private SPHelperUtils mSPHelper;

    private boolean ifConnected = false;//是否连接状态

    private boolean mIsShowBoatLowBattery = false;//是否主动显示船电量过低
    private boolean mIsShowStationLowBattery = false;//是否主动显示基站电量过低
//    private View masterMode;//主从未定义

    private TitleViewDelegate mDelegate;

    private static class TitleHandler extends BaseHandleReference<TitleView> {
        public TitleHandler(TitleView reference) {
            super(reference);
        }

        @Override
        public void referenceHandleMessage(TitleView reference, Message msg) {
            reference.dealWithMessage(msg);
        }
    }

    private void dealWithMessage(Message msg) {
        switch (msg.what) {
            case 1://本体电量
                setPowerRayBatteryLevel(currentPowerRayBatteryRemain);
                setPowerPayBatteryText(currentPowerRayBatteryRemain);

                if (!mIsShowBoatLowBattery && currentPowerRayBatteryRemain <= 20) {
                   // StandardDialogUtils.defaultStandardDialog(getContext(), R.string.ray_alarm_battery_title, R.string.ray_alarm_battery);
                }

                if (!mIsShowBoatLowBattery) {
                    mIsShowBoatLowBattery = true;
                }
                break;
            case 2://基站电量
                setBaseBatteryLevel(currentBaseStationBatteryRemain);
                setBaseBatteryText(currentBaseStationBatteryRemain);

                if (!mIsShowStationLowBattery && currentBaseStationBatteryRemain <= 20) {
                    //StandardDialogUtils.defaultStandardDialog(getContext(), R.string.ray_alarm_battery_title, R.string.ray_alarm_station_battery);
                }

                if (!mIsShowStationLowBattery) {
                    mIsShowStationLowBattery = true;
                }
                break;
            case 3://信号强度
                if (mContext != null) {
                    wm = (WifiManager) mContext.getApplicationContext().getSystemService(WIFI_SERVICE);
                    wi = wm.getConnectionInfo();
                    if (wi.getBSSID() != null)
                        currentWifiStr = wi.getRssi();//返回接收到的目前的网络的信号强度
                    setWifiStrLevel(currentWifiStr);
                }
                break;
            case 4://失去连接，改变连接状态
                setConnect(false);
                break;

        }
    }


    public TitleView(Context context) {
        this(context, null);
    }


    public TitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.gcs_water_titleview_layout, this);
       // mSPHelper = SPHelperUtils.getInstance(mContext);
        initViews();
    }

    private void initViews() {
        powerrayBatteryTextView = (TextView) findViewById(R.id.water_powerray_bt_text);
        baseBatteryTextView = (TextView) findViewById(R.id.water_base_bt_text);
        wifiStrView = (ImageView) findViewById(R.id.water_signal_view);
        connectTextView = (TextView) findViewById(R.id.connect_text_view);

    }

    @Override
    public void onClick(View view) {
        if (mDelegate == null) return;


    }

    /**
     * 设置本体电量
     *
     * @param i 电量值%
     */
    public void setPowerRayBatteryLevel(int i) {
        Drawable drawable = null;
        if (i >= 0 && i <= 20) {
            drawable = getResources().getDrawable(R.mipmap.powerray_bt_lv0);
        } else if (i > 20 && i <= 40) {
            drawable = getResources().getDrawable(R.mipmap.powerray_bt_lv1);
        } else if (i > 40 && i <= 60) {
            drawable = getResources().getDrawable(R.mipmap.powerray_bt_lv2);
        } else if (i > 60 && i <= 80) {
            drawable = getResources().getDrawable(R.mipmap.powerray_bt_lv3);
        } else {
            drawable = getResources().getDrawable(R.mipmap.powerray_bt_lv4);
        }
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            powerrayBatteryTextView.setCompoundDrawables(drawable, null, null, null);
        }
    }

    /**
     * 设置基站剩余电量
     *
     * @param i 电量值%
     */
    public void setBaseBatteryLevel(int i) {
        Drawable drawable = null;
        if (i >= 0 && i <= 20) {
            drawable = getResources().getDrawable(R.mipmap.water_base_battery0);
        } else if (i > 20 && i <= 40) {
            drawable = getResources().getDrawable(R.mipmap.water_base_battery1);
        } else if (i > 40 && i <= 60) {
            drawable = getResources().getDrawable(R.mipmap.water_base_battery2);
        } else if (i > 60 && i <= 80) {
            drawable = getResources().getDrawable(R.mipmap.water_base_battery3);
        } else {
            drawable = getResources().getDrawable(R.mipmap.water_base_battery4);
        }
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            baseBatteryTextView.setCompoundDrawables(drawable, null, null, null);
        }
    }

    /**
     * 设置WIFI信号强度
     *
     * @param i 信号强度
     */
    public void setWifiStrLevel(int i) {
        if (i < 0 && i > -50) {
            wifiStrView.setImageResource(R.mipmap.underwater_signal_5);
        } else if (i < -50 && i > -70) {
            wifiStrView.setImageResource(R.mipmap.underwater_signal_4);
        } else if (i < -70 && i > -100) {
            wifiStrView.setImageResource(R.mipmap.underwater_signal_3);
        } else if (i < -100 && i > -200) {
            wifiStrView.setImageResource(R.mipmap.underwater_signal_2);
        } else {
            wifiStrView.setImageResource(R.mipmap.underwater_signal_1);
        }
    }

    public void setPowerPayBatteryText(int bt) {
        bt = bt > 100 ? 100 : bt;
        powerrayBatteryTextView.setText(String.valueOf(bt) + "%");
    }

    public void setBaseBatteryText(int bt) {
        bt = bt > 100 ? 100 : bt;
        baseBatteryTextView.setText(String.valueOf(bt) + "%");
    }

    public void setConnect(boolean connect) {
        if (connect) {
            ifConnected = true;
            connectTextView.setTextColor(getResources().getColor(R.color.obslayout1));
            connectTextView.setText(getResources().getString(R.string.device_already_connect));
        } else {
            ifConnected = false;
            connectTextView.setTextColor(getResources().getColor(R.color.gary));
            connectTextView.setText(getResources().getString(R.string.device_not_connect));
        }

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        L.e(TAG, "onAttachedToWindow");
        mPowerSDK = PowerSDK.getInstance();
        /*本体剩余电量*/
        mPowerSDK.setSysStatusListener(new SystemStatusCallback.SysStatusListener() {
            @Override
            public void onSysStatusChanged(int status) {
//                L.e(TAG, "onSysStatusChanged");
                if (status == 0) {
                    try {
                        SystemStatusParam param = mPowerSDK.getSystemStatusParam();
                        if (param != null) {
                            currentPowerRayBatteryRemain = param.battery_remaining;
                            double v = param.voltage_battery / 1000.00;
                            DecimalFormat df = new DecimalFormat("##0.0");
                           // SPHelperUtils.getInstance(getContext()).saveString(Constant.AIRPLANE_V, df.format(v) + "");
                            titleHandler.sendEmptyMessage(1);
                        }
//                        L.e(TAG, "currentPowerRayBatteryRemain: " + currentPowerRayBatteryRemain);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        /*回调基站剩余电量*/
        mPowerSDK.setBaseStationBatteryListener(new RayCallback.BaseStationBatteryListener() {
            @Override
            public void baseStationBattery(char battery_remaining) {
                currentBaseStationBatteryRemain = battery_remaining;
                if (titleHandler != null) {
                    titleHandler.sendEmptyMessage(2);
                }
            }
        });

        mPowerSDK.setModeChangedListener(new SystemStatusCallback.ModeChangedListener() {
            @Override
            public void onModeChanged() {

            }

            @Override
            public void onModeChangedManual() {

            }

            @Override
            public void onModeChangedAltctl() {

            }

            @Override
            public void onModeChangedPosctl() {

            }

            @Override
            public void onModeChangedAutomission() {

            }

            @Override
            public void onModeChangedAutoTakeoff() {

            }

            @Override
            public void onModeChangedAutoLand() {

            }

            @Override
            public void onModeChangedAutoRtl() {

            }

            @Override
            public void onModeChangedSuperSimple() {

            }

            @Override
            public void onModeChangedAutoCircle() {

            }

            @Override
            public void onModeChangedFollowme() {

            }

            @Override
            public void onModeChangedAutoLoiter() {

            }

            @Override
            public void onModeChangedTimeout() {
                if (titleHandler != null) {
                    titleHandler.sendEmptyMessage(4);
                }
            }
        });
    }

    public void updateWifiStr() {
        if (titleHandler != null) {
            titleHandler.sendEmptyMessage(3);
        }
    }

    /**
     * 退出Activity的时候调用这个方法清除一些回调
     */
    public void onDestroy() {
        titleHandler.removeCallbacksAndMessages(this);
        titleHandler = null;
        mPowerSDK.setModeChangedListener(null);
        mPowerSDK.setBaseStationBatteryListener(null);
        mPowerSDK.setSysStatusListener(null);
    }


    public void setDelegate(TitleViewDelegate titleViewDelegate) {
        this.mDelegate = titleViewDelegate;
    }

    public static class TitleViewDelegate {
        public void onMasterModeClick() {

        }
    }
}
