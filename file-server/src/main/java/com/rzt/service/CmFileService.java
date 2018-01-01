/**    
 * 文件名：CmFileService           
 * 版本信息：    
 * 日期：2017/12/08 11:06:32    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;

import com.rzt.entity.CmFile;
import com.rzt.repository.CmFileRepository;
import com.rzt.utils.StorageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**      
 * 类名称：CmFileService    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/08 11:06:32 
 * 修改人：张虎成    
 * 修改时间：2017/12/08 11:06:32    
 * 修改备注：    
 * @version        
 */
@Service
public class CmFileService extends CurdService<CmFile,CmFileRepository> {

    protected static Logger LOGGER = LoggerFactory.getLogger(CmFileService.class);

    @Transactional
    public Map<String,Object> fileUpload(MultipartFile file, CmFile cmFile) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> map = StorageUtils.storageFiles(file);
            if("true".equals(map.get("success").toString())){

                String filePath = map.get("filePath").toString();
                String fileName = map.get("fileName").toString();
                cmFile.setId(null);
                cmFile.setCreateTime(new Date(System.currentTimeMillis()));
                cmFile.setFileName(fileName);
                cmFile.setFilePath(filePath);

                add(cmFile);
                result.put("success",true);
                result.put("id",String.valueOf(cmFile.getId()));
                result.put("filePath",filePath);

            }

        } catch (IOException e) {
            LOGGER.error("文件上传失败",e);
            result.put("success",false);
        }
        return result;
    }

    public Map<String,Object> getImgByFkId(Long fkid) {
        Map<String, Object> result = new HashMap<>();
        List<CmFile> list = reposiotry.findByFkId(fkid);
        result.put("success",true);
        result.put("object",list);

        return result;
    }

    public Map<String,Object> getImgById(Long id) {
        Map<String, Object> result = new HashMap<>();
        CmFile cmFile = reposiotry.findById(id);
        result.put("success",true);
        result.put("object",cmFile);
        return result;
    }

    @Transactional
    public Map<String,Object> deleteImgById(Long id) {
        Map<String, Object> result = new HashMap<>();
        try{
            CmFile one = reposiotry.findById(id);
            StorageUtils.deleteImg(one.getFilePath());
            reposiotry.deleteById(id);
            result.put("success",true);
        }catch (Exception e){
            LOGGER.error("图片删除失败！",e);
            result.put("success",false);
        }
        return result;
    }

}