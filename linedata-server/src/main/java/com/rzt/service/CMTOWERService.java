/**    
 * 文件名：CMTOWERService           
 * 版本信息：    
 * 日期：2017/12/07 15:05:37    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;

import com.rzt.entity.CMTOWER;
import com.rzt.repository.CMTOWERRepository;
import com.rzt.utils.ExcelUtil;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**      
 * 类名称：CMTOWERService    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/07 15:05:37 
 * 修改人：张虎成    
 * 修改时间：2017/12/07 15:05:37    
 * 修改备注：    
 * @version        
 */
@Service
public class CMTOWERService extends CurdService<CMTOWER,CMTOWERRepository> {

    protected static Logger LOGGER = LoggerFactory.getLogger(CMTOWERService.class);
    @Autowired
    private CMLINETOWERService cmlinetowerService;
    @Autowired
    private CMLINEService cmlineService;

    @Transactional
    public void importTowers(MultipartFile file) throws IOException {
        int i = 1;
        try{
            //读取excel文档
            HSSFWorkbook wb = new HSSFWorkbook(file.getInputStream());
            HSSFSheet sheet = wb.getSheetAt(0);

            CMTOWER cmtower = new CMTOWER();
            HSSFRow row = sheet.getRow(i);
            while (row!=null&&!"".equals(row.toString().trim())){
                HSSFCell cell = row.getCell(0);
                if(cell==null||"".equals(ExcelUtil.getCellValue(cell))){
                    break;
                }
                cmtower.setId(null);
                String tower_num = ExcelUtil.getCellValue(row.getCell(2));

                String line_name = ExcelUtil.getCellValue(row.getCell(3));

                row = sheet.getRow(++i);
            }

        }catch(Exception e){
            LOGGER.error("----------导入第"+(i-1)+"条故障数据时出错------");
            throw e;
        }

    }
}