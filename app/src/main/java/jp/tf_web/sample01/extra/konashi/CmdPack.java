package jp.tf_web.sample01.extra.konashi;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.ParseException;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by furukawanobuyuki on 2016/08/20.
 */
public class CmdPack {
    private static final String TAG = CmdPack.class.getName();

    @Getter
    @Setter
    private byte size;

    @Getter
    @Setter
    private int type;

    private ByteArrayOutputStream baoStream;

    public CmdPack(){
        baoStream =  new ByteArrayOutputStream();
    };

    //バッファを追加する
    public void write(byte[] b) throws IOException {
        baoStream.write(b);
        baoStream.flush();
    }

    //
    public boolean parse() throws ParseException{
        boolean result = false;
        ByteBuffer byteBuffer = ByteBuffer.wrap(baoStream.toByteArray());
        //バイトオーダー リトルエンディアン指定して 数値を読み込む場合
        //byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        this.size = byteBuffer.get();
        if (size == baoStream.size()) {
            this.type = (int) byteBuffer.getShort();
            //TODO: なんらかの失敗をした場合
            // throw new ParseException("XXXXXX");
            //parse 成功
            result = true;
        }
        return result;
    }

    //現在のバッファを配列として返す
    public byte[] toByteArray(){
        if(baoStream.size() == 0){
            baoStream.write(size);
            baoStream.write(type);
            try {
                parse();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return baoStream.toByteArray();
    }
}
