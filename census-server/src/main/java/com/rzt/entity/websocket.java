package com.rzt.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @ProjectName: sdtd2-task
 * @Package: com.rzt.entity
 * @Author: liuze
 * @date: 2017-12-9 18:31
 */
@Entity
@Table(name = "XS_ZC_TASK")
public class websocket extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String id;
}
