package com.rzt;

import com.rzt.utils.SnowflakeIdWorker;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


public class WarningmonitorApplicationTests {

    public static void main(String[] args) {
        for (int i=0;i<1000;i++){
            System.out.println(SnowflakeIdWorker.getInstance(20,23).nextId());
        }
    }

}
