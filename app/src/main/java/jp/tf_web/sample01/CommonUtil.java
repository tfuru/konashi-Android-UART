package jp.tf_web.sample01;

/**
 * Created by furukawanobuyuki on 2016/08/30.
 */

public class CommonUtil {
    private static CommonUtil ourInstance = new CommonUtil();

    public static CommonUtil getInstance() {
        return ourInstance;
    }

    private CommonUtil() {
    }

    /** 文字列に変換
     *
     * @param src
     * @return
     */
    public String toHexString(final byte[] src){
        StringBuffer buf = new StringBuffer();
        for(byte b:src){
            String s = String.format("%02x", b & 0xff);
            buf.append(s).append(",");
        }
        return buf.toString();
    }
}
