/**    
 * 文件名：PictureWarnService           
 * 版本信息：    
 * 日期：2018/01/21 03:31:41    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;
import com.rzt.entity.PictureWarn;
import com.rzt.repository.PictureWarnRepository;
import com.rzt.utils.StorageUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**      
 * 类名称：PictureWarnService    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2018/01/21 03:31:41 
 * 修改人：张虎成    
 * 修改时间：2018/01/21 03:31:41    
 * 修改备注：    
 * @version        
 */
@Service
public class PictureWarnService extends CurdService<PictureWarn,PictureWarnRepository> {

    protected static Logger LOGGER = LoggerFactory.getLogger(PictureWarnService.class);

    @Transactional
    public Map<String,Object> fileUpload(MultipartFile multipartFile, PictureWarn pictureWarn) {
        Map<String, Object> result = new HashMap<>();

        Long taskId = pictureWarn.getTaskId();
        //判断taskId是否为空
        if(taskId== null||taskId.equals(0)){
            result.put("success",false);
            result.put("msg","taskId不能为空！");
            return result;
            // TODO: 2017/12/1 压力测试用
            //taskId = "taskId";
        }

        try {
            Map<String, Object> map = StorageUtils.resizeDefault(multipartFile, String.valueOf(taskId));
            if("true".equals(map.get("success").toString())){

                String targetPath = map.get("picPath").toString();
                String thumPath = map.get("thumPath").toString();
                String picName = map.get("picName").toString();
                pictureWarn.setId(null);
                pictureWarn.setCreateTime(new Date());
                pictureWarn.setFileName(picName);
                pictureWarn.setFilePath(targetPath);
                pictureWarn.setFileSmallPath(thumPath);
                pictureWarn.setFileType(1);

                add(pictureWarn);
                result.put("success",true);
                result.put("id",String.valueOf(pictureWarn.getId()));
                result.put("thumPath",thumPath);
                result.put("picPath",targetPath);
            }

        } catch (IOException e) {
            LOGGER.error("上传文件失败！",e);
            result.put("success",false);
        }

        return result;
    }

    @Transactional
    public Map<String,Object> deleteImgsById(Long id) {
        Map<String, Object> result = new HashMap<>();
        try{
            PictureWarn one = reposiotry.findById(id);
            StorageUtils.deleteImg(one.getFilePath());
            StorageUtils.deleteImg(one.getFileSmallPath());
            reposiotry.deleteById(id);
            result.put("success",true);
        }catch (Exception e){
            LOGGER.error("图片删除失败！",e);
            result.put("success",false);
        }
        return result;
    }


    public Map<String,Object> getImgsBytaskId(Long taskId) {

        Map<String, Object> result = new HashMap<>();
        List<PictureWarn> list = reposiotry.findBytaskId(taskId);
        result.put("success",true);
        result.put("object",list);

        return result;
    }

    public Map<String,Object> getImgsBytaskIdAndProcessId(String taskId, String processId,String processType) {
        Map<String, Object> result = new HashMap<>();
        ArrayList<String> params = new ArrayList<>();
        String sql = "select id,file_path,FILE_SMALL_PATH,json_str from PICTURE_JC where 1=1 ";
        if(!StringUtils.isEmpty(taskId)){
            params.add(taskId);
            sql += " and task_id=?"+params.size();
        }
        if(!StringUtils.isEmpty(processId)){
            params.add(processId);
            sql += " and process_id=?"+params.size();
        }
        if(!StringUtils.isEmpty(processType)){
            params.add(processType);
            sql += " and process_type=?"+params.size();
        }
        List<Map<String, Object>> list = execSql(sql, params.toArray());
        result.put("success",true);
        result.put("object",list);

        return result;
    }

    public Map<String,Object> findById(Long id) {
        Map<String, Object> result = new HashMap<>();
        PictureWarn byId = reposiotry.findById(id);
        result.put("success",true);
        result.put("object",byId);
        return result;
    }

    @Transactional
    public Map<String,Object> fileUploadByType(MultipartFile multipartFile, PictureWarn pictureWarn) {
        Map<String, Object> result = new HashMap<>();
        Long taskId = pictureWarn.getTaskId();
        //判断taskId是否为空
        if(taskId== null||taskId.equals(0)){
            result.put("success",false);
            result.put("msg","taskId不能为空！");
            return result;
            // TODO: 2017/12/1 压力测试用
            //taskId = "taskId";
        }

        try {
            Map<String, Object> map = StorageUtils.storageFilesByDay(multipartFile);
            if("true".equals(map.get("success").toString())){

                String filePath = map.get("filePath").toString();
                String fileName = map.get("fileName").toString();
                pictureWarn.setId(null);
                pictureWarn.setCreateTime(new Date());
                pictureWarn.setFileName(fileName);
                pictureWarn.setFilePath(filePath);

                add(pictureWarn);
                result.put("success",true);
                result.put("id",String.valueOf(pictureWarn.getId()));
                result.put("filePath",filePath);
                result.put("fileName",fileName);
            }

        } catch (IOException e) {
            LOGGER.error("上传文件失败！",e);
            result.put("success",false);
        }

        return result;

    }
}