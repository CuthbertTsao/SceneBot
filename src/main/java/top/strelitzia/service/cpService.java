package top.strelitzia.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import top.angelinaBot.annotation.AngelinaGroup;
import top.angelinaBot.model.MessageInfo;
import top.angelinaBot.model.ReplayInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

@Service
public class cpService {

    @AngelinaGroup(keyWords = {"cp"}, description = "cp名字和名字")
    public ReplayInfo cp(MessageInfo messageInfo) throws IOException {
        ReplayInfo replayInfo = new ReplayInfo(messageInfo);
        String str = messageInfo.getText();
        int num = str.indexOf("和");
        String name1 = str.substring(4,num);
        String name2 = str.substring(num+1);
        String url = "https://api.xingzhige.com/API/cp_generate_2/?name1="+name1+"&name2="+name2;
        String reply = cpText(loadJson(url)).replace("【","");
        reply = reply.replace("】","");
        replayInfo.setReplayMessage(reply);
        return replayInfo;
    }

    public static String loadJson(String url) throws IOException{
        StringBuilder json = new StringBuilder();
        try {
            URL urlObject = new URL(url);
            URLConnection uc = urlObject.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream(),"UTF-8"));
            String inputLine = null;
            while ( (inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public static String cpText(String jsonStr){
        JSONObject root = new JSONObject(jsonStr);// 将json格式的字符串转换成json
        JSONObject data = root.getJSONObject("data");
        return data.getString("content");
    }




}