package com.rzt.controller.appController;
import com.rzt.entity.WarningOneKey;
import com.rzt.service.app.WarningOneKeyService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.rzt.controller.CurdController;

@RestController
@RequestMapping("WARNINGONEKEY")
public class WarningOneKeyController extends
        CurdController<WarningOneKey, WarningOneKeyService> {

    @PostMapping("/saveWarning")
    @ResponseBody
    public WebApiResponse saveWarning(WarningOneKey warn,String ids){
        return this.service.saveWarning(warn,ids);
    }
}