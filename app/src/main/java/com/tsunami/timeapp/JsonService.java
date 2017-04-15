package com.tsunami.timeapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shenxiaoming
 */
public class JsonService {
    //static String url="https://192.168.1.102:8080/ServeNew/arrangeCheck";
    public boolean flag=true;
    private String clientName;
    static String pw=null;
    private JSONArray ja=new JSONArray();
    public JsonService(){};
	public JsonService(String name) {
		clientName = name;
	}
    public void monthTable(String date,String client){
        String day=date+"27";
        dayTable(day,client);
    }

    public void getAll(String client){
        String url="http://192.168.1.119:8080/ServeNew/arrangeCheck";
        System.out.println(url);
        clientName=client;
        RequestParams rp = new RequestParams(url);

        String op="loadTable";
        rp.addBodyParameter("op", op);
        rp.addBodyParameter("client", client);
        x.http().post(rp, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                pw=result;
                try {
                    JSONArray json=new JSONArray(result);
                    System.out.println("reloding");
                    reUpdate(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                
                System.out.println("result+"+result);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                pw="error";
                System.out.println("error");

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                System.out.println("finish");
            }
        });
    }

	
	private int index =0;
	public Boolean complete=false;
	public Boolean getFlagUpdate(){
		if (datetime.allDate.size()>index) return false;
		return true;
	}
	
    public void reUpdate(JSONArray json){
        datetime=new dateTime();
        try {
            JSONArray nJson=json.getJSONArray(0);
            System.out.println(nJson);
			index=0;
            for (int i=0;i<nJson.length();i++){
                String date=nJson.getString(i);
                System.out.println(date);
                dayTable(date,clientName);
                datetime.allDate.add(date);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * 4
     * 一个测试用的下载后端客户一天日程安排的函数
     * @param date 日期 目前格式是“YYYYMMDD”
     * @param client 客户名
     * @return 目前没有返回值
     * 接收到数据后，会拿到JSON数据包然后进行刷新refresh操作。
     */
    public String dayTable(String date,String client) {
       // @Override
        String url="http://192.168.1.119:8080/ServeNew/arrangeCheck";
        RequestParams rp = new RequestParams(url);

        String op="load";
        rp.addBodyParameter("op", op);
        rp.addBodyParameter("client", client);
        rp.addBodyParameter("date", date);
        final String[] rs = {"default"};
        x.http().post(rp, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                pw=result;
                try {
                    JSONArray jsonObject=new JSONArray(result);
                    ja=jsonObject;
                    refresh();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println("result+"+result);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                pw="error";
                System.out.println("error");

            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
        return pw+"ok";
    }

    /**
     * 4
     * 一个刷新函数，在接受时拿到JSON数据，然后进行处理，将一天的日程写进list中
     * 2016.9,3
     * 添加了时间
     */
    public void refresh(){
        System.out.println("refresh");
        try {
            JSONArray js=ja.getJSONArray(0);
            System.out.println(js);
            dayWork dt=new dayWork();
            String date=null;
            for (int i=0;i<js.length();i++){
                JSONObject joi=js.getJSONObject(i);
                work w=new work();
                w.workname=joi.getString("workname");
                w.starttime=joi.getInt("starttime");
                w.endtime=joi.getInt("endtime");
                dateTime datetime=new dateTime();
                w.worktime=datetime.getTimeFromInt(w.starttime)+'~'+datetime.getTimeFromInt(w.endtime);
                date=joi.getString("date");
                dt.work.add(w);
            }
            list.put(date,dt);
        } catch (JSONException e) {
            e.printStackTrace();
        }
		index++;
		complete=getFlagUpdate();
    }

    public void getInterview(String client,String friend,String date){
        String url="http://192.168.1.119:8080/ServeNew/arrangeCheck";
        RequestParams rp = new RequestParams(url);

        String op="interview";
        rp.addBodyParameter("op", op);
        rp.addBodyParameter("client", client);
        rp.addBodyParameter("friend", friend);
        rp.addBodyParameter("date", date);
        x.http().post(rp, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                pw=result;
                try {
                    JSONArray jsonArray=new JSONArray(result);
                    reInterview(jsonArray.getJSONArray(0));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                System.out.println("result+"+result);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                pw="error";
                System.out.println("error");

            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void reInterview(JSONArray jsonArray) {
        System.out.println(jsonArray);
        freeTime=new ArrayList<Map<String,Object>>();
        for (int i=0;i<jsonArray.length();i++){
            try {
                JSONObject jo=jsonArray.getJSONObject(i);
                Map<String,Object> map=new HashMap<String,Object>();
                int begin=jo.getInt("begin"),end=jo.getInt("end");
                map.put("begin",jo.getInt("begin"));
                map.put("end",jo.getInt("end"));
                //map.put("time",datetime);
                freeTime.add(map);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 4
     * list 是一个日期对应——日程的Map数据
     * 日期为String 目前格式为“YYYYMMDD”
     * 日程格式为dayWork
     */
    public Map<String,dayWork> list=new HashMap<>();
    public List<Map<String,Object>> freeTime =null;
    /**
     * 4
     * 2016.9.3
     * dayWork是一个表示一天工作日程的类
     * 目前只含有work列表
     */
    public class dayWork{
        public List<work> work=new ArrayList<work>();
    }
    public String getTitle(String date){

        return date;
    }

    /**
     * 4
     * work是一个含有工作时间workname 工作内容（目前为worktime）的类
     * 2016.9.3
     * 添加了 起始时间starttime 终止时间endtime
     */
    public class work{
        public String workname,worktime;
        public int starttime,endtime;
    }

    /**
     * 2016.9.3
     * 一个将时间日期精细化处理的函数
     */
    public class dateTime{
        public List<String> allDate=new ArrayList<String>();
        public String getTimeFromInt(int time){
            int hou=time/60,min=time%60;
            String h=String.valueOf(hou);
            String m=String.valueOf(min);
            return h+':'+m;
        }
    }
    public dateTime datetime=new dateTime();
    private void setPW(String result){
        pw=result;
    }
    public static void main(){

    }

    /**
     * 4
     *
     * 2016.9.3 updateTest八月最后一天写的一个测试用的上传函数
     * 作用是发送一个上传的请求致使服务器自动添加一个日程
     * 用于测试连接成功 被应用在calendarListView中
     */
    public  void updateTest(){
        String url="http://192.168.1.119:8080/ServeNew/arrangeCheck";
        RequestParams rp = new RequestParams(url);

        String op="update";
        rp.addBodyParameter("op", op);
        x.http().post(rp, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println("result+"+result);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                pw="error";

                System.out.println("error");

            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 4
     * 2016.9.3 昨天写的一个加入了测试可上传日程的日期时间的函数
     * 目前被用于SublimePicker中 除了日期时间 其余参数为固定
     * 名称为UPDATA-TEST 表格为testtable 星期为ofweek 内容为null 目前在服务器内部修改为起始时间的字符串
     *
     * @param date 日程的日期
     * @param time 日程的起始时间（终止时间默认为起始时间+1）
     *
     */
    public void updateArrangement(String date,int time){
        String url="http://192.168.1.119:8080/ServeNew/arrangeCheck";
        RequestParams rp = new RequestParams(url);
        System.out.println("ok");
        String op="update";
        int time1=time;
        rp.addBodyParameter("op",op);
        rp.addBodyParameter("workname", "UPDATE-TEST");
        rp.addBodyParameter("client", "testtable");
        rp.addBodyParameter("starttime", String.valueOf(time));
        rp.addBodyParameter("endtime",String.valueOf(time+1));
        rp.addBodyParameter("ofweek","ofweek");
        rp.addBodyParameter("worktext", "null");
        rp.addBodyParameter("date", date);


        x.http().post(rp, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                System.out.println("error");
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
                System.out.println("finshi");
            }
        });
    }
}
