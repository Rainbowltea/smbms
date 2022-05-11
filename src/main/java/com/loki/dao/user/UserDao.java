package com.loki.dao.user;

import com.loki.pojo.Role;
import com.loki.pojo.Users;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserDao {
    //得到登录的用户
    public Users getLoginUser(Connection connection, String userCode) throws SQLException;

    //修改当前用户密码
    public int updatePwd(Connection connection, int id, String password) throws SQLException;

    //查询用户总数
    public int getUsercount(Connection connection, String username, int userRole) throws SQLException;

    //通过条件查询
    public List<Users> getUserList(Connection connection, String userName, int userRole, int currentPaeNo, int pageSize) throws SQLException;

    //通过userId删除user
    public int deleteUserById(Connection connection, Integer delId) throws Exception;

    //增加用户信息
    int add(Connection connection, Users user) throws Exception;
}
