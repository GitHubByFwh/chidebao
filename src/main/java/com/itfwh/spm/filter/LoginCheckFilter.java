package com.itfwh.spm.filter;

import com.alibaba.fastjson.JSON;
import com.itfwh.spm.common.BaseContext;
import com.itfwh.spm.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author:Fwh
 * @date 2022/4/14 22:27
 * @ClassName LoginCheckFilter
 * @Description  检查用户是否已经完成登录
 */
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        /*
        * 1.获取本次请求的URI
        * 2.判断本次请求是否需要处理
        * 3.如果不需要处理，则直接放行
        * 4.判断登录状态，如果已经登录，则直接放行
        * 5.如果未登录则返回未登录结果
        * */
        //1.
        String requestURI = request.getRequestURI();//  /backend/index.html


        log.info("拦截到请求：{}",requestURI);
        //定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",//移动端发送短信
                "/user/login"//移动端登录
        };

        //2.
        boolean check = check(urls, requestURI);
        //3.
        if (check){//匹配到可以不做处理
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //4.1
        if (request.getSession().getAttribute("employee")!=null) {
            //已登录
            log.info("用户已登录：用户id:{}",request.getSession().getAttribute("employee"));

            Long empId = (Long) request.getSession().getAttribute("employee");
            //线程隔离设置用户ID
            BaseContext.setCurrentID(empId);

            long id = Thread.currentThread().getId();
            log.info("线程id为：{}" ,id);
            filterChain.doFilter(request,response);
            return;
        }
        //4.2判断移动端用户登录状态
        if (request.getSession().getAttribute("user")!=null) {
            //已登录
            log.info("用户已登录：用户id:{}",request.getSession().getAttribute("user"));

            Long userId = (Long) request.getSession().getAttribute("user");
            //线程隔离设置当前用户ID
            BaseContext.setCurrentID(userId);

            long id = Thread.currentThread().getId();
            log.info("线程id为：{}" ,id);
            filterChain.doFilter(request,response);
            return;
        }
        log.info("用户未登录");
        //5.返回登录页面，通过输出流的方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString((R.error("NOTLOGIN"))));
        return;
    }

    /**
     * 判断当前路径是否需要处理
     * @param urls
     * @param uri
     * @return
     */
    public boolean check (String[] urls,String uri){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, uri);
            if (match){
                return true;
            }
        }
        return false;
    }
}
