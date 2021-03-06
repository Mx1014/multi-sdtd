/**
 * 文件名：RztSysUserController
 * 版本信息：
 * 日期：2017/10/10 17:28:27
 * Copyright 融智通科技(北京)股份有限公司 版权所有
 */
package com.rzt.controller;

import com.alibaba.fastjson.JSONObject;
import com.rzt.entity.RztSysUser;
import com.rzt.entity.RztSysUserauth;
//import com.rzt.eureka.Cmuserfile;
import com.rzt.security.JwtHelper;
import com.rzt.security.TokenProp;
import com.rzt.service.RztMenuPrivilegeService;
import com.rzt.service.RztSysUserService;
import com.rzt.service.RztSysUserauthService;
import com.rzt.util.WebApiResponse;
import com.rzt.utils.Constances;
import com.rzt.utils.DateUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 类名称：RztSysUserController
 * 类描述：
 * 创建人：张虎成
 * 创建时间：2017/10/10 17:28:27
 * 修改人：张虎成
 * 修改时间：2017/10/10 17:28:27
 * 修改备注：
 */
@RestController
@RequestMapping("RztSysUser")
public class RztSysUserController extends
        CurdController<RztSysUser, RztSysUserService> {
    //    @Autowired
//    private Cmuserfile cmuserfile;
    @Autowired
    private RztSysUserauthService userauthService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RztMenuPrivilegeService privilegeService;
    @Autowired
    private TokenProp tokenProp;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @PostMapping("addUser")
    @Transactional
    @ApiOperation(value = "人员添加", notes = "人员添加")
    public WebApiResponse addUser(String password, RztSysUser user) {
//        String id = UUID.randomUUID().toString().replaceAll("-", "");
        /**
         * 验证
         */
        String flag = userauthService.findByUserName(user);
        if (!flag.equals("1")) {
            return WebApiResponse.erro(flag);
        }
        user.setCreatetime(DateUtil.dateNow());
        user.setUserdelete(1);
        /**
         * 添加
         */
        this.service.add(user);
        userauthService.addUserAuth(user, password);
        /**
         * 人员缓存Redis
         */
        userRedis(user);
        return WebApiResponse.success("添加成功！");
    }

    private void userRedis(RztSysUser user) {
        try {
            String sql = " SELECT * FROM USERINFO where USERDELETE = 1 AND ID=?1  ";
            Map<String, Object> map1 = this.service.execSqlSingleResult(sql, user.getId());
            HashOperations hashOperations = redisTemplate.opsForHash();
            hashOperations.put("UserInformation", map1.get("ID"), map1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改人员
     *
     * @param id
     * @param user
     * @param password
     * @return
     */
    @PatchMapping("updateUser")
    @ApiOperation(value = "修改人员", notes = "修改人员")
    public WebApiResponse updateUser(String id, RztSysUser user, String password) {
        /**
         * 验证
         */
        String flag = userauthService.findByUserName(user);
        if (!flag.equals("1")) {
            return WebApiResponse.erro(flag);
        }
        this.service.updateUser(id, user);
        return WebApiResponse.success("修改成功");
    }

    /**
     * 单个伪删除人员
     *
     * @param id
     * @return
     */
    @DeleteMapping("deleteUser/{id}")
    @ApiOperation(value = "逻辑删除一个人", notes = "逻辑删除一个人")
    public WebApiResponse deleteUser(@PathVariable String id) {
        try {
            this.service.logicUser(id);
            /**
             * 删除人员
             */
            HashOperations hashOperations = redisTemplate.opsForHash();
            hashOperations.delete("UserInformation", id);
            return WebApiResponse.success("true");
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("删除失败");
        }
    }

    /**
     * 批量伪删除人员
     *
     * @param ids
     * @return
     */
    @DeleteMapping("deleteBatchUser")
    @ApiOperation(value = "逻辑批量删除人员", notes = "逻辑批量删除人员*人员ID用,分割")
    public WebApiResponse deleteBatchUser(@RequestParam String ids) {
        if (!StringUtils.isEmpty(ids)) {
            HashOperations hashOperations = redisTemplate.opsForHash();
            String[] id = ids.split(",");
            for (int i = 0; i < id.length; i++) {
                try {
                    this.service.logicUser(id[i]);
                    /**
                     * 删除人员Redis
                     */
                    hashOperations.delete("UserInformation", id[i]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return WebApiResponse.erro("删除成功");
    }

    /**
     * 人员列表分页查询
     *
     * @param page 页数
     * @param size 每页行数
     * @return
     */
    @GetMapping("findAllUser")
    @ApiOperation(value = "人员分页查询", notes = "人员分页查询")
    public WebApiResponse findAllUser(String currentUserId, int page, int size, String id, String realname, String companyid, String worktype) {
        try {
            return WebApiResponse.success(this.service.findUserList(currentUserId, page, size, id, realname, companyid, worktype));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据错误");
        }
    }

    /**
     * 人员分页查询下拉框
     *
     * @return
     */
    @GetMapping("userQuertDeptZero")
    @ApiOperation(value = "人员分页查询下拉框", notes = "人员分页查询下拉框")
    public WebApiResponse userQuertDeptZero(String currentUserId) {
        return this.service.userQuertDeptZero(currentUserId);
    }

    /**
     * 下拉框外协单位
     *
     * @return
     */
    @GetMapping("companyPage")
    @ApiOperation(value = "下拉框外协单位", notes = "下拉框外协单位")
    public WebApiResponse companyPage(String currentUserId) {
        return this.service.companyPage(currentUserId);
    }

    @GetMapping("findUserById/{id}")
    @ResponseBody
    public RztSysUser findAllUser(@PathVariable String id) {
        RztSysUser user = this.service.findOne(id);
        return user;
    }

    /**
     * @param flag      0 账号登录 1 手机号登录  2 邮箱登录
     * @param loginType 0 APP登录 1 PC登录
     * @param account   账号
     * @param password  密码
     * @param request
     * @return
     */
    @GetMapping("login/{flag}/{loginType}")
    public WebApiResponse login(@PathVariable int flag, @PathVariable int loginType, @RequestParam String account,
                                @RequestParam String password, HttpServletRequest request) {

        RztSysUserauth userauth = userauthService.findByIdentifierAndIdentityTypeAndPassword(flag, account, password);
        RztSysUser sysUser = null;
        String access_token = null;
        Map<String, Object> map = null;
        if (userauth != null) {
            sysUser = this.service.findOne(userauth.getUserid());
            sysUser.setLoginstatus(1);
            try {
                map = this.service.getUserinfoByUserId(sysUser.getId());
                redisTemplate.opsForHash().put(Constances.USER_OBJ, sysUser.getId(), map);
                access_token = JwtHelper.createJWT(map,
                        tokenProp.getExpireTime()).getAccess_token();
                stringRedisTemplate.opsForValue().set("user:" + map.get("USERNAME"), access_token);
                this.service.update(sysUser, sysUser.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            userauthService.updateLoginIp(request.getRemoteAddr(), sysUser.getId(), flag);
        } else {
            return WebApiResponse.erro("用户名或密码不正确！");
        }

        return WebApiResponse.success(access_token);
    }

    /*
    人员登陆暂时不用
    @GetMapping("login/{flag}")
    public WebApiResponse login(@PathVariable int flag, @RequestParam String account,
                                @RequestParam String password, @RequestParam String deptid) {
        RztSysUser sysUser = this.service.findByUsernameAndDeptid(account, deptid);


        if (sysUser != null) {
            RztSysUserauth userauth = userauthService.findByIdentifierAndIdentityTypeAndPassword(flag, account, password);
            if (userauth != null)
                sysUser.setLoginstatus(1);
            else
                return WebApiResponse.erro("用户名或密码不正确！");
            try {
                this.service.update(sysUser, sysUser.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return WebApiResponse.erro("用户名或单位不正确！");
        }

        return WebApiResponse.success(sysUser);
    }*/

    /*
    人员退出暂时不用
    @GetMapping("logOut")
    public WebApiResponse logOut(HttpServletRequest request) {
        RztSysUser user = (RztSysUser) request.getSession().getAttribute("user");
        user.setLoginstatus(0);
        try {
            this.service.update(user, user.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        request.getSession().removeAttribute("user");

        return WebApiResponse.success("退出成功！");
    }*/

    /**
     * 人员查询
     *
     * @param classname 班组ID
     * @param realname  人员姓名模糊查询不是必须
     * @return
     */
    @GetMapping("userQuery")
    @ApiOperation(value = "公共人员查询", notes = "公共人员查询")
    public WebApiResponse userQuery(String classname, String realname) {
        try {
            return WebApiResponse.success(this.service.userQuery(classname, realname));
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("[Data Request Failed]");
        }
    }

    /**
     * 人员添加角色
     *
     * @param roleid 登陆人角色ID
     * @return
     */
    @GetMapping("treeRztsysroleQuery")
    @ApiOperation(value = "给人员附角色", notes = "给人员附角色")
    public List<Map<String, Object>> treeRztsysroleQuery(String roleid) {
        return this.service.treeRztsysroleQuery(roleid);
    }

    /**
     * 人员登陆
     *
     * @param password
     * @param account
     * @param loginType
     * @return
     */
    @PostMapping("userLogin")
    @ApiOperation(value = "人员登陆", notes = "人员登陆")
    public WebApiResponse userLogin(String password, String account, String loginType) {
        return this.service.userLogin(password, account, loginType);
    }

    /**
     * 退出
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "人员退出", notes = "人员退出")
    @PostMapping("userQuit")
    public WebApiResponse userQuit(String id) {
        if (!StringUtils.isEmpty(id)) {
            return this.service.userQuit(id);
        }
        return WebApiResponse.erro("erro");
    }

    @GetMapping("flushUserInformationRedis")
    public Object flushUserInfoRedis() {
        try {
            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
            String sql = "SELECT * from USERINFO";
            List<Map<String, Object>> maps = this.service.execSql(sql);
            for (Map<String, Object> map : maps) {
                String id = map.get("ID").toString();
                hashOperations.put("UserInformation", id, map);
            }
            return WebApiResponse.success("成功了");
        } catch (Exception e) {
            return WebApiResponse.erro("数据查询失败" + e.getMessage());
        }
    }
    //手机通讯录提供
    @GetMapping("getAddressList")
    public WebApiResponse getAddressList(String currentUserId) {
        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
        Object userInformation = hash.get("UserInformation", currentUserId);
        JSONObject jsonObject = JSONObject.parseObject(userInformation.toString());
        return this.service.getAddressList(currentUserId,jsonObject);
    }
}