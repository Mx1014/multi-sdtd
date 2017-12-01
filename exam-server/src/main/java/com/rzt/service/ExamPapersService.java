/**    
 * 文件名：ExamPapersService           
 * 版本信息：    
 * 日期：2017/11/09 09:38:12    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;

import com.rzt.entity.ExamOptions;
import com.rzt.entity.ExamPapers;
import com.rzt.entity.ExamText;
import com.rzt.entity.PaperText;
import com.rzt.utils.DateUtil;
import com.rzt.repository.ExamPapersRepository;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**      
 * 类名称：ExamPapersService    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/11/09 09:38:12 
 * 修改人：张虎成    
 * 修改时间：2017/11/09 09:38:12    
 * 修改备注：    
 * @version        
 */
@Service
@Transactional
public class ExamPapersService extends CurdService<ExamPapers,ExamPapersRepository> {
    protected static Logger LOGGER = LoggerFactory.getLogger(ExamPapersService.class);
    @Autowired
    ExamTextService examTextService;
    @Autowired
    PaperTextService paperTextService;
    @Autowired
    ExamOptionsService examOptionsService;

    public void importPaper(MultipartFile file) throws Exception {
        int i = 2;
        try{
            //读取excel文档
            XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
            XSSFSheet sheet = wb.getSheetAt(0);
            XSSFRow row = sheet.getRow(0);
            String paperName = getCellValue(row.getCell(0));
            //add试卷信息
            ExamPapers examPapers = new ExamPapers();
            examPapers.setCreateTime(DateUtil.getCurrentTime());
            examPapers.setPaperName(paperName);
            examPapers.setExamTime(10);//分钟
            add(examPapers);
            row = sheet.getRow(i);
            while (row!=null&&!"".equals(row.toString().trim())){
                System.out.println(i-1);
                //试题描述
                String text = getCellValue(row.getCell(1));
                //试题分值
                String score = getCellValue(row.getCell(2));
                //试题类型
                String text_type = getCellValue(row.getCell(3));
                //add一道试题
                ExamText examText = new ExamText();
                examText.setTextBody(text);
                examText.setTextPoints(Integer.parseInt(score));
                //1单选2多选3判断
                if(text_type.contains("单选")){
                    examText.setTextType(1);
                }else if(text_type.contains("多选")){
                    examText.setTextType(2);
                }else if(text_type.contains("判断")){
                    examText.setTextType(3);
                }
                examTextService.add(examText);
                //add试卷与试题的关联中间表
                PaperText paperText = new PaperText();
                paperText.setPaperId(examPapers.getId());
                paperText.setTextId(examText.getId());
                paperTextService.add(paperText);

                int x = 4;
                XSSFCell cell = row.getCell(x);
                while(cell!=null&&!"".equals(cell.toString().trim())&&cell.getCellType()!=XSSFCell.CELL_TYPE_BLANK){
                    XSSFCellStyle cellStyle = cell.getCellStyle();
                    int isRight = 0;
                    if(cellStyle.getFillPattern() == XSSFCellStyle.SOLID_FOREGROUND){//设置过背景色
                        String argbHex = cellStyle.getFillForegroundColorColor().getARGBHex();
                        if(!"FFFFFFFF".equals(argbHex)){
                            //有背景色
                            isRight = 1;
                        }
                    }
                    //试题选项
                    String option1 = cell.toString();

                    //add试题选项信息
                    ExamOptions examOption = new ExamOptions();
                    examOption.setTextId(examText.getId());
                    if(examText.getTextType()!=3){
                        examOption.setOptionName(option1);
                        examOption.setIsRight(isRight);
                    }else{
                        if(option1.contains("对")){
                            examOption.setIsRight(1);
                        }else{
                            examOption.setIsRight(0);
                        }
                    }
                    examOptionsService.add(examOption);
                    cell = row.getCell(++x);
                }

                row = sheet.getRow(++i);
            }

        }catch(Exception e){
            LOGGER.error("----------导入第"+(i-1)+"题时报错------",e);
            throw e;
        }
        LOGGER.info("试卷导入成功！");
    }

    private String getCellValue(XSSFCell cell) {
        String cellValue = "";
        DecimalFormat df = new DecimalFormat("#");
        switch (cell.getCellType()) {
            case XSSFCell.CELL_TYPE_STRING:
                cellValue = cell.getRichStringCellValue().getString().trim();
                break;
            case XSSFCell.CELL_TYPE_NUMERIC:
                cellValue = df.format(cell.getNumericCellValue()).toString();
                break;
            case XSSFCell.CELL_TYPE_BOOLEAN:
                cellValue = String.valueOf(cell.getBooleanCellValue()).trim();
                break;
            case XSSFCell.CELL_TYPE_FORMULA:
                cellValue = cell.getCellFormula();
                break;
            default:
                cellValue = "";
        }
        return cellValue;
    }

    public List<Map<String,Object>> getExamTexts(String paperId) {
        return this.execSql("select t.id as text_id,t.text_body,t.text_points,group_concat(o.id) as option_id,group_concat(o.option_name,',|分|割|') as options,GROUP_CONCAT(o.is_right) as anwers " +
                " from paper_text pt,exam_text t,exam_options o " +
                " where pt.paper_id=?1 and t.id=pt.text_id and t.id=o.text_id  group by t.id",paperId);
        //return this.reposiotry.getExamTexts(paperId);
    }
}