package com.cqupt.xuetu.service.impl;

import com.cqupt.xuetu.dao.UserDao;
import com.cqupt.xuetu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public List<Map<String, Object>> select(String key, String value, String tablesName) {
        return userDao.select(key, value, tablesName);
    }

    @Override
    public int updateData(String tablesName, String userID, String key, Map mapTypes) {
        return userDao.updateData(tablesName, userID, key, mapTypes);
    }

    @Override
    public int delete(String tablesName, String key, String keyValue) {
        return userDao.delete(tablesName, key, keyValue);
    }
}
