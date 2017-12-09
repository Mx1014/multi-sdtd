package com.rzt.controller.pcController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
* @Class  XsTaskManageController
* @Description
* @param
* @return
* @date 2017/12/6 14:45
* @author nwz
*/
@RestController
@Api(
    value = "pc巡视任务管理",
        tags = "xs"
)
@RequestMapping("pcXsTask")
@ApiIgnore
public class XsTaskManageController {

    

    /**
    * @Method addXsTaskCycle
    * @Description         
    * @param
    * @return java.lang.Object
    * @date 2017/12/6 16:39
    * @author nwz
    */
    @ApiOperation(value="fuck你")
    @PostMapping("fuck")
    public Object addXsTaskCycle() {
        return 1;
    }

}
