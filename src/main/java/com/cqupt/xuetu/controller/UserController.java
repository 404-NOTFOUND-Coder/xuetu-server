package com.cqupt.xuetu.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.cqupt.xuetu.entity.User;
import com.cqupt.xuetu.response.Result;
import com.cqupt.xuetu.response.ResultFactory;
import com.cqupt.xuetu.service.UserService;
import com.cqupt.xuetu.utils.DateUtils;
import com.cqupt.xuetu.utils.EmailUtils;
import com.cqupt.xuetu.utils.QiNiuUtil;
import com.cqupt.xuetu.utils.TokenUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class UserController {
    public static String beginDate;
    public static String endDate;
    public static String c_id;

    @Autowired
    private UserService userService;

    /**
     * 数据查询接口
     *
     * @param queryList queryList:{ tableName: '' , id: '', tableKey: ''}
     *                  tableName:数据库表名(必配)
     *                  id:键值,多个id查询可以传入一个数组，如不需要id,需要置空即 id:'' (选配)
     *                  tableKey:键名,如不需要tableKey,需要置空即 tableKey:'' (选配)
     * @return 200操作成功, 400操作失败
     */
    @RequestMapping("/QueryData")
    public Result QueryData(@RequestBody String queryList) {
        try {
            JSONObject json = new JSONObject(queryList);
            String key = json.getString("tableKey");
            String tablesName = json.getString("tableName");
            if (json.getJSONArray("id").isNull(0)) {
                List<Map<String, Object>> list = userService.select(key, null, tablesName);
                return ResultFactory.buildSuccessResult(list);
            } else {
                List<Object> list = new ArrayList<>();
                for (int i = 0; i < json.getJSONArray("id").length(); i++) {
                    list.add(userService.select(key, (String) json.getJSONArray("id").get(i), tablesName));
                }
                return ResultFactory.buildSuccessResult(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildResult(500, "服务器错误", 500);
        }
    }

    /**
     * 数据插入更新接口
     *
     * @param updateList updateList:{ header: [{ tableName: '', id: '', tableKey: '' }], content: [{ id: '', 字段名: '字段值' }] }
     *                   header:用于配置数据库表名、主键名、主键值
     *                   tableName:数据库表名(必配)
     *                   id:主键值(必配)
     *                   tableKey:主键名(必配)
     *                   content:数据部分，根据实际需求按照 字段名: '字段值' 进行插值(选配)
     * @return list 返回用户数据,400 操作失败
     */
    @RequestMapping("/UpdateData")
    public Result UpdateData(@RequestBody String updateList) {
        try {
            JSONObject jsonObject = new JSONObject(updateList);
            // 取头部信息
            String tablesName = jsonObject.getJSONArray("header").getJSONObject(0).getString("tableName");
            String userID = jsonObject.getJSONArray("header").getJSONObject(0).getString("id");
            String key = jsonObject.getJSONArray("header").getJSONObject(0).getString("tableKey");
            // 取内容信息,并用JSON类的parseObject来解析JSON字符串
            Map mapTypes = JSON.parseObject(jsonObject.getJSONArray("content").getJSONObject(0).toString());
            for (Object obj : mapTypes.keySet()) {
                mapTypes.put(obj, mapTypes.get(obj));
            }
            userService.updateData(tablesName, userID, key, mapTypes);
            return ResultFactory.buildResult(200, "操作成功", 200);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildResult(400, "操作失败", 400);
        }
    }

    /**
     * 数据删除接口
     *
     * @param deleteList deleteList:{ tableName: '' , id: '', tableKey: ''}
     *                   tableName:数据库表名(必配)
     *                   id:键值,多个id查询可以传入一个数组，如不需要id,需要置空即 id:'' (选配)
     *                   tableKey:键名,如不需要tableKey,需要置空即 tableKey:'' (选配)
     * @return 200操作成功, 400操作失败
     */
    @RequestMapping("/DeleteData")
    public Result DeleteData(@RequestBody String deleteList) {
        try {
            JSONObject jsonObject = new JSONObject(deleteList);
            String key = jsonObject.getString("tableKey");
            String keyValue = jsonObject.getString("id");
            String tablesName = jsonObject.getString("tableName");
            userService.delete(tablesName, key, keyValue);
            return ResultFactory.buildResult(200, "操作成功", 200);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildResult(400, "操作失败", 400);
        }
    }

    /**
     * 注册接口
     *
     * @param jsonList 传入接口数据
     * @return hs 返回Token注册成功,401 验证码不正确,400 用户名已经注册
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Result Register(@RequestBody String jsonList) {
        try {
            JSONObject json = new JSONObject(jsonList);
            List<Map<String, Object>> userList = userService.select("email", "'" + json.getString("email") + "'", json.getString("tableName"));
            if (userList == null || userList.size() == 0) {
                if (json.getString("confirmCode").equals(EmailUtils.getCode()) && json.getString("email").equals(EmailUtils.getEmail())) {
                    String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("username", json.getString("phone"));
                    map.put("password", json.getString("password"));
                    map.put("email", json.getString("email"));
                    map.put("phone", json.getString("phone"));
                    map.put("id", uuid);
                    map.put("createTime", json.getString("time"));
                    map.put("name", "默认用户名");
                    map.put("avatarurl", json.getString("avatarurl"));
                    map.put("type", json.getString("radio"));
                    userService.updateData("user", uuid, "id", map);
                    String token = TokenUtil.sign(new User(json.getString("phone"), json.getString("password")));
                    HashMap<String, Object> hs = new HashMap<>();
                    hs.put("token", token);
                    List<Map<String, Object>> list = userService.select("userName", json.getString("phone"), json.getString("tableName"));
                    hs.put("id", list.get(0).get("id"));
                    hs.put("nickname", list.get(0).get("name"));
                    hs.put("avatarurl", list.get(0).get("avatarurl"));
                    hs.put("type", list.get(0).get("type"));
                    return ResultFactory.buildSuccessResult(hs);
                } else {
                    return ResultFactory.buildResult(401, "验证码不正确", 401);
                }
            } else {
                return ResultFactory.buildResult(400, "用户名已经注册", 400);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildResult(500, "服务器错误", 500);
        }
    }

    /**
     * 登录接口
     *
     * @param jsonList 传入接口数据
     * @return hs 返回Token登录验证成功,500 账户或密码不正确
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result Login(@RequestBody String jsonList) {
        try {
            JSONObject json = new JSONObject(jsonList);
            List<Map<String, Object>> userList = userService.select("username", json.getString("username"), json.getString("tableName"));
            if (userList.size() != 0 && (String.valueOf(userList.get(0).get("password"))).equals(json.getString("password"))) {
                String token = TokenUtil.sign(new User(json.getString("username"), json.getString("password")));
                HashMap<String, Object> hs = new HashMap<>();
                hs.put("token", token);
                hs.put("id", userList.get(0).get("id"));
                hs.put("nickname", userList.get(0).get("name"));
                hs.put("avatarurl", userList.get(0).get("avatarurl"));
                hs.put("type", userList.get(0).get("type"));
                return ResultFactory.buildSuccessResult(hs);
            } else {
                return ResultFactory.buildResult(400, "账户或密码不正确", 400);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildResult(500, "服务器错误", 500);
        }
    }

    /**
     * 邮箱发送验证码
     *
     * @param email 传入email参数
     * @return 200 发送成功,400 发送失败
     */
    @RequestMapping("/sendCode")
    @ResponseBody
    public Result GetEmail(@RequestBody String email) {
        try {
            if (email != null) {
                EmailUtils sendEmail = new EmailUtils();
                String code = sendEmail.sendAuthCodeEmail(email);
                if (!Objects.equals(code, "0")) {
                    return ResultFactory.buildResult(200, "邮箱验证码发送成功", 200);
                } else {
                    return ResultFactory.buildResult(400, "邮箱验证码发送失败", 400);
                }
            } else {
                return ResultFactory.buildResult(400, "邮箱验证码发送失败", 400);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildResult(500, "服务器错误", 500);
        }
    }

    /**
     * 邮箱验证接口
     *
     * @param jsonList 发送邮箱验证内容
     * @return hs验证通过, 400验证不通过
     */
    @RequestMapping("/verifyEmail")
    public Result verifyEmail(@RequestBody String jsonList) {
        try {
            JSONObject json = new JSONObject(jsonList);
            List<Map<String, Object>> list = userService.select("email", "'" + json.getString("email") + "'", "user");
            if (list == null || list.size() == 0) {
                return ResultFactory.buildResult(401, "邮箱未注册", 401);
            } else {
                if (json.getString("code").equals(EmailUtils.getCode()) && json.getString("email").equals(EmailUtils.getEmail())) {
                    HashMap<String, Object> hs = new HashMap<>();
                    hs.put("code", 200);
                    hs.put("id", list.get(0).get("id"));
                    return ResultFactory.buildSuccessResult(hs);
                } else {
                    return ResultFactory.buildResult(400, "验证不通过", 400);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildResult(500, "服务器错误", 500);
        }
    }

    /**
     * 上传接口
     *
     * @param file 图片/PDF文件
     * @return 200上传成功 + url
     */
    @RequestMapping("/savePic")
    public Result saveFiles(MultipartFile file) {
        String url;   //图片上传到七牛云，返回的地址
        try (FileInputStream inputStream = (FileInputStream) file.getInputStream()) {
            url = QiNiuUtil.upload2Qiniu(inputStream, reName(file.getOriginalFilename()));
            StringBuffer sb = new StringBuffer();
            url = "http://" + sb.append(url).insert(28, "/");
            return ResultFactory.buildResult(200, "图片上传成功", url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResultFactory.buildResult(200, "图片上传成功", null);
    }

    // region 设置图片名称
    private String reName(String name) {
        if (StringUtils.isEmpty(name)) return "";
        int pos = name.lastIndexOf(".");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("-yyyyMMddHHmmssSSS");
        return name.substring(0, pos) + simpleDateFormat.format(new Date()) + name.substring(pos);
    }

    /**
     * 获取用户学习时间接口
     *
     * @param List 用户id和课程编号c_id
     * @return 200操作成功 500服务器错误
     */

    @RequestMapping("/getTime")
    public Result getTime(@RequestBody String List) {
        try {
            JSONObject json = new JSONObject(List);
            String uuid = json.getString("uuid");
            String type = json.getString("type");
            if (type.equals("beginDate")) {
                c_id = json.getString("c_id");
                beginDate = DateUtils.getSysTime();
            } else {
                String id = json.getString("id");
                endDate = DateUtils.getSysTime();
                List<Map<String, Object>> list = userService.select("stu_class_id", "'" + c_id + "'" + "AND p_id = " + "'" + uuid + "'", "student_course");
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                int newStudyTime = (int) list.get(0).get("studyTime") + (int) (DateUtils.dateDiff(dateFormat.parse(beginDate), dateFormat.parse(endDate)) / 1000);
                HashMap<String, Object> map = new HashMap<>();
                map.put("studyTime", newStudyTime);
                map.put("id", id);
                userService.updateData("student_course", id, "id", map);
            }

            return ResultFactory.buildResult(200, "操作成功", 200);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildResult(500, "服务器错误", 500);
        }
    }
}
