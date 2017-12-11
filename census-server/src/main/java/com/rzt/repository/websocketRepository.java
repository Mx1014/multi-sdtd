package com.rzt.repository;

import com.rzt.entity.websocket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @ProjectName: sdtd2-task
 * @Package: com.rzt.repository
 * @Author: liuze
 * @date: 2017-12-9 18:33
 */
@Repository
public interface websocketRepository extends JpaRepository<websocket, String> {
}
