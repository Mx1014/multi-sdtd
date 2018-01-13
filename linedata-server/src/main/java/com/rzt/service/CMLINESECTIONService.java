/**    
 * 文件名：CMLINESECTIONService           
 * 版本信息：    
 * 日期：2017/12/07 15:05:37    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;

import com.alibaba.fastjson.JSON;
import com.rzt.entity.CMLINE;
import com.rzt.entity.CMLINESECTION;
import com.rzt.entity.CMTOWER;
import com.rzt.repository.CMLINERepository;
import com.rzt.repository.CMLINESECTIONRepository;
import com.rzt.repository.CMTOWERRepository;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.ExcelUtil;
import com.rzt.utils.HanyuPinyinHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**      
 * 类名称：CMLINESECTIONService    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/07 15:05:37 
 * 修改人：张虎成    
 * 修改时间：2017/12/07 15:05:37    
 * 修改备注：    
 * @version        
 */
@Service
public class CMLINESECTIONService extends CurdService<CMLINESECTION,CMLINESECTIONRepository> {

    protected static Logger LOGGER = LoggerFactory.getLogger(CMLINESECTIONService.class);
    @Autowired
    private CMLINERepository cmlineRepository;
    @Autowired
    private CMTOWERRepository cmtowerRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public WebApiResponse getLineInfoByOrg(Pageable pageable, String tdOrg, String kv, String lineId) {
        List<String> list = new ArrayList<>();
        Object[] objects = list.toArray();
        String sql = "select * from cm_line_section where is_del=0 ";
        if(tdOrg!=null&&!"".equals(tdOrg.trim())){
            list.add(tdOrg);
            sql += " and td_org= ?" + list.size();
        }
        if(kv!=null&&!"".equals(kv.trim())){
            list.add(kv);
            sql += " and v_level= ?" + list.size();
        }
        if(lineId!=null&&!"".equals(lineId.trim())){
            list.add(lineId);
            sql += " and line_id= ?" + list.size();
        }
        Page<Map<String, Object>> maps = execSqlPage(pageable, sql,list.toArray());
        return WebApiResponse.success(maps);
    }

    public WebApiResponse getLineInfoComm(String tdOrg, String kv) {
        List<String> list = new ArrayList<>();
        Object[] objects = list.toArray();
        String sql = "select * from cm_line_section where is_del=0 ";
        if(tdOrg!=null&&!"".equals(tdOrg.trim())){
            list.add(tdOrg);
            sql += " and td_org= ?" + list.size();
        }
        if(kv!=null&&!"".equals(kv.trim())){
            list.add(kv);
            sql += " and v_level= ?" + list.size();
        }
        List<Map<String, Object>> maps = execSql(sql,list.toArray());
        return WebApiResponse.success(maps);
    }

    public WebApiResponse getLineInfoCommOptions(String tdOrg, String kv,String currentUserId) {
        List<String> list = new ArrayList<>();
        String sql = "select line_id,line_name,line_jb,SECTION from cm_line_section where is_del=0 ";
        if(StringUtils.isNotEmpty(currentUserId)){
            Map<String, Object> map = userInfoFromRedis(currentUserId);
            Integer roletype = Integer.parseInt(map.get("ROLETYPE").toString());
            String deptid  = map.get("DEPTID").toString();
            switch (roletype) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    tdOrg = deptid;
                    break;
                case 3:
                    //外协角色
                    break;
                case 4:
                    //班组角色
                    break;
                case 5:
                    //个人角色
                    break;
            }

        }
        if(tdOrg!=null&&!"".equals(tdOrg.trim())){
            list.add(tdOrg);
            sql += " and td_org= ?" + list.size();
        }
        if(kv!=null&&!"".equals(kv.trim())){
            list.add(kv);
            sql += " and v_level= ?" + list.size();
        }
        sql += " ORDER BY NLSSORT(line_name,'NLS_SORT = SCHINESE_PINYIN_M')";
        List<Map<String, Object>> maps = execSql(sql,list.toArray());
        return WebApiResponse.success(maps);
    }

    public WebApiResponse getTdOrg() {
        List<Map<String, Object>> maps = execSql("select id,deptname from rztsysdepartment where deptpid=(select id from rztsysdepartment where deptname='通道运维单位') ");
        return WebApiResponse.success(maps);
    }

    @Transactional
    public void importLineSection(MultipartFile file) throws Exception {
        int i = 2;
        try{
            //读取excel文档
            HSSFWorkbook wb = new HSSFWorkbook(file.getInputStream());
            HSSFSheet sheet = wb.getSheetAt(0);

            CMLINESECTION cmlinesection = new CMLINESECTION();
            HSSFRow row = sheet.getRow(i);
            while (row!=null&&!"".equals(row.toString().trim())){
                HSSFCell cell = row.getCell(1);
                if(cell==null||"".equals(ExcelUtil.getCellValue(cell))){
                    break;
                }
                cmlinesection.setId(null);
                String line_jb = ExcelUtil.getCellValue(row.getCell(1));
                cmlinesection.setLineJb(line_jb);
                String line_name = ExcelUtil.getCellValue(row.getCell(2)).replace("线","");
                cmlinesection.setLineName(HanyuPinyinHelper.getPinyinString(line_name));
                cmlinesection.setLineName1(line_name);
                String kv = ExcelUtil.getCellValue(row.getCell(3));
                cmlinesection.setVLevel(kv);
                try{
                    Map<String, Object> result = execSqlSingleResult("select id from cm_line where line_name = ?1 and v_level = ?2", line_name,kv);
                    cmlinesection.setLineId(Long.valueOf(String.valueOf(result.get("ID"))));
                }catch (Exception e){
                    //LOGGER.error("线路匹配失败！请先录入此线路信息--->第"+(i-1)+"条数据"+kv+line_name);
                    System.out.println("线路匹配失败！请先录入此线路信息--->第"+(i-1)+"条数据"+kv+line_name);
                    row = sheet.getRow(++i);
                    continue;
                }
                String fj_tower = ExcelUtil.getCellValue(row.getCell(4));
                if(StringUtils.isNotEmpty(fj_tower)){
                    cmlinesection.setFjTower(Integer.valueOf(fj_tower));
                }
                String section = ExcelUtil.getCellValue(row.getCell(5));
                cmlinesection.setSection(section);
                if(section.contains("-")){
                    section = section.replace("--",",");
                    section = section.replace("-",",");
                    section = section.replace("J","1");
                    String[] split = section.split(",");
                    cmlinesection.setStartSort(Integer.valueOf(split[0]));
                    cmlinesection.setEndSort(Integer.valueOf(split[1]));
                }else{
                    cmlinesection.setStartSort(Integer.valueOf(section));
                    cmlinesection.setEndSort(Integer.valueOf(section));
                }

                String length = ExcelUtil.getCellValue(row.getCell(6));
                cmlinesection.setLength(length);
                String bt_ord = ExcelUtil.getCellValue(row.getCell(7));
                cmlinesection.setBtOrg(bt_ord);//设备单位(本体单位)
                String td_org = ExcelUtil.getCellValue(row.getCell(8));
                try{
                    Map<String, Object> maps = execSqlSingleResult("select ID,DEPTNAME from rztsysdepartment where deptpid=(select id from rztsysdepartment where deptname='通道运维单位')  and deptname like ?1", "%"+td_org.substring(0, 2)+"%");
                    cmlinesection.setTdOrg(String.valueOf(maps.get("ID")));
                    cmlinesection.setTdOrgName(String.valueOf(maps.get("DEPTNAME")));
                }catch (Exception e){
                    //LOGGER.error("通道单位匹配失败！--->第"+(i-1)+"条数据"+kv+line_name);
                    System.out.println("通道单位匹配失败！--->第"+(i-1)+"条数据"+kv+line_name);
                    continue;
                }

                //班组
                String bz = ExcelUtil.getCellValue(row.getCell(9));
                cmlinesection.setWxOrg(bz);
                add(cmlinesection);
                row = sheet.getRow(++i);
                System.out.print(".");
            }

        }catch(Exception e){
            LOGGER.error("----------导入第"+(i-1)+"条故障数据(excel第"+(i+1)+"行)时出错------");
            throw e;
        }
        System.out.println("导入成功！");

    }

    public Map<String, Object> userInfoFromRedis(String userId) {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();

        Map<String,Object> jsonObject = null;
        Object userInformation = hashOperations.get("UserInformation", userId);
        if(userInformation == null) {
            String sql = "select * from userinfo where id = ?";
            try {
                jsonObject = this.execSqlSingleResult(sql, userId);
            } catch (Exception e) {
                LOGGER.error("currentUserId未获取到唯一数据!",e);
            }
            hashOperations.put("UserInformation",userId,jsonObject);
        } else {
            jsonObject = JSON.parseObject(userInformation.toString(),Map.class);
        }
        return jsonObject;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Map<String,Object> addLineSection(CMLINESECTION cmlinesection) {
        Map<String, Object> map = new HashMap<>();

        try {
            Integer startSort = cmlinesection.getStartSort();
            Integer endSort = cmlinesection.getEndSort();
            cmlinesection.setSection(startSort+"--"+endSort);
            List<Map<String, Object>> list = execSql("select * from cm_line where line_name = ?1 and v_level = ?2 ", cmlinesection.getLineName(), cmlinesection.getVLevel());
            if(list.size()==0){
                CMLINE cmline = new CMLINE();
                cmline.setId(null);
                cmline.setLineName(cmlinesection.getLineName());
                cmline.setVLevel(cmlinesection.getVLevel());
                cmline.setSection(cmlinesection.getSection());
                cmline.setIsDel(0);
                CMLINE save = cmlineRepository.save(cmline);
                cmlinesection.setLineId(save.getId());

            }else if(list.size()==1){
                cmlinesection.setLineId(Long.valueOf(list.get(0).get("ID").toString()));
                cmlineRepository.updateLineSection(cmlinesection.getLineId(),startSort+"--"+endSort);
            }else{
                LOGGER.error("此线路信息重复!"+cmlinesection.getVLevel()+cmlinesection.getLineName());

                throw new Exception("此线路信息重复!");
            }

            cmlinesection.setId(null);
            reposiotry.save(cmlinesection);
            if(list.size()==0){
                for (int i = startSort; i <= endSort; i++) {
                    CMTOWER cmtower = new CMTOWER();
                    cmtower.setId(null);
                    cmtower.setLineId(String.valueOf(cmlinesection.getLineId()));
                    cmtower.setName(String.valueOf(i));
                    cmtowerRepository.save(cmtower);
                }
            }else if(list.size()==1){
                String section = String.valueOf(list.get(0).get("SECTION"));
                String[] split = section.split("--");
                Integer a = Integer.valueOf(split[0]);
                Integer b = Integer.valueOf(split[1]);
                if(startSort<a&&endSort<a || startSort>b&&endSort>b){
                    for (int i = startSort; i <= endSort; i++) {
                        CMTOWER cmtower = new CMTOWER();
                        cmtower.setId(null);
                        cmtower.setLineId(String.valueOf(cmlinesection.getLineId()));
                        cmtower.setName(String.valueOf(i));
                        cmtowerRepository.save(cmtower);
                    }
                }else {
                    if(startSort<a&&endSort>=a){
                        for (int i = startSort; i < a; i++) {
                            CMTOWER cmtower = new CMTOWER();
                            cmtower.setId(null);
                            cmtower.setLineId(String.valueOf(cmlinesection.getLineId()));
                            cmtower.setName(String.valueOf(i));
                            cmtowerRepository.save(cmtower);
                        }
                    }
                    if(startSort<=b&&endSort>b){
                        for (int i = b+1; i <= endSort; i++) {
                            CMTOWER cmtower = new CMTOWER();
                            cmtower.setId(null);
                            cmtower.setLineId(String.valueOf(cmlinesection.getLineId()));
                            cmtower.setName(String.valueOf(i));
                            cmtowerRepository.save(cmtower);
                        }
                    }
                }


            }

            reposiotry.deleteCmLineTower(cmlinesection.getLineId());

            //reposiotry.addCmLineTower(cmlinesection.getLineId());
            map.put("success",true);
            map.put("lineId",cmlinesection.getLineId());
        }catch (Exception e){
            map.put("success",false);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return map;
    }

    @Transactional
    public void addCmLineTower(String lineId) {
        reposiotry.addCmLineTower(Long.valueOf(lineId));
    }
}