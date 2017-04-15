package com.tsunami.timeapp.util;


import com.tsunami.timeapp.R;

/**
 * @author wangshujie
 */
public class WeatherIconUtil {

    static public int getWeatherIcon(String code) {
        switch (Integer.parseInt(code)) {
            case 100:
                return R.drawable.icon100;
            case 101:
                return R.drawable.icon101;
            case 102:
                return R.drawable.icon102;
            case 103:
                return R.drawable.icon103;
            case 104:
                return R.drawable.icon104;
            case 200:
            case 203:
            case 204:
                return R.drawable.icon200;
            case 201:
            case 202:
                return R.drawable.icon201;
            case 205:
            case 206:
            case 207:
                return R.drawable.icon205;
            case 208:
            case 209:
            case 210:
            case 211:
            case 212:
            case 213:
                return R.drawable.icon208;
            case 300:
                return R.drawable.icon300;
            case 301:
                return R.drawable.icon301;
            case 302:
                return R.drawable.icon302;
            case 303:
                return R.drawable.icon303;
            case 304:
                return R.drawable.icon304;
            case 305:
                return R.drawable.icon305;
            case 306:
                return R.drawable.icon306;
            case 307:
                return R.drawable.icon307;
            case 308:
                return R.drawable.icon308;
            case 309:
                return R.drawable.icon309;
            case 310:
                return R.drawable.icon310;
            case 311:
                return R.drawable.icon311;
            case 312:
                return R.drawable.icon312;
            case 313:
                return R.drawable.icon313;
            case 400:
                return R.drawable.icon400;
            case 402:
                return R.drawable.icon402;
            case 403:
                return R.drawable.icon403;
            case 404:
                return R.drawable.icon404;
            case 405:
                return R.drawable.icon405;
            case 406:
                return R.drawable.icon406;
            case 407:
                return R.drawable.icon407;
            case 500:
                return R.drawable.icon500;
            case 501:
                return R.drawable.icon501;
            case 502:
                return R.drawable.icon502;
            case 503:
                return R.drawable.icon503;
            case 504:
                return R.drawable.icon504;
            case 507:
                return R.drawable.icon507;
            case 508:
                return R.drawable.icon508;
            case 900:
                return R.drawable.icon900;
            case 901:
                return R.drawable.icon901;
            default:
                return R.drawable.icon999;
        }
    }
}
