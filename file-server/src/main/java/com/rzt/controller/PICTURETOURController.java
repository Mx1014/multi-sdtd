/**    
 * 文件名：PICTURETOURController
 * 版本信息：    
 * 日期：2017/11/29 09:35:42    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.controller;

import com.rzt.entity.PICTURETOUR;
import com.rzt.service.PICTURETOURService;
import com.rzt.utils.DateUtil;
import com.rzt.utils.SnowflakeIdWorker;
import com.rzt.utils.StorageUtils;
import com.rzt.utils.YmlConfigUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 类名称：PICTURETOURController
 * 类描述：巡视照片控制类
 * 创建人：张虎成   
 * 创建时间：2017/11/29 09:35:42 
 * 修改人：张虎成    
 * 修改时间：2017/11/29 09:35:42    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("PICTURETOUR")
@Api(description = "巡视照片类")
public class PICTURETOURController extends
		CurdController<PICTURETOUR,PICTURETOURService> {

    protected static Logger LOGGER = LoggerFactory.getLogger(PICTURETOURController.class);

    @ApiOperation(
            value = "单个文件上传",
            notes = "上传文件信息及参数。taskId  userId processId processName lat lon，这些是必须的"
    )
    @PostMapping("fileUpload")
    public Map<String, Object> fileUpload(MultipartFile multipartFile, PICTURETOUR picturetour) {
        return service.fileUpload(multipartFile,picturetour);
    }

    @ApiOperation(
            value = "根据Id删除照片",
            notes = "根据Id删除照片，同时删除文件及数据"
    )
    @DeleteMapping("deleteImgsById")
    public Map<String, Object> deleteImgsById(Long id) {
        return service.deleteImgsById(id);
    }

    @ApiOperation(
            value = "获取巡视任务照片",
            notes = "根据taskId获取某条任务的所有照片，返回List<PICTURETOUR>"
    )
    @GetMapping("getImgsBytaskId")
    public Map<String, Object> getImgsBytaskId(Long taskId) {
        return service.getImgsBytaskId( taskId);
    }

    @ApiOperation(
            value = "获取某一步骤的照片",
            notes = "根据taskId,processId获取某一步骤的照片"
    )
    @GetMapping("getImgsBytaskIdAndProcessId")
    public Map<String, Object> getImgsBytaskIdAndProcessId(String taskId,String processId) {
        return service.getImgsBytaskIdAndProcessId(taskId,processId);
    }

    @ApiOperation(
            value = "根据id获取照片",
            notes = "根据id获取照片"
    )
    @GetMapping("getImgById")
    public Map<String, Object> getImgById(Long id) {
        return service.getImgById(id);
    }

    @ApiOperation(
            value = "文件上传压力测试",
            notes = "上传文件信息及参数"
    )
    @PostMapping("fileUpload1")
    public Map<String, Object> fileUpload1(MultipartFile file, String taskId) {

        Map<String, Object> result = new HashMap<>();

        PICTURETOUR picturetour = new PICTURETOUR();
        //判断taskId是否为空
        if(taskId== null||"".equals(taskId)){
            //return;
            // TODO: 2017/12/1 压力测试用
            taskId = "taskId";
        }
        //判断文件是否为空,taskId是否为空
        if(!file.isEmpty()){
            //每天存一个文件夹、每个任务类型一个文件夹、每个任务一个文件夹
            String currentDate = DateUtil.getCurrentDate();
            String targetDir = YmlConfigUtil.getConfigByKey("upload-dir") + currentDate + File.separator + taskId + File.separator;
            File dir = new File(targetDir);
            //目标目录是否存在
            if(!dir.exists()&&!dir.isDirectory()){
                dir.mkdirs();
            }
            String originalFilename = UUID.randomUUID().toString()+file.getOriginalFilename();
            File targetFile = new File(targetDir + originalFilename);
            try {
                String suffix = originalFilename.substring(originalFilename.lastIndexOf(".")+1,originalFilename.length());
                //判断下文件是否为图片
                if("jpg".equals(suffix)){
                    int picHeight = Integer.parseInt(YmlConfigUtil.getConfigByKey("thum-height"));
                    int picWidth = Integer.parseInt(YmlConfigUtil.getConfigByKey("thum-width"));
                    StorageUtils.resize(file,targetDir + picHeight + "x" + picWidth + originalFilename,picHeight,picWidth);
                }
                file.transferTo(targetFile);
                picturetour.setId(null);
                picturetour.setFileName("");
                picturetour.setCreateTime(new Date());
                result.put("success",true);
            } catch (IOException e) {
                result.put("success",false);
                LOGGER.error("文件上传出错，请确保路径设置正确，且拥有读写权限！",e);
            }
        }
        return result;

    }

/*    @Scheduled(cron = "0/2 * * * * *")
    public void timerTask(){
        SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
        String dirStr = YmlConfigUtil.getConfigByKey("file-dir");
        System.out.println(dirStr);
        File dir = new File(dirStr);
        File[] files = dir.listFiles();


    }*/

    @ApiOperation(
            value = "SnowFlake测试",
            notes = "线程池测试SnowFlake重复概率"
    )
    @GetMapping("testSnowFlakeId")
	public void testSnowFlakeId(){
        SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
        /*for (int i = 0; i < 1000; i++) {
            long id = idWorker.nextId();
            System.out.println(Long.toBinaryString(id));
            System.out.println(id);
        }*/
        AtomicInteger failCount = new AtomicInteger(0);
        AtomicInteger count = new AtomicInteger(0);
        int totalCount = 1000;
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(100);
        executor.afterPropertiesSet();
        for (int i = 0; i < totalCount; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        PICTURETOUR picturetour = new PICTURETOUR();
                        picturetour.setId(idWorker.nextId());
                        picturetour.setFileName(String.valueOf(idWorker.nextId()));
                        service.add(picturetour);
                        System.out.println();

                    } catch (Exception e) {
                        e.printStackTrace();
                        failCount.incrementAndGet();
                    } finally {
                        count.incrementAndGet();
                    }

                }
            });
        }
        while (count.get() < totalCount) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                return;
            }
        }
        executor.destroy();
        System.out.println("success count: " + count.get());
        System.out.println("fail count: " + failCount.get());
    }
	
}