package cc.idiary.spider;

import java.util.Collection;

public interface UserHandler {
    void handle(SimpleUserEntity user);
    void handle(Collection<SimpleUserEntity> users);
}
