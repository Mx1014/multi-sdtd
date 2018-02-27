package com.rzt.utils;

//import org.jasypt.util.text.BasicTextEncryptor;

import org.jasypt.util.text.BasicTextEncryptor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 张虎成 on 2017/6/9.
 */
public class Tesat {
//    public static void main(String ags[]){
//        BasicTextEncryptor encryptor = new BasicTextEncryptor();
//        encryptor.setPassword("root");
//        //String decrypted = encryptor.decrypt("0u+72b8MZIaTD21CfT3Wxg==");
//        String encrypted = encryptor.encrypt("root");
//        System.out.println(encrypted);
//        //System.out.println(decrypted);
//    }

    public static void main(String[] args) {
        /*StringBuilder buffer = new StringBuilder();
        buffer.append("SELECT node.id ,node.deptName ,node.lft,node.rgt,node.deptPid ");
        buffer.append("FROM RztSysDepartment node,RztSysDepartment parent WHERE node.lft BETWEEN parent.lft AND parent.rgt ");
        buffer.append("AND node.lft > " + 1);
        buffer.append(" AND node.rgt <" + 45 + " ");
        buffer.append("GROUP BY node.id,node.lft,node.deptName,node.rgt,node.deptPid ");
        buffer.append("ORDER BY node.lft");
        System.out.println(buffer);*/

        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(1);
        integers.add(2);
        integers.add(3);
        integers.add(4);
        integers.add(5);
        List<Integer> integers1 = integers.subList(0, 7);
        System.out.println(integers.size());
        System.out.println(integers1.size());
    }
}
