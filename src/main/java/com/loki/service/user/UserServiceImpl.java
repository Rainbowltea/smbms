package com.loki.service.user;

import com.loki.dao.BaseDao;
import com.loki.dao.user.UserDao;
import com.loki.dao.user.UserDaoImpl;
import com.loki.pojo.Users;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Year;
import java.util.List;

public class UserServiceImpl implements UserService {
    //引入Dao层
    private UserDao userDao;

    public UserServiceImpl() {
        userDao = new UserDaoImpl();
    }

    @Override
    public Users login(String userCode, String password) {
        Connection connection = null;
        Users user = null;

        try {
            connection = BaseDao.getConnection();
            user = userDao.getLoginUser(connection, userCode);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        if (user != null) {
            if (user.getUserPassword().equals(password))//验证密码是否正确
                return user;
        }
        return null;
    }

    @Override
    public boolean updatePwd(int id, String password) {
        Connection connection = null;
        boolean flag = false;
        try {
            connection = BaseDao.getConnection();
            if (userDao.updatePwd(connection, id, password) > 0) {
                flag = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }

    @Override
    public int getUserCount(String username, int userRole) {
        //创建连接
        Connection connection = null;
        int usercount = 0;
        try {
            connection = BaseDao.getConnection();
            usercount = userDao.getUsercount(connection, username, userRole);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return usercount;
    }

    @Override
    public List<Users> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize) {
        Connection connection = null;
        List<Users> usersList = null;
        try {
            connection = BaseDao.getConnection();
            usersList = userDao.getUserList(connection, queryUserName, queryUserRole, currentPageNo, pageSize);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return usersList;
    }

    @Override
    public boolean deleteUserById(Integer delId) {
        Connection connection = null;
        boolean flag = false;
        try {
            connection = BaseDao.getConnection();
            if (userDao.deleteUserById(connection, delId) > 0) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }

    @Override
    public boolean add(Users user) {
        boolean flag = false;
        Connection connection = null;
        try {
            connection = BaseDao.getConnection();
            connection.setAutoCommit(false);//开启JDBC事务管理
            int updateRows = userDao.add(connection, user);
            connection.commit();
            if (updateRows > 0) {
                flag = true;
                System.out.println("add success!");
            } else {
                System.out.println("add failed!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                System.out.println("rollback==================");
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            //在service层进行connection连接的关闭
            BaseDao.closeResource(connection, null, null);
        }
        return flag;
    }


//    @Test
//    public void test(){
//        UserServiceImpl userService = new UserServiceImpl();
//        int userconut=userService.getUserCount(null,1);
//        System.out.println(userconut);
//    }
}
//}   调试出一个sql错误，即"and"前没有加空格


//    @Test
//    public void test(){
//        UserServiceImpl userService=new UserServiceImpl();
//        //调用函数查询数据库中有无该人，并输出
//        Users admin=userService.login("admin","111111");
//        System.out.println(admin.getUserPassword());
//    }

