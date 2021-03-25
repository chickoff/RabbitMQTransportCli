package ru.a5x5retail.db.read;


import ru.a5x5retail.db.CallableQAsync;


import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;


public class GetSettingsQuery extends CallableQAsync {

    public GetSettingsQuery(String settingName) {
        this.settingName = settingName;
    }

    @Override
    protected void SetQuery() {
        setSqlString("{? = call V_RMQ.dbo.GetDefaultSettingsByName(?,?)}");
    }

    Date date = new Date(System.currentTimeMillis());
    String settingName;

    @Override
    protected void SetQueryParams() throws SQLException {
        getStmt().registerOutParameter(parameterIndex++, Types.NVARCHAR);
        getStmt().setString(parameterIndex++, settingName);
        getStmt().registerOutParameter(parameterIndex++, microsoft.sql.Types.SQL_VARIANT);

    }

    @Override
    protected void parseResultSet() throws Exception {

    }

    @Override
    protected void parseOutputVars() throws Exception {
        result = getStmt().getString(3);
    }


    private String result;


    public String getResult() {
        return result;
    }
}
