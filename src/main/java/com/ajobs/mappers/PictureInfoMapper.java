package com.ajobs.mappers;

import com.ajobs.tabledatas.PictureInfo;
import com.ajobs.tabledatas.UserInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PictureInfoMapper implements RowMapper<PictureInfo> {
    public PictureInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        PictureInfo pictureInfo = new PictureInfo(rs.getString("pictureUrl"),
                rs.getString("pictureName"));
        return pictureInfo;
    }
}