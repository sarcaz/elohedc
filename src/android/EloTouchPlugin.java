package com.initzero.elotouch;

import android.content.Context;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import com.elotouch.library.EloPeripheralManager;
import com.elotouch.library.EloPeripheralEventListener;
//import android.elo.peripheral.ELOPeripheralEventListener;
//import android.elo.peripheral.ELOPeripheralManager;

public class EloTouchPlugin extends CordovaPlugin  {
    //private static final String TAG = "HoneywellScanner";
	private CallbackContext callbackContext;
    private static final String BCR_DEVICE_NAME = "Barcode Decoder";
    private static final String POWER_HIGH = "1";
    public static final String ACTION_USB_PERMISSION = "com.elo.peripheraltool.USB_PERMISSION";
    public static final int BCR_DEVICE = 2;
    private static final int NOT_SPECIAL_DEVICE = 3;
    private static final int BCR_DATA = 100;
    private static final int CHANGE_BCR_DEVICE_STATUS = 101;
    private static final int BCR_TRIGGER_TIME = 100 * 1000; // 1 sec
    public static final int BCR_VID = 1155;
	private EloPeripheralManager mEloManager;
	/*private HEDCUsbCom m_engine;
	private String    m_Codabar         = "";
    private byte m_presentation_mode    =  3;
    private byte m_manual_mode          =  0;
    private String[] AsciiTab = {
            "NUL", "SOH", "STX", "ETX", "EOT", "ENQ", "ACK", "BEL",	"BS",  "HT",  "LF",  "VT",
            "FF",  "CR",  "SO",  "SI",	"DLE", "DC1", "DC2", "DC3", "DC4", "NAK", "SYN", "ETB",
            "CAN", "EM",  "SUB", "ESC", "FS",  "GS",  "RS",  "US",	"SP",  "DEL",
    };
	public String ConvertToString(byte[] data, int length)
    {
        String s = "";
        String s_final = "";
        for (int i = 0; i < length; i++) {
            if ((data[i]>=0)&&(data[i] < 0x20 ))
            {
                s = String.format("<%s>",AsciiTab[data[i]]);
            }
            else if (data[i] >= 0x7F)
            {
                s = String.format("<0x%02X>",data[i]);
            }
            else {
                s = String.format("%c", data[i]&0xFF);
            }
            s_final+=s;
        }

        return s_final;
    }*/
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Log.d("BCR", "initialize");

        Context context = cordova.getActivity().getApplicationContext();
        mEloManager = new EloPeripheralManager(context, new PeripheralEventListener());
        mEloManager.OnResume();

		//mEloManager.activeBcr();
    }
	@Override
    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if(action.equals("listenForScans")){
            Log.d("BCR", "listenForScans");

            this.callbackContext=callbackContext;
            PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
            result.setKeepCallback(true);
            this.callbackContext.sendPluginResult(result);
            mEloManager.activeBcr();
            mEloManager.disactiveBcr();
			}
        return true;
    }
	private class PeripheralEventListener implements EloPeripheralEventListener {
        /*@Override
        public void onEvent(int i, String s) {
            Log.d("BCR", "onEvent: ");
        }

        @Override
        public void onEvent(int i, int i1) {
            Log.d("BCR", "onEvent: ");

        }

        @Override
        public void onEvent(int i) {
            Log.d("BCR", "onEvent: ");

        }*/

        @Override
        public void onEvent(int state, String data) {
            // TODO Auto-generated method stub
			//if(this.callbackContext!=null)
			//{
            Log.d("BCR", "onEvent: "+state+" "+data);

				switch(state) {
				case EloPeripheralEventListener.BCR_STATE_DEVICE_CONNECTION:
				case EloPeripheralEventListener.BCR_STATE_DEVICE_DISCONNECTION:
				case EloPeripheralEventListener.BCR_STATE_PIN_AUTO_DISABLE:
					break;
				case EloPeripheralEventListener.BCR_STATE_DATA_RECEIVIED:
                    Log.d("BCR", "onEvent: "+data);
                    NotifyResult(data);
					//PluginResult result = new PluginResult(PluginResult.Status.OK, data);
					//result.setKeepCallback(true);
					//this.callbackContext.sendPluginResult(result);
					break;
				}
			//}
		}
        @Override
        public void onEvent(int state) {
            // TODO Auto-generated method stub
            Log.d("BCR", "onEvent: "+state);
            onEvent(state, null);
        }  
		@Override
        public void onEvent(int pinNumber, int state) {
            // TODO Auto-generated method stub
            Log.d("BCR", "onEvent: "+state);

        }
    }
    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        if (mEloManager != null) {
            try {
                mEloManager.OnResume();
                mEloManager.activeBcr();
            } catch (Exception e) {
                e.printStackTrace();
                NotifyError("Scanner unavailable");
            }
        }
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
        if (mEloManager != null) {
            // release the scanner claim so we don't get any scanner
            // notifications while paused.
            mEloManager.OnPause();
            //mEloManager.disactiveBcr();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mEloManager != null) {
            // close BarcodeReader to clean up resources.
            //mEloManager.disactiveBcr();
            mEloManager = null;
        }

    }
	private void NotifyError(String error){
        if(this.callbackContext!=null)
        {
            PluginResult result = new PluginResult(PluginResult.Status.ERROR, error);
            result.setKeepCallback(true);
            this.callbackContext.sendPluginResult(result);
        }
    }
	public void NotifyResult(String data){
        if(this.callbackContext!=null)
        {
            PluginResult result = new PluginResult(PluginResult.Status.OK, data);
            result.setKeepCallback(true);
            this.callbackContext.sendPluginResult(result);
        }
    }

}