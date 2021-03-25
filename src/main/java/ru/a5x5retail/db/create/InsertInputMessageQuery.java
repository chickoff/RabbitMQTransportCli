package ru.a5x5retail.db.create;




import ru.a5x5retail.db.CallableQAsync;

import java.sql.SQLException;
import java.sql.Types;

public class InsertInputMessageQuery extends CallableQAsync {
    public InsertInputMessageQuery(String sender, String partType, String xmlData) {
        this.sender = sender;
        this.partType = partType;
        this.xmlData = xmlData;
    }

    String sender, partType, xmlData;

    @Override
    protected void SetQuery() {
        setSqlString("{? = call V_RMQ.dbo.InsertInputMessage (?, ?, ?)}");
    }

    @Override
    protected void SetQueryParams() throws SQLException {
        getStmt().registerOutParameter(parameterIndex++, Types.INTEGER);
        getStmt().setString(parameterIndex++,sender);
        getStmt().setString(parameterIndex++,partType);
        getStmt().setString(parameterIndex++,xmlData);
    }

    @Override
    protected void parseResultSet() throws Exception {

    }

    @Override
    protected void parseOutputVars() throws Exception {

    }
}
