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
import com.rzt.utils.ImageUtils;
import com.rzt.utils.SnowflakeIdWorker;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 类名称：PICTURETOURController
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/11/29 09:35:42 
 * 修改人：张虎成    
 * 修改时间：2017/11/29 09:35:42    
 * 修改备注：    
 * @version        
 */
@RestController
@RequestMapping("PICTURETOUR")
public class PICTURETOURController extends
		CurdController<PICTURETOUR,PICTURETOURService> {

    protected static Logger LOGGER = LoggerFactory.getLogger(PICTURETOURController.class);

    @Autowired
    private Environment env;

	@ApiOperation(
			value = "文件上传",
			notes = "上传文件信息及参数"
	)
	@PostMapping("fileUpload")
	public synchronized void fileUpload(MultipartFile file,String taskId) {
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
            String targetDir = env.getProperty("file.upload-dir") + currentDate + File.separator + taskId + File.separator;
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
                    int picHeight = Integer.parseInt(env.getProperty("file.thum.height"));
                    int picWidth = Integer.parseInt(env.getProperty("file.thum.width"));
                    ImageUtils.resize(file,targetDir + picHeight + "x" + picWidth + originalFilename,picHeight,picWidth);
                }
                file.transferTo(targetFile);
            } catch (IOException e) {
                LOGGER.error("文件上传出错，请确保路径设置正确，且拥有读写权限！",e);
            }

        }

	}

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
                        picturetour.setId(String.valueOf(idWorker.nextId()));
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