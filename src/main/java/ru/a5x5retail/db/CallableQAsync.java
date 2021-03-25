package ru.a5x5retail.db;




import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import ru.a5x5retail.RmqMainClass;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class CallableQAsync extends DataBaseQuery

{

    protected static final Logger log = LogManager.getLogger(CallableQAsync.class);

    public String getVstoreTsdDbName() {
        return "V_StoreTSD.dbo.";
    }

    public CallableQAsync()  {
        super();

    }

    /**********************************************************************************************/
    /*************************  Из BaseAsyncQuery  ************************************************/
    /**********************************************************************************************/

    private String sqlString;
    private Exception exception;
    public int returnCode = -100;



    public String eventMessage = "Не заполнено";
    private ResultSet rs;

    public boolean isDoError() {
        return isDoError;
    }

    private boolean isDoError;
    private boolean isRunning;

    protected  int parameterIndex = 1;



    protected Connection getConnection() {
        Connection c = null;
        long l = 1;
        while (c == null && !RmqMainClass.isStoppingService) {
            if (l % 6 == 0) {
                //sdxsds
            }
            try {
                c = new MsSqlConnection().getConnection();
            } catch (Throwable e) {
                log.error(e);
                e.printStackTrace();

                try {
                    Thread.sleep(1000);
                } catch (Throwable ex) {
                    log.error(ex);
                    e.printStackTrace();
                }

                l++;
            }
        }
        return c;
    }

    public String getSqlString() {
        return sqlString;
    }

    public void setSqlString(String sqlString) {
        this.sqlString = sqlString;
    }

    public ResultSet getResultSet() {
        return rs;
    }

    public void setResultSet(ResultSet rs) {
        this.rs = rs;
    }

    public String getEventMessage() {
        return eventMessage;
    }

    public void setEventMessage(String eventMessage) {
        this.eventMessage = eventMessage;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    protected void setReturnCode() throws SQLException {
       /* if (stmt == null) {
            return;
        }
        ResultSet rs = stmt.getResultSet();

       if (rs.isBeforeFirst()) {
            boolean b = stmt.getMoreResults(0);
            b = b;
        }
        setReturnCode((int) stmt.getObject(1));*/

    }

    public Exception getException() {
        return exception;
    }

    protected void setException(Exception exception) {
        this.exception = exception;
        isDoError = true;
    }

    /**********************************************************************************************/
    /**********************************************************************************************/


    private boolean isRowsAvailable;

    public boolean isRowsAvailable() {
        return isRowsAvailable;
    }

    protected void setRowsAvailable(boolean rowsAvailable) {
        isRowsAvailable = rowsAvailable;
    }

    /**********************************************************************************************/


    protected CallableStatement stmt = null;

    protected CallableStatement getStmt() {

        return stmt;
    }

    protected void createStatement() throws SQLException {
        if (stmt == null) {
            stmt = getConnection().prepareCall(getSqlString());
        }
    }

    protected abstract void SetQuery();
    protected abstract void SetQueryParams() throws SQLException;
    protected abstract void parseResultSet() throws Exception;
    protected abstract void parseOutputVars() throws Exception;

    public boolean Execute() {
        try {
            SetQuery();
            createStatement();
            SetQueryParams();
            stmt.execute();
            while (stmt.getUpdateCount() != -1 && !stmt.getMoreResults()) {
                stmt.getMoreResults();
            }

            if (stmt.getResultSet() != null && stmt.getResultSet().isBeforeFirst()) {
                setRowsAvailable(true);
                setResultSet(stmt.getResultSet());
            }
            if (isRowsAvailable) {
                parseResultSet();
            }
            stmt.getMoreResults();
            setReturnCode(stmt.getInt(1));
            parseOutputVars();

        } catch (Exception e) {
            setException(e);
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.getConnection().close();
                    stmt.close();
                }
            } catch (Throwable e) {
                log.error(e);
                e.printStackTrace();
            }


            if (getReturnCode() == 0) {
                sendOnSuccessfulListener();
            } else {
                sendAsyncQueryEvent(this, getReturnCode(), getEventMessage());
            }

            if (isDoError) {
                sendAsyncQueryException(this, getException());
            }
        }

        return !isDoError;
    }














}
