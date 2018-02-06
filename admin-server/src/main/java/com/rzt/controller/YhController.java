package com.rzt.controller;

import com.rzt.entity.TimedTask;
import com.rzt.service.YhService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return yhService.findYh();
    }
    /**
     * 二级页面显示
     * 隐患统计信息  按照单位分组返回
     * @return
     */
    @GetMapping("/findYhSumTwo")
    public WebApiResponse findYhSumTwo(){
        return yhService.findYhTwo();
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

}
