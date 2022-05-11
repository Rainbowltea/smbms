package com.loki.service.user;

import com.loki.pojo.Users;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserService {
    //用户登录
    public Users login(String userCode, String password);

    //根据用户id修改密码
    public boolean updatePwd(int id, String password);

    //查询记录数
    public int getUserCount(String username, int userRole);

    //根据条件查询用户列表
    public List<Users> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize);

    //通过id删除user
    public boolean deleteUserById(Integer delId);

    //增加用户信息
    public boolean add(Users user);
}
