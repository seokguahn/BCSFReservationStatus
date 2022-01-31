package com.example.firstjava;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ItemWeatherInfo {
    public String ta;
    public String air;
    public String stn_id;
    public String cai;
    public String strdate;
    public String weatherdesc;
    public String imageSrc;

    ItemWeatherInfo(String response) {
        try {
            JSONObject jObject = new JSONObject(response).getJSONObject("weatherInfo");
            ta = jObject.optString("ta");
            air = jObject.optString("air");
            stn_id = jObject.optString("stn_id");
            cai = jObject.optString("cai");
            strdate = jObject.optString("strdate");
            weatherdesc = jObject.optString("weatherdesc");
            imageSrc = getImageSrc();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getImageSrc() {
        String imeageSrc = "http://reserv.bucheon.go.kr/images/egovframework/com/bucheon/ctzn/main/";

        if(weatherdesc == "맑음") {
            imeageSrc = imeageSrc + "weathericon_new_d1.png";
        }
        else if(weatherdesc == "구름조금") {
            imeageSrc = imeageSrc + "weathericon_new_d2.png";
        }
        else if(weatherdesc == "구름많음") {
            imeageSrc = imeageSrc + "weathericon_new_d3.png";
        }
        else if(weatherdesc == "흐림") {
            imeageSrc = imeageSrc + "weathericon_new_d4.png";
        }
        else if(weatherdesc == "비") {
            imeageSrc = imeageSrc + "weathericon_new_d5.png";
        }
        else if(weatherdesc == "눈") {
            imeageSrc = imeageSrc + "weathericon_new_d6.png";
        }
        else if(weatherdesc == "눈비") {
            imeageSrc = imeageSrc + "weathericon_new_d7.png";
        }
        else if(weatherdesc == "천둥번개") {
            imeageSrc = imeageSrc + "weathericon_new_d8.png";
        }
        else{
            imeageSrc = imeageSrc + "weathericon_new_d1.png";
        }

        return imeageSrc;
    }
}
