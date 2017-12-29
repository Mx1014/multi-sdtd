/**
 * 文件名：RztsyscompanyController
 * 版本信息：
 * 日期：2017/12/08 16:40:23
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller;

import com.rzt.entity.RztSysUser;
import com.rzt.entity.Rztsyscompany;
import com.rzt.entity.Rztsyscompanyfile;
import com.rzt.service.RztsyscompanyService;
import com.rzt.service.RztsyscompanyfileService;
import com.rzt.util.WebApiResponse;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.rzt.controller.CurdController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * 类名称：RztsyscompanyController
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/12/08 16:40:23
 * 修改人：张虎成
 * 修改时间：2017/12/08 16:40:23
 * 修改备注：
 */
@RestController
@RequestMapping("Rztsyscompany")
public class RztsyscompanyController extends
        CurdController<Rztsyscompany, RztsyscompanyService> {
    @Autowired
    private RztsyscompanyfileService companyFileService;

    @PostMapping("addCompany")
    @ApiOperation(value = "添加外协单位", notes = "添加外协单位")
    public WebApiResponse addUser(HttpServletRequest request, @ModelAttribute Rztsyscompany company,
                                  @RequestParam(required = false) String fileType) {
        company.setCreatetime(new Date());
        this.service.add(company);
        System.out.println(company.getId());
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        String[] fileNames = new String[files.size()];
        String filePath = "";
        if (files.size() != 0) {
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                // 获取文件名
                String fileName = file.getOriginalFilename();
                fileNames[i] = fileName;
                // 文件上传后的路径
                filePath = "E://test//" + fileName;
                File dest = new File(filePath);
                try {
                    file.transferTo(dest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!StringUtils.isEmpty(fileType)) {
            String[] ft = fileType.split(",");
            for (int i = 0; i < fileNames.length; i++) {
                Rztsyscompanyfile companyfile = new Rztsyscompanyfile();
                companyfile.setCompanyid(company.getId());
                companyfile.setFilename(fileNames[i]);
                companyfile.setFiletype(Integer.valueOf(ft[i]));
                companyFileService.add(companyfile);
            }
        }
        return WebApiResponse.success("添加成功！");
    }

    /**
     * 外协队伍分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @GetMapping("queryRztsyscompany")
    @ApiOperation(value = "外协队伍分页查询", notes = "外协队伍分页查询")
    public WebApiResponse queryRztsyscompany(
            Integer page,
            Integer size) {
        try {
            return WebApiResponse.success(this.service.queryRztsyscompany(page, size));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("Data Error");
        }
    }

    /**
     * 添加外协队伍
     *
     * @param filename   文件名称
     * @param filetype   文件类型
     * @param cmpanyname 外协队伍名称
     * @param orgid      通道单位ID
     * @return
     */
    @PostMapping("addRztsyscompany")
    @ApiOperation(value = "添加外协单位", notes = "添加外协单位")
    public WebApiResponse addRztsyscompany(String filename, String filetype, String cmpanyname, String orgid) {
        int one = 1;
        String id = UUID.randomUUID().toString().replaceAll("-", "");
        int addRztsyscompany = this.service.addRztsyscompany(id, filename, filetype, cmpanyname, orgid);
        if (addRztsyscompany == one) {
            return WebApiResponse.success("Note[Data addition success]");
        }
        return WebApiResponse.erro("Note[Data addition failure]");
    }

    /**
     * 修改外协单位
     *
     * @param cmpanyname 外协队伍名称
     * @param orgid      通道单位id
     * @param id         外协队伍ID
     * @param filetype   文件名称
     * @param filename   文件类型
     * @return
     */
    @PatchMapping("updateRztsyscompany")
    @ApiOperation(value = "修改外协单位", notes = "修改外协单位")
    public WebApiResponse updateRztsyscompany(String cmpanyname, String orgid, String id, String filetype, String filename) {
        int one = 1;
        int updateRztsyscompany = this.service.updateRztsyscompany(cmpanyname, orgid, id, filetype, filename);
        if (updateRztsyscompany == one) {
            return WebApiResponse.success("Note[Data modification success]");
        } else {
            return WebApiResponse.erro("Note[Data modification failure]");
        }
    }

    /**
     * 外协队伍删除
     *
     * @param id 外协队伍ID
     * @return
     */
    @DeleteMapping("deleteRztsyscompany")
    @ApiOperation(value = "外协队伍删除", notes = "外协队伍删除")
    public WebApiResponse deleteRztsyscompany(String id) {
        String[] split = id.split(",");
        try {
            for (int i = 0; i < split.length; i++) {
                this.service.deleteRztsyscompany(split[i]);
            }
            return WebApiResponse.success("Note[Data deleted successfully]");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return WebApiResponse.erro("Note[Data deletion failed]");
    }

    /**
     * 公共的
     *
     * @return
     */
    @GetMapping("queryCompanyname")
    @ApiOperation(value = "公共外协队伍查询", notes = "公共外协队伍查询")
    public WebApiResponse queryCompanyname() {
        return this.service.queryCompanyname();
    }

    /**
     * 单位id 查外协权限
     *
     * @param id
     * @return
     */
    @GetMapping("queryCompanynameById")
    @ApiOperation(value = "根据单位ID查询外协队伍查询", notes = "根据单位ID查询外协队伍查询")
    public WebApiResponse queryCompanynameById(String id) {
        return this.service.queryCompanynameById(id);
    }

    /**
     * 外协单位导出
     *
     * @param request
     * @param response
     * @return
     */
    @GetMapping("exportXlsCompany")
    @ApiOperation(value = "外协单位导出", notes = "外协单位导出")
    public WebApiResponse exportXlsCompany(HttpServletRequest request, HttpServletResponse response) {
        try {
            //读取excel模板
            String rootpath = request.getSession().getServletContext().getRealPath(File.separator);
            XSSFWorkbook wb = new XSSFWorkbook();
            XSSFSheet sheet = wb.createSheet("外协队伍");
            final List<Map<String, Object>> list1 = this.service.exportXlsCompany();
            // 设置列宽
            sheet.setColumnWidth((short) 0, (short) 6000);
            sheet.setColumnWidth((short) 1, (short) 6000);
            sheet.setColumnWidth((short) 2, (short) 6000);
            sheet.setColumnWidth((short) 3, (short) 2000);
            // 设置表头样式
            XSSFCellStyle cellstyle = wb.createCellStyle();
            // 设置居中
            cellstyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
            // 创建标题样式
            XSSFCellStyle headerStyle = wb.createCellStyle();
            //设置垂直居中
            headerStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
            //设置水平居中
            headerStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
            //创建字体样式
            XSSFFont headerFont = wb.createFont();
            // 字体加粗
            headerFont.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
            //设置字体类型
            headerFont.setFontName("Times New Roman");
            //设置字体大小
            headerFont.setFontHeightInPoints((short) 12);
            //为标题样式设置字体样式
            headerStyle.setFont(headerFont);
            XSSFRow row = sheet.createRow(0);
            XSSFCell cell = row.createCell((short) 0);
            cell.setCellValue("外协名称");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 1);
            cell.setCellValue("所属单位");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 2);
            cell.setCellValue("创建时间");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 3);
            cell.setCellValue("修改时间");
            cell.setCellStyle(headerStyle);
            cell = row.createCell((short) 4);
            cell.setCellStyle(headerStyle);
            for (int i = 0; i < list1.size(); i++) {
                row = sheet.createRow(i + 1);
                Map<String, Object> map = list1.get(i);
                row.createCell(0).setCellValue(String.valueOf(map.get("COMPANYNAME")));
                row.createCell(1).setCellValue(String.valueOf(map.get("CREATETIME")));
                row.createCell(2).setCellValue(String.valueOf(map.get("UPDATETIME")));
                row.createCell(3).setCellValue(String.valueOf(map.get("ORGNAME")));
            }
            OutputStream output = response.getOutputStream();
            response.reset();
//            response.setHeader("content-Type", "application/vnd.ms-excel");
            response.setHeader("Content-disposition", "attachment;filename=" + new String("外协队伍.xlsx".getBytes("gbk"), "iso8859-1"));
            response.setContentType("Content-Type:application/vnd.ms-excel");
            wb.write(output);
            output.close();
            return WebApiResponse.success("true");
        } catch (Exception e) {
            WebApiResponse.erro("false");
            e.printStackTrace();
        }
        return null;
    }
}