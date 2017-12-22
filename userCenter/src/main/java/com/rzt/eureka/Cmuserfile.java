package com.rzt.eureka;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @ProjectName: sdtd2-task
 * @Package: com.rzt.eureka
 * @Author: liuze
 * @date: 2017-12-21 10:55
 */
@FeignClient("FILESERVER")
public interface Cmuserfile {
    /**
     * 人员头像上传
     *
     * @param file
     * @param flag
     * @param fkId
     * @return
     */
    @PostMapping("/CmFile/fileUpload")
    Map<String,Object> userFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("flag") Integer flag, @RequestParam("fkId") Long fkId);
}
