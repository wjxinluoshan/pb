package com.ajobs.mappers;

import com.ajobs.tabledatas.UserInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserInfoMapper implements RowMapper<UserInfo> {
    public UserInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserInfo userInfo = new UserInfo();
        userInfo.setProfile(rs.getString("profile"));
        userInfo.setIntroduce(rs.getString("introduce"));
        userInfo.setHabit(rs.getString("habit"));
        return userInfo;
    }
}