package ru.a5x5retail.db.read;




import ru.a5x5retail.Settings;
import ru.a5x5retail.db.CallableQAsync;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class GetAllowExchangesQuery extends CallableQAsync {
    public GetAllowExchangesQuery() {

    }

    @Override
    protected void SetQuery() {
        setSqlString("{? = call V_RMQ.dbo.GetAllowExchanges ()}");
    }

    @Override
    protected void SetQueryParams() throws SQLException {
        getStmt().registerOutParameter(parameterIndex++, Types.INTEGER);
    }


    @Override
    protected void parseResultSet() throws Exception {
        allowExchangesList = new ArrayList<>();

        while (getResultSet().next()) {
            Settings.AllowExchanges allowExchange = new Settings.AllowExchanges();
            allowExchange.exchangeName = getResultSet().getString("ExchangeName");

            String routingKey = getResultSet().getString("RoutingKey");
            allowExchange.routingKey = routingKey == null ? "": routingKey;

            allowExchange.isEnable = getResultSet().getBoolean("isEnable");
            allowExchangesList.add(allowExchange);
        }
    }

    @Override
    protected void parseOutputVars() throws Exception {

    }

    private List<Settings.AllowExchanges> allowExchangesList;
    public List<Settings.AllowExchanges> getAllowExchangesList() {
        if (allowExchangesList == null) {
            allowExchangesList = new ArrayList<>();
        }
        return allowExchangesList;
    }


}
