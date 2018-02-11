package com.youli.oldageassess.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by liutao on 2017/8/4.
 */

public class IOUtil {

    public static byte[] getBytesByStream(InputStream inStream){

        ByteArrayOutputStream baos=new ByteArrayOutputStream();

        byte [] data=null;

        byte[] buffer=new byte[1024];
        int len=0;
        try {
            while ((len=inStream.read(buffer))!=-1){
                baos.write(buffer,0,len);
            }
            data=baos.toByteArray();
            inStream.close();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

}
