package com.cqupt.xuetu.service;

import java.util.List;
import java.util.Map;

public interface UserService {
    public List<Map<String, Object>> select(String key, String value, String tablesName);

    public int updateData(String tablesName, String userID, String key, Map mapTypes);

    public int delete(String tablesName, String key, String keyValue);

}
