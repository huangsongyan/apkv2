package com.meituan.android.walle;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args){

        try {
            File file = new File("/Users/hsy/Documents/demo/GameTrackSDk/app/build/outputs/apk/debug/app-debug.apk");

            Map<String,String> map = new HashMap<>();
            map.put("name","hsy_yes");
            map.put("age","22123666");
            ChannelWriter.put(file,"hsy",map);
            ChannelInfo channelInfo = ChannelReader.get(file);
            if(channelInfo!=null){
                String channel = channelInfo.getChannel();
                System.out.println("channel:"+channel);
                String name = channelInfo.getExtraInfo().get("name");
                System.out.println("name:"+name);
                String age = channelInfo.getExtraInfo().get("age");
                System.out.println("age:"+age);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
