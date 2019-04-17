package com.gyb.chat.dao;

import com.gyb.chat.bean.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecordDao extends JpaRepository<Record,Long> {

    @Query("from Record r where r.to=?1 and r.used=false")
    List<Record> findUnread(long userid);

    @Query("from Record r where r.from=?1")
    List<Record> findByFromUserId(Long id);
}
