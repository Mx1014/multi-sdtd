package com.rzt.controller;

import com.rzt.entity.MapMenInfo;
import com.rzt.service.CmcoordinateService;
import com.rzt.util.WebApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/***
* @Class PcMapShowController
* @Description
* @param
* @return
* @date 2017/12/25 13:57
* @author nwz
*/
@RestController
@RequestMapping("pcMapShow")
public class PcMapShowController {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private CmcoordinateService cmcoordinateService;
    /***
    * @Method menInMap
    * @Description 地图上的人
    *
    * @return java.lang.Object
    * @date 2017/12/25 13:59
    * @author nwz
    */
    @GetMapping("menInMap1")
    public Object menInMap() {
        try {

            List<MapMenInfo> list = new ArrayList<>();
            MapMenInfo mapMenInfo1 = new MapMenInfo("601DB81A3BF4DC35E0501AAC38EF512E","就是这个人",1,116.339782f, 39.912089f ,true);
            MapMenInfo mapMenInfo2 = new MapMenInfo("id2","就是这个看护人员",0, 116.404269f, 39.916927f,true);
            MapMenInfo mapMenInfo3 = new MapMenInfo("id3","稽查人员",2, 116.396961f, 39.907249f,true);
            MapMenInfo mapMenInfo11 = new MapMenInfo("id11","巡视人员1",1,116.329782f, 39.912089f ,false);
            MapMenInfo mapMenInfo22 = new MapMenInfo("id22","看护人员1",0, 116.424269f, 39.916927f,false);
            MapMenInfo mapMenInfo33 = new MapMenInfo("id33","稽查人员1",2, 116.326961f, 39.907249f,false);
            list.add(mapMenInfo1);
            list.add(mapMenInfo2);
            list.add(mapMenInfo3);
            list.add(mapMenInfo11);
            list.add(mapMenInfo22);
            list.add(mapMenInfo33);
            return WebApiResponse.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("失败");
        }
    }

    /***
     * @Method menInMap
     * @Description 地图上的人
     *
     * @return java.lang.Object
     * @date 2017/12/25 13:59
     * @author nwz
     */
    @GetMapping("menInMap")
    public Object menInMap1() {
        try {
            List<MapMenInfo> list = new ArrayList<MapMenInfo>();
            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
            List<Object> coordinateMap = hashOperations.values("temporyCoordinateMap");

            return WebApiResponse.success(coordinateMap);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("失败");
        }
    }

    /***
     * @Method menInMap
     * @Description 地图上的人
     *
     * @return java.lang.Object
     * @date 2017/12/25 13:59
     * @author nwz
     */
    @GetMapping("menInfo")
    public Object menInfo(String userId) {
        try {
            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
            Object userInformation = hashOperations.get("UserInformation", userId);
            return WebApiResponse.success(userInformation);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("失败");
        }
    }

    /***
     * @Method lineCoordinateList
     * @Description 线路的位置
     *
     * @return java.lang.Object
     * @date 2017/12/25 13:59
     * @author nwz
     */
    @GetMapping("lineCoordinateList")
    public Object lineCoordinateList(Long lineId) {
        try {
            List<Map<String,Object>> coordinateList =  cmcoordinateService.lineCoordinateList(lineId);
            return WebApiResponse.success(coordinateList);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据查询失败" + e.getMessage());
        }
    }

    /***
     * @Method towerCoordinate
     * @Description 杆塔的位置
     *
     * @return java.lang.Object
     * @date 2017/12/25 13:59
     * @author nwz
     */
    @GetMapping("towerCoordinate")
    public Object towerCoordinate(Long towerId) {
        try {
            Map<String,Object> coordinate =  cmcoordinateService.towerCoordinate(towerId);
            return WebApiResponse.success(coordinate);
        } catch (Exception e) {
            e.printStackTrace();
            return WebApiResponse.erro("数据查询失败" + e.getMessage());
        }
    }
}
