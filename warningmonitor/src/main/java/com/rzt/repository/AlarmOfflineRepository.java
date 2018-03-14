package com.rzt.repository;

import com.rzt.entity.AlarmOffline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmOfflineRepository extends JpaRepository<AlarmOffline, String> {

}
