package com.rzt.conf;/**
 * Created by Administrator on 2017/7/9.
 */

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author 作者：hcy
 * @version V1.0.0
 * @method 方法名：fileConf
 * @methodDesc 方法描述：
 * @return 返回值：
 * @description 描述：
 * @time 时间：2017-07-09 16:42
 */

//jdk1.8
/*@ConfigurationProperties(locations = "classpath:FileConfig.properties", prefix = "upload")*/
//jdk1.7
@Component
@PropertySource(value = "classpath:FileConfig.properties")
@ConfigurationProperties(prefix = "systemFile")
public class FileConfig {
    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}
