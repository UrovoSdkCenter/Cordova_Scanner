package com.urovoscanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.DeviceManager;
import android.device.ScanManager;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class CordovaPluginUrovoScanner extends CordovaPlugin {
    private static final String LOG_TAG = "UrovoScannerPlugin";
    private static final String ACTION_DECODE = "android.intent.ACTION_DECODE_DATA";
    private static final String ACTION_KEYCODE_SCAN_PRESSED = "ACTION_KEYCODE_SCAN_PRESSED";//获取按键广播
    private static final String BARCODE_STRING_TAG = "barcode_string";
    private static final String BARCODE_TYPE_TAG = "barcodeType";
    private static final String BARCODE_LENGTH_TAG = "length";
    private static final String DECODE_DATA_TAG = "barcode";

    private ScanManager scanManager = new ScanManager();
    private CallbackContext callback = null;
    private UrovoBroadcastReceiver broadcastReceiver = new UrovoBroadcastReceiver();

    @Override
    protected void pluginInitialize() {
        super.initialize(cordova, webView);
        boolean powerOn = scanManager.getScannerState();
        if (!powerOn) {
            powerOn = scanManager.openScanner();
        }
        scanManager.switchOutputMode(0);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("register")) {
            callback = callbackContext;
            register();
            return true;
        }
        if (action.equals("unregister")) {
            callback = null;
            return true;
        } else if (action.equals("scan")) {
            if (scanManager != null) {
                scanManager.startDecode();
            }
            return true;
        } else if (action.equals("cancel")) {
            if (scanManager != null) {
                scanManager.stopDecode();
            }
            return true;
        } else if (action.equals("setScanKey")) {
            String key = args.getString(0);
            enableScanHead(key);
            callbackContext.success("");
            return true;
        }
        return false;
    }

    private void register() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_DECODE);
        filter.addAction(ACTION_KEYCODE_SCAN_PRESSED);
        webView.getContext().registerReceiver(broadcastReceiver, filter);
    }


    private void unregister() {
        try {
            webView.getContext().unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
            LOG.d(LOG_TAG, "Error unregistering Urovo mobile receiver: " + e.getMessage(), e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregister();
    }

    public class UrovoBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            JSONObject json = new JSONObject();
            if (action.equals(ACTION_DECODE)) {
                // Get scan results, including string and byte data etc.
                byte[] barcode = intent.getByteArrayExtra(DECODE_DATA_TAG);
                int barcodeLen = intent.getIntExtra(BARCODE_LENGTH_TAG, 0);
                byte temp = intent.getByteExtra(BARCODE_TYPE_TAG, (byte) 0);
                String barcodeStr = intent.getStringExtra(BARCODE_STRING_TAG);
                try {
                    json.put("action", action);
                    json.put("barcode", barcodeStr);
                    json.put("type", barcodeLen);
                } catch (Exception e) {
                    LOG.d(LOG_TAG, "Error sending urovo scanner receiver: " + e.getMessage(), e);
                }
            } else if (action.equals(ACTION_KEYCODE_SCAN_PRESSED)) {
                try {
                    String keyAction = intent.getStringExtra("action");//down:按下  up:抬起
                    int keyCode = intent.getIntExtra("keyCode", 0);//按键键值
                    long downTime = intent.getLongExtra("time", 0L);//按下时间
                    json.put("keyAction", keyAction);
                    json.put("keyCode", keyCode);
                    json.put("pressTime", downTime);
                } catch (Exception e) {
                    LOG.d(LOG_TAG, "Error sending urovo keyevent receiver: " + e.getMessage(), e);
                }
            }
            if (callback != null) {
                PluginResult result = new PluginResult(PluginResult.Status.OK, json);
                result.setKeepCallback(true);
                callback.sendPluginResult(result);
            }
        }
    }


    /**
     * 是否对应按键键值使扫描头出光
     *
     * @param keyEvStr 520-521-522-523-
     */
    public static void enableScanHead(String keyEvStr) {
        Log.d("urovoScan", "==enableScanHead keyCode:" + keyEvStr);
        DeviceManager mDeviceManager = new DeviceManager();
        mDeviceManager.setSettingProperty("persist-persist.sys.rfid.key", "0-");
        mDeviceManager.setSettingProperty("persist-persist.sys.scan.key", keyEvStr);//这里入参传入了哪些键值，在按下键值的的时候就会调起扫描头出光
    }
}
