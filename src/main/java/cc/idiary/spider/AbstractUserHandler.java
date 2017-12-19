package cc.idiary.spider;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public abstract class AbstractUserHandler implements UserHandler {

    @Override
    public void handle(UserParser parser) {
        Iterator<UserParser.UserConstruction> userConstructionIterator = parser.getConstruction();
        while (userConstructionIterator.hasNext()) {
            handle(userConstructionIterator.next());
        }
    }

    protected abstract void handle(UserParser.UserConstruction userConstruction);
}
