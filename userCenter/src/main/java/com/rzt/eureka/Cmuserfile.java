package com.rzt.eureka;

import feign.Headers;
import feign.Param;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.Map;

/**
 * @ProjectName: sdtd2-task
 * @Package: com.rzt.eureka
 * @Author: liuze
 * @date: 2017-12-21 10:55
 */
@FeignClient(value = "FILESERVER")
//@FeignClient(value = "FILESERVER",url = "http://168.130.1.31:9091/")
public interface Cmuserfile {
    /**
     * 人员头像上传
     *
     * @param file       文件
     * @param fileName   文件名字
     * @param fileType   文件类型 0 头像
     * @param createTime 创建时间
     * @param fkIdStr    人员ID
     * @return
     */
    @PostMapping("fileserver/CmFile/fileUpload")
    Map<String, Object> userFileUpload(@Param(value = "file") MultipartFile file, @RequestParam("fileName") String fileName, @RequestParam("fileType") Integer fileType, @RequestParam("createTime") Date createTime, @RequestParam("fkIdStr") String fkIdStr);

}
