package jp.tf_web.sample01.extra.konashi;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.StringBuilderPrinter;

import com.uxxu.konashi.lib.Konashi;
import com.uxxu.konashi.lib.KonashiListener;
import com.uxxu.konashi.lib.KonashiManager;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

import info.izumin.android.bletia.BletiaException;

/** Konashi ユーテリティ
 *
 * Created by furukawanobuyuki on 2016/08/17.
 */
public class KonashiUtil {
    private static String TAG = KonashiUtil.class.getName();

    private static KonashiUtil ourInstance = new KonashiUtil();

    public static KonashiUtil getInstance() {
        return ourInstance;
    }

    //Konashiマネージャー
    private KonashiManager konashiManager;

    private Context context;

    public static final String BROADCAST_ACTION = "com.ux_xu.jstmail.util.command.action.konashi";
    public static final String BROADCAST_KEY_CMD = "CMD";
    public static final String BROADCAST_VALUE_CMD_UART = "CMD_UART";
    public static final String BROADCAST_KEY_VALUE = "VALUE";


    private KonashiUtil() {
    }

    //初期化
    public void initialize(final Context context){
        this.context = context;

        this.konashiManager = new KonashiManager(context);
        //リスナー設定
        this.konashiManager.addListener(this.konashiListener);
    }

    //Konashi 検索
    public void find(final Activity activity){
        this.konashiManager.find(activity);
    }

    //Konashi 検索
    public void findWithName(final Activity activity, final String name){
        this.konashiManager.findWithName(activity,name);
    }

    //Konashi 切断
    public void disconnect(){
        if((this.konashiManager == null) || (this.konashiManager.isConnected() == false)) return;
        this.konashiManager.disconnect();
    }

    //UART 書き込み
    public void uartWrite(final byte[] value){
        if((this.konashiManager == null) || (this.konashiManager.isConnected() == false)) return;
        this.konashiManager.uartWrite(value);
    }

    //Konashi 接続 イベント
    private final KonashiListener konashiListener = new KonashiListener() {

        @Override
        public void onConnect(KonashiManager manager) {
            konashiManager.uartMode(Konashi.UART_ENABLE)
                    .then(new DoneCallback<BluetoothGattCharacteristic>() {
                        @Override
                        public void onDone(BluetoothGattCharacteristic result) {
                            konashiManager.uartBaudrate(Konashi.UART_RATE_115K2);
                        }
                    })
                    .fail(new FailCallback<BletiaException>() {
                        @Override
                        public void onFail(BletiaException result) {
                            //Toast.makeText(self, result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        @Override
        public void onDisconnect(KonashiManager manager) {

        }

        @Override
        public void onError(KonashiManager manager, BletiaException e) {

        }

        @Override
        public void onUpdateUartRx(KonashiManager manager, byte[] value) {
            Log.d(TAG,"onUpdateUartRx value length:"+value.length);

            //IntentでUIに通知する
            Intent intent = new Intent(BROADCAST_ACTION);
            intent.putExtra(BROADCAST_KEY_CMD,BROADCAST_VALUE_CMD_UART);
            intent.putExtra(BROADCAST_KEY_VALUE, value);
            KonashiUtil.this.context.sendBroadcast(intent);
        }

        @Override
        public void onUpdatePioOutput(KonashiManager manager, int value) {

        }

        @Override
        public void onUpdateSpiMiso(KonashiManager manager, byte[] value) {

        }

        @Override
        public void onUpdateBatteryLevel(KonashiManager manager, int level) {

        }
    };
}
