package com.ajobs.mappers;

import com.ajobs.tabledatas.TitleDictumDiagramInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
public class TitleDictumDiagramInfoMapper implements RowMapper<TitleDictumDiagramInfo> {
    public TitleDictumDiagramInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        TitleDictumDiagramInfo titleDictumDiagramInfo = new TitleDictumDiagramInfo(
                rs.getString("titleName"),
                rs.getString("dictum"),
                rs.getString("titleBg")
        );
        return titleDictumDiagramInfo;
    }
}