package com.cqupt.xuetu.config;

import com.cqupt.xuetu.interceptor.TokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;


/**
 * 跨域请求支持/token拦截
 * tip:只能写在一个配置类里
 *
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final TokenInterceptor tokenInterceptor;

    /**
     * 构造方法
     */
    public WebConfiguration(TokenInterceptor tokenInterceptor) {
        this.tokenInterceptor = tokenInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowedOriginPatterns("*");
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor(new ConcurrentTaskExecutor(Executors.newFixedThreadPool(3)));
        configurer.setDefaultTimeout(30000);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> excludePath = new ArrayList<>();
        //排除拦截登录接口
        excludePath.add("/api/login");
        excludePath.add("/api/register");
        excludePath.add("/api/sendCode");
        excludePath.add("/api/verifyEmail");
        excludePath.add("/api/QueryData");
        excludePath.add("/api/savePic");
        excludePath.add("/api/UpdateData");
        excludePath.add("/api/recommend");
        excludePath.add("/alipay/**");
        //登录
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(excludePath);
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
