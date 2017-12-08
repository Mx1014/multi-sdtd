package com.rzt.controller.app;

import com.rzt.controller.CurdController;
import com.rzt.entity.appentity.xsZcTaskwpqr;
import com.rzt.service.app.xsZcTaskwpqrService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashMap;

/**
 * @ProjectName: sdtd2-task
 * @Package: com.rzt.controller.app
 * @Author: liuze
 * @date: 2017-12-7 19:41
 */
@RestController
@RequestMapping("xsAppChange")
@EnableSwagger2
@Api(value = "巡视任务app修改接口")
public class xsZcTaskwpqrController extends CurdController<xsZcTaskwpqr, xsZcTaskwpqrService> {
    /**
     * xslx 巡视类型 1 正常巡视 2 保电巡视 ID 任务id
     * 身份确认修改时间
     *
     * @param xslx
     * @param id
     * @return
     */
    @ApiOperation(value = "身份确认修改时间", notes = "xslx 巡视类型 1 正常巡视 2 保电巡视 ID 任务id")
    @PostMapping("updateSfqrTime")
    public HashMap updateSfqrTime(int xslx, String id) {
        int updateSfqrTime = this.service.updateSfqrTime(id, xslx);
        int one = 1;
        HashMap<String, Boolean> hashMap = new HashMap(0);
        if (updateSfqrTime == one) {
            hashMap.put("success", true);
            return hashMap;
        }
        hashMap.put("success", false);
        return hashMap;
    }

    /**
     * xslx 巡视类型 1 正常巡视 2 保电巡视 ID 任务id
     * 到达现场时间修改
     *
     * @param xslx
     * @param id
     * @return
     */
    @ApiOperation(value = "到达现场时间修改", notes = "xslx 巡视类型 1 正常巡视 2 保电巡视 ID 任务id")
    @RequestMapping("reachSpot")
    public HashMap reachSpot(int xslx, String id) {
        int reachSpot = this.service.reachSpot(xslx, id);
        int one = 1;
        int zero = 0;
        HashMap<String, Boolean> hashMap = new HashMap(0);
        if (reachSpot == one) {
            hashMap.put("success", true);
        } else if (zero == 0) {
            hashMap.put("success", false);
        }
        return hashMap;
    }

    /**
     * 物品提醒
     *
     * @param xslx   巡视类型 0 特巡 1 保电 2 正常巡视
     * @param taskId 任务ID
     * @param wpZt   选择物品状态
     * @return
     */
    @PostMapping("articlesUpdate")
    @ApiOperation(value = "物品提醒", notes = " xslx   巡视类型 0 特巡 1 保电 2 正常巡视  taskId 任务ID  wpZt 返回 true false")
    public HashMap articlesUpdate(int xslx, String taskId, String wpZt, String id) {
        int one = 1;
        int zero = 0;
        HashMap<String, Boolean> hashMap = new HashMap(0);
        int articlesReminding = this.service.articlesReminding(taskId, wpZt, xslx, id);
        if (articlesReminding == zero) {
            hashMap.put("success", true);
            return hashMap;
        } else if (articlesReminding == one) {
            hashMap.put("success", false);
            return hashMap;
        }
        return hashMap;
    }
}
