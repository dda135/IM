package mine.fanjh.encryption;

import java.util.UUID;

/**
 * 随机生成字符串
 * Created by wsdevotion on 15/10/26.
 */
public class GetKey {

    public static String getKey(int length) {
        String s = UUID.randomUUID().toString();
        String[] str = s.split("-");
        s = "";
        for (String st : str) {
            s = s + st;
        }

        String string = s.substring(0, length);
        return string;
    }
}
