package com.loki.servlet.user;

import com.loki.pojo.Users;
import com.loki.service.user.UserService;
import com.loki.service.user.UserServiceImpl;
import com.loki.util.Constants;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
    //控制层调用业务层
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("LoginServlet---star....");
        //获取用户名和密码
        String userCode=req.getParameter("userCode");
        String userPassword=req.getParameter("userPassword");
        //和数据库中的密码进行对比，调用业务层
        UserService userService = new UserServiceImpl();
        Users login = userService.login(userCode, userPassword);

        if(login!=null){//查有此人，放入session中
            req.getSession().setAttribute(Constants.USER_SESSION,login);
            //登录成功，跳转页面
            resp.sendRedirect("jsp/frame.jsp");
        }
        else{
            //查无此人，无法登录
            //转发回登录界面，顺带提示它，用户名或者密码错误
            req.setAttribute("error","用户名或者密码不正确");
            req.getRequestDispatcher("login.jsp").forward(req,resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
