package com.loki.servlet.user;

import com.alibaba.fastjson.JSONArray;
import com.loki.pojo.Role;
import com.loki.pojo.Users;
import com.loki.service.role.RoleServiceImpl;
import com.loki.service.user.UserService;
import com.loki.service.user.UserServiceImpl;
import com.loki.util.Constants;
import com.loki.util.PageSupport;
import com.mysql.cj.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//实现servlet复用
public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if (method.equals("savepwd") && method != null) {
            this.updatePwd(req, resp);
        } else if (method.equals("pwdmodify") && method != null) {
            this.pmdModify(req, resp);
        } else if (method.equals("query") && method != null) {
            this.query(req, resp);
        } else if (method.equals("delUser")) {
            this.delUser(req, resp);
        } else if (method.equals("add")) {
            this.add(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    public void updatePwd(HttpServletRequest req, HttpServletResponse resp) {
        //从Session中拿id
        Object attribute = req.getSession().getAttribute(Constants.USER_SESSION);
        String newpassword = req.getParameter("newpassword");
        boolean flag = false;
        if (attribute != null && !StringUtils.isNullOrEmpty(newpassword)) {//等于null返回true,非空返回false
            UserService userService = new UserServiceImpl();
            flag = userService.updatePwd(((Users) attribute).getId(), newpassword);
            if (flag) {
                req.setAttribute("message", "修改密码成功，请退出，使用新密码登录");
                //密码修改成功溢出Session
                req.getSession().removeAttribute(Constants.USER_SESSION);
            } else {
                req.setAttribute("messsage", "密码修改失败");
            }
        } else {
            req.setAttribute("message", "密码格式不正确");
        }
        try {
            req.getRequestDispatcher("pwdmodify.jsp").forward(req, resp);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pmdModify(HttpServletRequest req, HttpServletResponse resp) {
        Object attribute = req.getSession().getAttribute(Constants.USER_SESSION);
        String oldpassword = req.getParameter("oldpassword");
        //万能的Map: 结果集
        Map<String, String> resultMap = new HashMap<String, String>();
        if (attribute == null) {
            resultMap.put("result", "sessionerror");
        } else if (StringUtils.isNullOrEmpty(oldpassword)) {
            resultMap.put("result", "error");
        } else {
            String userPassword = ((Users) attribute).getUserPassword();//session中用户的密码
            if (oldpassword.equals(userPassword)) {
                resultMap.put("result", "true");
            } else {
                resultMap.put("result", "false");
            }
        }

        try {
            PrintWriter writer = resp.getWriter();
            resp.setContentType("application/json");
            writer.write(JSONArray.toJSONString(resultMap));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void query(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        //查询用户列表，从前段userlist.jsp中获取数据
        String queryUserName = req.getParameter("queryname");
        String temp = req.getParameter("queryUserRole");
        String pageIndex = req.getParameter("pageIndex");
        int queryUserRole = 0;//前段可能没有对用户Role进行选择，默认为0；

        //获取用户列表
        UserServiceImpl userService = new UserServiceImpl();
        //第一次访问是第一页，页面大小固定
        int pageSize = 5;
        int currentPageNo = 1;

        if (queryUserName == null) {
            queryUserName = "";
        }
        if (temp != null && !temp.equals("")) {
            queryUserRole = Integer.parseInt(temp);//给查询赋值
        }
        if (pageIndex != null) {
            //在此处加了个try-catch发现网页报错500
            try {
                currentPageNo = Integer.parseInt(pageIndex);
            } catch (NumberFormatException e) {
                resp.sendRedirect("error.jsp");
            }

        }
        //调用方法获取用户的总数
        int userCount = userService.getUserCount(queryUserName, queryUserRole);
        //分页有上一页下一页，调用分页工具pageSupport
        PageSupport pageSupport = new PageSupport();
        pageSupport.setCurrentPageNo(currentPageNo);
        pageSupport.setPageSize(pageSize);
        pageSupport.setTotalCount(userCount);

        int totalpageCount = pageSupport.getTotalPageCount();//调错方法
        //控制尾页和首页
        if (currentPageNo < 1) {
            currentPageNo = 1;
        } else if (currentPageNo > pageSize) {
            currentPageNo = totalpageCount;//是否直接等于pagesize=5?
        }


        //将数据布置到前端
        List<Users> usersList = userService.getUserList(queryUserName, queryUserRole, currentPageNo, pageSize);
        req.setAttribute("userList", usersList);

        RoleServiceImpl roleService = new RoleServiceImpl();
        List<Role> roleList = roleService.getRoleList();
        //跳转页面后出现空白
        /*
        debug过程：
        一：仔细查看req重定向路径
            使用重定向能够正常跳转
        二：将req.setAttribute注释掉重新运行项目观察
            发现能够正常显示跳转页面
        三：注解掉req.set，设置test.jsp页面，发现能够正常跳转
            取消注解，发现同样能正常跳转
        四：在行118加入try-catch，服务器报错500，并提示无法在类型[com.loki.pojo.Role]上找不到属性
            经查找后发现是大小写问题。。。。（痛哭）
            修正后页面能实现翻页，但未能显示用户列表
        五：查找用户表格显示区代码
            发现是sql.append(" order by creationDate DESC limit ?,?");语句拼写错误。。
        最后解决以上所有问题，页面正常显示
         */

        req.setAttribute("queryUserName", queryUserName);
        req.setAttribute("queryUserRole", queryUserRole);
        req.setAttribute("totalPageCount", totalpageCount);
        req.setAttribute("totalCount", userCount);
        req.setAttribute("roleList", roleList);
        req.setAttribute("userList", usersList);
        req.setAttribute("currentPageNo", currentPageNo);

        req.getRequestDispatcher("userlist.jsp").forward(req, resp);
        //返回前段测试
//        try {
//            req.getRequestDispatcher("userlist.jsp").forward(req,resp);
//        } catch (ServletException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void delUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("uid");
        Integer delId = 0;
        try {
            delId = Integer.parseInt(id);
        } catch (Exception e) {
            delId = 0;
        }
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if (delId <= 0) {
            resultMap.put("delResult", "notexist");
        } else {
            UserService userService = new UserServiceImpl();
            if (userService.deleteUserById(delId)) {
                resultMap.put("delResult", "true");
            } else {
                resultMap.put("delResult", "false");
            }
        }

        //把resultMap转换成json对象输出
        resp.setContentType("application/json");
        PrintWriter outPrintWriter = resp.getWriter();
        outPrintWriter.write(JSONArray.toJSONString(resultMap));
        outPrintWriter.flush();
        outPrintWriter.close();
    }

    private void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userCode = request.getParameter("userCode");
        String userName = request.getParameter("userName");
        String userPassword = request.getParameter("userPassword");
        String gender = request.getParameter("gender");
        String birthday = request.getParameter("birthday");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String userRole = request.getParameter("userRole");

        Users user = new Users();
        user.setUserCode(userCode);
        user.setUserName(userName);
        user.setUserPassword(userPassword);
        user.setAddress(address);
        try {
            user.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(birthday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setGender(Integer.valueOf(gender));
        user.setPhone(phone);
        user.setUserRole(Integer.valueOf(userRole));
        user.setCreationDate(new Date());
        user.setCreatedBy(((Users) request.getSession().getAttribute(Constants.USER_SESSION)).getId());

        UserService userService = new UserServiceImpl();
        if (userService.add(user)) {
            response.sendRedirect(request.getContextPath() + "/jsp/user.do?method=query");
        } else {
            request.getRequestDispatcher("/jsp/useradd.jsp").forward(request, response);
        }

    }
}
