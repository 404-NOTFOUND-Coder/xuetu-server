package com.cqupt.xuetu.utils;

import org.apache.commons.mail.HtmlEmail;

import java.util.Random;

public class EmailUtils {
    public static String verificationCode;
    public static String verificationEmail;

    public static String getCode() {
        return verificationCode;
    }

    public static String getEmail() {
        return verificationEmail;
    }

    /**
     * 生成随机验证码
     *
     * @param number 几位数
     * @return 验证码
     */
    public String generateVerifyCode(int number) {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i <= number; i++) {
            builder.append(random.nextInt(10));
        }
        return builder.toString();
    }



    /**
     * 发送验证码
     *
     * @param email 邮箱
     * @return 0
     */
    public String sendAuthCodeEmail(String email) {
        try {
            HtmlEmail mail = new HtmlEmail();
            /*发送邮件的服务器 126邮箱为smtp.126.com,163邮箱为smtp.163.com，QQ为 smtp.qq.com*/
            mail.setHostName("smtp.163.com"); /*不设置发送的消息有可能是乱码*/
            mail.setCharset("UTF-8");
            /*IMAP/SMTP服务的密码*/
            /*
             * IMAP/SMTP服务的密码 LVUXGMEPYKCAPSSQ
             * */
            mail.setAuthentication("xueleme_admin@163.com", "LVUXGMEPYKCAPSSQ"); /*发送邮件的邮箱和发件人*/
            mail.setFrom("xueleme_admin@163.com", "学途知识共享平台安全中心"); /*使用安全链接*/
            mail.setSSLOnConnect(true); /*接收的邮箱*/
            mail.addTo(email);
            /*验证码*/
            verificationCode = this.generateVerifyCode(4);
            /*设置邮件的主题*/
            mail.setSubject("来自学途知识共享平台的验证码邮件");
            /*设置邮件的内容*/
            mail.setMsg("尊敬的用户:你好! 注册验证码为:" + verificationCode + "(有效期为一分钟)");
            mail.send();//发送
            verificationEmail = email;
            return verificationCode;
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }
}
