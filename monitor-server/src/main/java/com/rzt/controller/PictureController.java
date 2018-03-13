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
     * 获取详情中默认展示的3张最新照片
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
    public WebApiResponse getPictureAndLines(String taskId,String TASKTYPE,String currentUserId){
        return service.getPictureAndLines(taskId,TASKTYPE,currentUserId);
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

    /**
     * 根据任务id 和图片id 查询当前问题
     * @param taskId  任务id
     * @param pId  图片id
     * @return
     */
    @GetMapping("/findProByTaskId")
    public WebApiResponse findProByTaskId(String taskId,String pId){
        return service.findProByTaskId(taskId,pId);
    }

    /**
     * 根据当前流程id  获取当前流程的照片
     * @param id  流程id
     * @param proId  看护任务时的流程id
     * @return
     */
    @GetMapping("/findProByproId")
    public WebApiResponse findProByproId(String id,String taskType,String proId,String dtId){
       return service.findProByproId(id,taskType,proId,dtId);
    }

    /**
     * 获取当前隐患的图片
     * @param id
     * @param taskType
     * @return
     */
    @GetMapping("/findPicByTaskId")
    public WebApiResponse findPicByTaskId(Long id,String taskType){
        return service.findPicByTaskId(id,taskType);
    }

    /**
     * 查看当前任务所有有问题的照片   页面回显提示使用
     * @param taskId
     * @return
     */
    @GetMapping("/findPicByPro")
    public WebApiResponse findPicByPro(String taskId){
        return service.findPicByPro(taskId);

    }




}
