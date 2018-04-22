package com.lufi.controllers;

/**
 * Created by Sunny on 2018/4/16.
 */

import com.lufi.services.model.DetectHistoryPO;
import com.lufi.services.service.LogService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryController {
    @Autowired
    private LogService logService;

    /**
     * @author sunny
     * 获取某个用户历史记录列表
     */
    @GetMapping("getHistories")
    @ResponseBody
    public Map<String, Object> getHistories(@RequestParam(value = "userId") String userId, HttpServletResponse response){
        response.addHeader("Access-Control-Allow-Origin", "*");
        Map<String, Object> rm = new HashMap<>();
        try{
            List<DetectHistoryPO> histories = logService.getHistories(userId);
            JSONArray jsonArray = new JSONArray();
            if(histories != null && histories.size() > 0){
                for(DetectHistoryPO dh: histories){
                    Map<String, Object> map = new HashMap<>();
                    map.put("id",dh.getId());
                    map.put("files", dh.getDetect_files());
                    map.put("date", dh.getDetect_time());
                    jsonArray.put(map);
                }
                rm.put("code", "200");
                rm.put("historyList",jsonArray.toString());
            }else{
                rm.put("code","404");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return rm;
    }

    /**
     * @author sunny
     * 获取某个详细的历史记录
     */
    @GetMapping("getHistory")
    @ResponseBody
    public Map<String, Object> getHistory(@RequestParam(value = "historyId") String historyId, HttpServletResponse response){
        response.addHeader("Access-Control-Allow-Origin", "*");
        Map<String, Object> rm = new HashMap<>();
        try{
            DetectHistoryPO dh = logService.getHistory(historyId);
            JSONObject jsonOb = new JSONObject();
            if(dh != null){

                jsonOb.put("id", dh.getId());
                jsonOb.put("files", dh.getDetect_files());
                jsonOb.put("date", dh.getDetect_time());
                jsonOb.put("detail", dh.getDetect_detail());
                jsonOb.put("path",dh.getReport_path());
                rm.put("code","200");
                rm.put("history", jsonOb.toString());
            }else{
                rm.put("code","404");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return rm;
    }

    /**
     * @author sunny
     * 删除某个历史记录
     */
    @PostMapping("deleteHistory")
    @ResponseBody
    public Boolean deleteHistory(@RequestParam(value = "historyId") String historyId, HttpServletResponse response){
        response.addHeader("Access-Control-Allow-Origin", "*");
        if(logService.deleteHistory(historyId) == 1)
            return true;
        return false;
    }
}
