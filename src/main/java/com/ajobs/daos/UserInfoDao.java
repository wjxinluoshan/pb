package com.ajobs.daos;

import com.ajobs.tabledatas.UserInfo;

import javax.sql.DataSource;
import java.util.List;

public interface UserInfoDao {

    public void setDataSource(DataSource ds);

    public int uploadUserInfo(String profile, String introduce, String habit);

    public UserInfo getUserInfo();

//    public List<UserInfo> listUserInfos();

//    public void delete(Integer id);
//
//    public void update(Integer id, Integer age);
}
