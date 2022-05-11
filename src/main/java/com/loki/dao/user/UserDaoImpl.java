package com.loki.dao.user;

import com.loki.dao.BaseDao;
import com.loki.pojo.Role;
import com.loki.pojo.Users;
import com.mysql.cj.util.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {
    @Override
    public Users getLoginUser(Connection connection, String userCode) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        Users user = null;
        if (connection != null) {
            String sql = "SELECT * FROM smbms_user WHERE userCode= ?";
            Object[] params = {userCode};
            System.out.println(params[0]);
            //调用查询公共类
            rs = BaseDao.execute(connection, sql, params, pstm, rs);
            try {
                //rs = BaseDao.execute(connection, sql, params, pstm, rs);
                if (rs.next()) {
                    user = new Users();
                    user.setId(rs.getInt("id"));
                    user.setUserCode(rs.getString("userCode"));
                    user.setUserName(rs.getString("userName"));
                    user.setUserPassword(rs.getString("userPassword"));
                    user.setGender(rs.getInt("gender"));
                    user.setBirthday(rs.getDate("birthday"));
                    user.setPhone(rs.getString("phone"));
                    user.setAddress(rs.getString("address"));
                    user.setUserRole(rs.getInt("userRole"));
                    user.setCreatedBy(rs.getInt("createdBy"));
                    user.setCreationDate(rs.getTimestamp("creationDate"));
                    user.setModifyBy(rs.getInt("modifyBy"));
                    user.setModifyDate(rs.getTimestamp("modifyDate"));
                }
                BaseDao.closeResource(connection, pstm, rs);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    @Override
    //修改用户密码
    public int updatePwd(Connection connection, int id, String password) throws SQLException {
        PreparedStatement psvm = null;
        int execute = 0;//提高作用域
        if (connection != null) {
            String sql = "UPDATE smbms_user SET userPassword=? WHERE id=?";
            Object params[] = {password, id};
            execute = BaseDao.execute(connection, sql, params, psvm);
            BaseDao.closeResource(connection, psvm, null);
        }
        return execute;
    }

    @Override
    //根据用户名或者角色查询用户总数
    public int getUsercount(Connection connection, String username, int userRole) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        int count = 0;
        if (connection != null) {
            StringBuffer sql = new StringBuffer();//
            sql.append("select count(1) as count from smbms_user u,smbms_role r where u.userRole=r.id");
            ArrayList<Object> objects = new ArrayList<>();

            if (!StringUtils.isNullOrEmpty(username)) {
                sql.append("and u.userName like ?");//sql语句拼接，like模块查询
                objects.add("%" + username + "%");
            }

            if (userRole > 0) {
                sql.append(" and u.userRole = ?");
                objects.add(userRole);
            }
            Object[] params = objects.toArray();
            rs = BaseDao.execute(connection, sql.toString(), params, pstm, rs);
            if (rs.next()) {
                count = rs.getInt("count");
            }
            BaseDao.closeResource(null, pstm, rs);
        }
        return count;
    }

    @Override
    public List<Users> getUserList(Connection connection, String userName, int userRole, int currentPaeNo, int pageSize) throws SQLException {
        PreparedStatement pstm = null;
        ResultSet rs = null;
        List<Users> usersList = new ArrayList<>();
        if (connection != null) {
            StringBuffer sql = new StringBuffer();
            sql.append("select u.*,r.roleName as userRoleName from smbms_user u,smbms_role r where u.userRole = r.id");
            List<Object> list = new ArrayList<>();
            if (!StringUtils.isNullOrEmpty(userName)) {
                sql.append(" and u.userName like ?");
                list.add("%" + userName + "%");
            }
            if (userRole > 0) {
                sql.append(" and u.userRole = ?");
                list.add(userRole);
            }
            //分页使用
            sql.append(" order by creationDate DESC limit ?,?");
            currentPaeNo = (currentPaeNo - 1) * pageSize;
            list.add(currentPaeNo);
            list.add(pageSize);

            Object[] params = list.toArray();
            rs = BaseDao.execute(connection, sql.toString(), params, pstm, rs);
            while (rs.next()) {
                Users user = new Users();
                user.setId(rs.getInt("id"));
                user.setUserCode(rs.getString("userCode"));
                user.setUserName(rs.getString("userName"));
                user.setUserPassword(rs.getString("userPassword"));
                user.setGender(rs.getInt("gender"));
                user.setBirthday(rs.getDate("birthday"));
                user.setPhone(rs.getString("phone"));
                user.setUserRole(rs.getInt("userRole"));
                user.setUserRoleName(rs.getString("userRoleName"));
                usersList.add(user);
            }
            BaseDao.closeResource(null, pstm, rs);
        }
        return usersList;
    }

    @Override
    public int deleteUserById(Connection connection, Integer delId) throws Exception {
        PreparedStatement pstm = null;
        int flag = 0;
        if (null != connection) {
            String sql = "delete from smbms_user where id=?";
            Object[] params = {delId};
            flag = BaseDao.execute(connection, sql, params, pstm);
            BaseDao.closeResource(null, pstm, null);
        }
        return flag;
    }

    @Override
    public int add(Connection connection, Users user) throws Exception {
        PreparedStatement pstm = null;
        int updateRows = 0;
        if (null != connection) {
            String sql = "insert into smbms_user (userCode,userName,userPassword," +
                    "userRole,gender,birthday,phone,address,creationDate,createdBy) " +
                    "values(?,?,?,?,?,?,?,?,?,?)";
            Object[] params = {user.getUserCode(), user.getUserName(), user.getUserPassword(),
                    user.getUserRole(), user.getGender(), user.getBirthday(),
                    user.getPhone(), user.getAddress(), user.getCreationDate(), user.getCreatedBy()};
            updateRows = BaseDao.execute(connection, sql, params, pstm);
            BaseDao.closeResource(null, pstm, null);
        }
        return updateRows;
    }


}
