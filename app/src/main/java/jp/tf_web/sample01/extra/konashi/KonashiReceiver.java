package jp.tf_web.sample01.extra.konashi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/** Konashi の受信イベントを受け取る為のレシーバー
 *
 * Created by furukawanobuyuki on 2016/08/17.
 */

public class KonashiReceiver extends BroadcastReceiver {
    private static final String TAG = KonashiReceiver.class.getName();

    private CallbackKonashiReceiver callback;

    //コンストラクタ
    public KonashiReceiver(CallbackKonashiReceiver callback){
        this.callback = callback;
    }

    //レジスター
    public static KonashiReceiver registerReceiver(final Context context, final CallbackKonashiReceiver callback){
        KonashiReceiver receiver = new KonashiReceiver(callback);
        //BROADCAST_ACTIONでフィルター
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(KonashiUtil.BROADCAST_ACTION);
        context.registerReceiver(receiver,intentFilter);
        return receiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(KonashiUtil.BROADCAST_ACTION.equals( action )){
            String cmd = intent.getStringExtra(KonashiUtil.BROADCAST_KEY_CMD);
            if(cmd.equals(KonashiUtil.BROADCAST_VALUE_CMD_UART)){
                //UART の場合 コールバック
                byte[] value = intent.getByteArrayExtra(KonashiUtil.BROADCAST_KEY_VALUE);
                callback.onUpdateUartRx(value);
            }
            //TODO:他のメソッドが必要ならここに実装
        }
    }

    //コールバック通知
    public interface CallbackKonashiReceiver {
        void onUpdateUartRx(byte[] value);
    }
}
