package ru.a5x5retail.db.update;




import ru.a5x5retail.db.CallableQAsync;
import ru.a5x5retail.model.OutputMessage;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class OutputMessageUpdateStatusQuery extends CallableQAsync {
    public OutputMessageUpdateStatusQuery(String outputMessageUpdateStatus, String newStatus) {
        this.outputMessageUpdateStatus = outputMessageUpdateStatus;
        this.newStatus = newStatus;
    }

    String outputMessageUpdateStatus, newStatus;

    @Override
    protected void SetQuery() {
        setSqlString("{? = call V_RMQ.dbo.OutputMessageUpdateStatus (?, ?)}");
    }

    @Override
    protected void SetQueryParams() throws SQLException {
        getStmt().registerOutParameter(parameterIndex++, Types.INTEGER);
        getStmt().setString(parameterIndex++,outputMessageUpdateStatus);
        getStmt().setString(parameterIndex++,newStatus);
    }

    @Override
    protected void parseResultSet() throws Exception {

    }

    @Override
    protected void parseOutputVars() throws Exception {

    }




}
