package com.rzt.controller;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.TimedTask;
import com.rzt.service.YhService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 李成阳
 * 2018/1/31
 * 管理app隐患数据统计
 */
@RestController
@RequestMapping("/YH")
public class YhController extends CurdController<TimedTask, YhService>  {
    @Autowired
    private YhService yhService;

    /**
     * 一级页面显示
     * 隐患统计信息
     * @return
     */
    @GetMapping("/findYhSum")
    public WebApiResponse findYh(){
        return yhService.findYh1();
    }


    /**
     * 二级页面显示
     * 隐患统计信息  按照单位分组返回
     * @return
     */
    @GetMapping("/findYhSumTwo")
    public WebApiResponse findYhSumTwo(){
        return yhService.findYh();
    }
    /**
     * 三级页面显示
     * 隐患统计信息  返回一个单位
     * @return
     */
    @GetMapping("/findYhSumThree")
    public WebApiResponse findYhSumThree(String deptId){
        return yhService.findYhThree(deptId);
    }

    /**
     * 获取隐患列表数据
     * @param page
     * @param size
     * @param kv
     * @param lineId
     * @param yhjb
     * @param deptId
     * @return
     */
    @GetMapping("getYHInfo")
    public Page<Map<String, Object>> getYHInfo(@RequestParam(value = "page",defaultValue = "0") Integer page, @RequestParam(value = "size",defaultValue = "15") Integer size,  String kv, String lineId, String yhjb, String deptId){
        Pageable pageable = new PageRequest(page, size);
        return yhService.getYHInfo( pageable,kv,lineId,yhjb,deptId);
    }

}
