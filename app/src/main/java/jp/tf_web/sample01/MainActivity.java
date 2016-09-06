package jp.tf_web.sample01;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.text.ParseException;

import jp.tf_web.sample01.extra.konashi.CmdPack;
import jp.tf_web.sample01.extra.konashi.KonashiReceiver;
import jp.tf_web.sample01.extra.konashi.KonashiUtil;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    //Konashiからのデータを受け取る為のレシーバー
    private KonashiReceiver konashiReceiver;

    //BLEで受け取ったパケット
    private CmdPack cmdPack = null;

    private TextView txtLogs;

    private StringBuffer logBuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.logBuffer = new StringBuffer();

        this.txtLogs = (TextView) findViewById(R.id.txtLogs);

        Button btnFind = (Button) findViewById(R.id.btnFind);
        btnFind.setOnClickListener(this.clickBtnFind);

        Button btnDisconnect = (Button) findViewById(R.id.btnDisconnect);
        btnDisconnect.setOnClickListener(this.clickBtnDisconnect);

        //Konashi 関連の処理をするユーテリティ初期化
        KonashiUtil.getInstance().initialize(getApplicationContext());

        //Konashiからの通知を受け取るレシーバー
        this.konashiReceiver = KonashiReceiver.registerReceiver(getApplicationContext(),this.callbackKonashiReceiver);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        //konashiとの接続解除
        KonashiUtil.getInstance().disconnect();
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private View.OnClickListener clickBtnFind = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            //ログを削除
            logBuffer.delete(0,logBuffer.length());
            MainActivity.this.txtLogs.setText("");

            //Kinashiとの接続
            KonashiUtil.getInstance().find(MainActivity.this);
        }
    };

    private View.OnClickListener clickBtnDisconnect = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            KonashiUtil.getInstance().disconnect();
        }
    };


    //Konashiからのイベント受け取り
    private KonashiReceiver.CallbackKonashiReceiver callbackKonashiReceiver = new KonashiReceiver.CallbackKonashiReceiver(){
        @Override
        public void onUpdateUartRx(byte[] value) {
            String hex = CommonUtil.getInstance().toHexString(value);
            if(value.length != 3) {
                Log.e(TAG,"onUpdateUartRx value length:"+value.length);
                Log.e(TAG, " hex:" + hex);
                return;
            }
            else{
                Log.d(TAG,"onUpdateUartRx value length:"+value.length);
                Log.d(TAG, " hex:" + hex);
            }

            //コマンド 構造体を作成する
            try {
                if(cmdPack == null) {
                    cmdPack = new CmdPack();
                }
                cmdPack.write(value);
                try {
                    if (cmdPack.parse()) {
                        String row = "cmdPack size:" + cmdPack.getSize() + " type:" + cmdPack.getType()+"\n";
                        //コマンド化に成功
                        Log.d(TAG, row);

                        MainActivity.this.logBuffer.insert(0,row);
                        MainActivity.this.logBuffer.setLength(600);

                        MainActivity.this.txtLogs.setText(MainActivity.this.logBuffer.toString());

                        //受信コマンドをエコーしてみる
                        KonashiUtil.getInstance().uartWrite(cmdPack.toByteArray());

                        cmdPack = null;
                    }
                }catch (ParseException e){
                    e.printStackTrace();
                    //パース失敗なので次のパケットを処理する為に初期化
                    cmdPack = null;
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    };
}
