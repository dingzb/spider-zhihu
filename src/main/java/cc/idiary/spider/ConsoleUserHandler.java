package cc.idiary.spider;

import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

public class ConsoleUserHandler extends AbstractUserHandler implements UniqueRecorder{

    private Map<String, Boolean> ids = new ConcurrentHashMap<>(); //判重用
    private static BlockingDeque<UserEntity> userQueue = new LinkedBlockingDeque<>(); //内部打印用队列
    private BlockingDeque<String> urlTokenQueue; //用于持续获取用户队列

    public ConsoleUserHandler(BlockingDeque<String> urlTokenQueue) {
        this.urlTokenQueue = urlTokenQueue;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        System.out.println(userQueue.take());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void handle(UserParser.UserConstruction userConstruction) {
        if (!exist(userConstruction.getId())){
            ids.put(userConstruction.getId(), true);
            UserEntity userEntity = userConstruction.getEntity();
            try {
                urlTokenQueue.put(userEntity.getUrlToken());
                userQueue.put(userEntity);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean exist(Object key) {
        return ids.containsKey(key);
    }
}
