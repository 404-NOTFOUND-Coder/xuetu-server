package com.cqupt.xuetu.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserDao {
    List<Map<String, Object>> select(String key, String value, String tablesName);

    int updateData(String tablesName, String userID, String key, Map mapTypes);

    int delete(String tablesName, String key, String keyValue);
}
