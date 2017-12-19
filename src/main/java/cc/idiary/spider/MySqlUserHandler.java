package cc.idiary.spider;

import java.sql.Connection;

public class MySqlUserHandler extends AbstractUserHandler implements UniqueRecorder{

    private static Connection conn = Class.forName("")
    @Override
    protected void handle(UserParser.UserConstruction userConstruction) {

    }

    @Override
    public boolean exist(Object key) {
        return false;
    }
}
