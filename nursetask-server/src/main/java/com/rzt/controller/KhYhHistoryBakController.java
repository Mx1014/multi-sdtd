
/**
 * 文件名：KHYHHISTORYBAKController
 * 版本信息：
 * 日期：2017/12/01 10:37:32
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller;
        import com.rzt.entity.KhYhHistoryBak;
        import com.rzt.service.KhYhHistoryBakService;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RestController;

/**
 * 类名称：KHYHHISTORYBAKController
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/12/01 10:37:32
 * 修改人：张虎成
 * 修改时间：2017/12/01 10:37:32
 * 修改备注：
 * @version
 */
@RestController
@RequestMapping("KHYHHISTORYBAK")
public class KhYhHistoryBakController extends
        CurdController<KhYhHistoryBak, KhYhHistoryBakService> {



}