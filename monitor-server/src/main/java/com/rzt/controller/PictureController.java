package com.rzt.controller;

import com.rzt.service.PictureService;
import com.rzt.service.XSZCTASKService;
import com.rzt.util.WebApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 李成阳
 * 2018/1/7
 */
@RequestMapping("/picture")
@RestController
public class PictureController extends
        CurdController<PictureController,PictureService>{

    /***
     * 获取详情中默认展示的4条照片
     * @param taskId
     * @param TASKTYPE
     * @return
     */
    @GetMapping("/getPictureAndLine")
    public WebApiResponse getPictureAndLine(String taskId,String TASKTYPE){

        return service.getPictureAndLine(taskId,TASKTYPE);
    }

    /**
     * 获取问题检查中的所有照片  以杆塔分组返回
     * @param taskId
     * @param TASKTYPE
     * @return
     */
    @GetMapping("/getPictureAndLines")
    public WebApiResponse getPictureAndLines(String taskId,String TASKTYPE){
        return service.getPictureAndLines(taskId,TASKTYPE);
    }

    /**
     * 根据问题id 和任务类型  查看当前问题对用的照片
     * @param ids
     * @param taskType
     * @return
     */
    @GetMapping("/getPhotos")
    public WebApiResponse getPhotos(String ids,String taskId,String taskType){
        return service.getPhotos(taskId,ids,taskType);
    }



}
