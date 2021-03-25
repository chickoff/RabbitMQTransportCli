package ru.a5x5retail.db.read;




import ru.a5x5retail.db.CallableQAsync;
import ru.a5x5retail.model.OutputMessage;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class GetOutputMessageQuery extends CallableQAsync {
    public GetOutputMessageQuery() {

    }

    @Override
    protected void SetQuery() {
        setSqlString("{? = call V_RMQ.dbo.getOutputMessage ()}");
    }

    @Override
    protected void SetQueryParams() throws SQLException {
        getStmt().registerOutParameter(parameterIndex++, Types.INTEGER);
    }

    @Override
    protected void parseResultSet() throws Exception {
        outputMessageList = new ArrayList<>();

        while (getResultSet().next()) {
            OutputMessage message = new OutputMessage();
            message.guid = getResultSet().getString("Guid");
            message.sender = getResultSet().getString("Sender");
            message.receiver = getResultSet().getString("Receiver");
            message.partType = getResultSet().getString("PartType");
            message.xmlData = getResultSet().getString("XmlData");
            outputMessageList.add(message);
        }
    }

    @Override
    protected void parseOutputVars() throws Exception {

    }

    private List<OutputMessage> outputMessageList;
    public List<OutputMessage> getOutputMessageList() {
        if (outputMessageList == null) {
            outputMessageList = new ArrayList<>();
        }
        return outputMessageList;
    }


}
