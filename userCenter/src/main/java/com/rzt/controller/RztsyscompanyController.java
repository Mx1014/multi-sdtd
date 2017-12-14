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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.rzt.controller.CurdController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
    public WebApiResponse deleteRztsyscompany(String id) {
        int deleteRztsyscompany = this.service.deleteRztsyscompany(id);
        int one = 1;
        if (deleteRztsyscompany == one) {
            return WebApiResponse.success("Note[Data deleted successfully]");
        } else {
            return WebApiResponse.erro("Note[Data deletion failed]");
        }
    }
}