package com.ajobs.mappers;

import com.ajobs.tabledatas.DocInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DocInfoMapper implements RowMapper<DocInfo> {
    public DocInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        DocInfo docInfo = new DocInfo(rs.getString("docFilePath"),
                rs.getString("docFileName"));
        return docInfo;
    }
}
