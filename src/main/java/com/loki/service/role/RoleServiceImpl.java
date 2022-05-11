package com.loki.service.role;

import com.loki.dao.BaseDao;
import com.loki.dao.role.RoleDao;
import com.loki.dao.role.RoleDaoImpl;
import com.loki.pojo.Role;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RoleServiceImpl implements RoleService {

    private RoleDao roleDao;

    public RoleServiceImpl(){
        roleDao=new RoleDaoImpl();
    }
    @Override
    public List<Role> getRoleList() {
        List<Role> roleList=null;//提出到try外面提高作用域
        Connection connection =null;
        try {
             connection=BaseDao.getConnection();
             roleList = roleDao.getRoleList(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            BaseDao.closeResource(connection,null,null);
        }
        return roleList;
    }
}
