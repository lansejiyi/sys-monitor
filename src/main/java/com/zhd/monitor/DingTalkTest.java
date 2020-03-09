package com.zhd.monitor;

import com.alibaba.fastjson.JSON;
import com.zhd.components.utils.HttpClientUtils.HttpClient;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DingTalkTest {

   public void sendMessage(){
       try {
           //钉钉机器人地址（配置机器人的webhook）
           String dingUrl = "https://oapi.dingtalk.com/robot/send?access_token=4acd1578e885c544ec5b0ab69182dcd9733214dd4924c55b84a1ac6bc4b3c55c";
           String secret = "SECcc12c8d434deb039a1641cb63f0ce5d43b0fd429a936f0814fef0d79b346b363";
           Long timestamp = System.currentTimeMillis();
           String stringToSign = timestamp + "\n" + secret;
           Mac mac = Mac.getInstance("HmacSHA256");
           mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
           byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
           String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)),"UTF-8");
           String url = dingUrl+"&timestamp="+timestamp+"&sign="+sign;
           //是否通知所有人
           boolean isAtAll = false;
           //通知具体人的手机号码列表
           List<String> mobileList =new ArrayList<>();

           //钉钉机器人消息内容
           String content = "你好";
           //组装请求内容
           String reqStr = buildReqStr(content, isAtAll, mobileList);
            System.out.println(url);
           //推送消息（http请求）
           String result = HttpClient.sendHttpPost(url, reqStr);
           System.out.println("result == " + result);
       }catch (Exception e){
           e.printStackTrace();

       }

   }

    public String buildReqStr(String content, boolean isAtAll, List<String> mobileList) {
        //消息内容
        Map<String, String> contentMap = new HashMap<>();
        contentMap.put("content", content);
        //通知人
        Map<String, Object> atMap = new HashMap<>();
        //1.是否通知所有人
        atMap.put("isAtAll", isAtAll);
        //2.通知具体人的手机号码列表
        atMap.put("atMobiles", mobileList);

        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("msgtype", "text");
        reqMap.put("text", contentMap);
        reqMap.put("at", atMap);
        return JSON.toJSONString(reqMap);

    }



}
