package io.renren.common.lock;


import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Configuration
public class CuratorLockUtil<T> {

    private static Logger logger = org.slf4j.LoggerFactory.getLogger(CuratorLockUtil.class);

    @Value("${zk.connection.address}")
    private String connectString;

    private CuratorFramework client;

    @PostConstruct
    public void getCuratorFramework() {
        // 重试策略 初始休眠时间为 1000ms, 最大重试次数为 3
        RetryPolicy retry = new ExponentialBackoffRetry(1000, 3);
        // 创建一个客户端, 60000(ms)为 session 超时时间, 15000(ms)为链接超时时间
        client = CuratorFrameworkFactory.newClient(connectString, 60000, 15000, retry);
        client.start();

    }


    /**
     * 获取分布式锁并执行
     *
     * @param lockPath
     * @param function
     * @param param
     * @return
     */
    public Object sharedReentrantLock(String lockPath, Function<Object, Object> function, Object param) {
        InterProcessMutex lock = new InterProcessMutex(client, lockPath);
        try {
            try {
                if (lock.acquire(2, TimeUnit.SECONDS)) {
                    return function.apply(param);
                }
            } catch (Exception e) {
                logger.error("获取锁失败继续执行相关流程。。。。。。");
                return function.apply(param);
            }
            throw new RuntimeException("锁已被获取执行任务失败。。。。。。");
        } finally {
            try {
                lock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取分布式锁并执行（一个入参无返回值）
     *
     * @param lockPath
     * @param consumer
     * @param param
     */
    public void sharedReentrantLock(String lockPath, Consumer<Object> consumer, Object param) {
        InterProcessMutex mutex = new InterProcessMutex(client, lockPath);
        try {
            try {
                if (mutex.acquire(2, TimeUnit.SECONDS)) {
                    logger.info("lockPath{}获取锁成功开始执行任务",lockPath);
                    consumer.accept(param);
                    logger.info("lockPath{}获取锁成功任务执行成功",lockPath);
                    return;
                }
            } catch (Exception e) {
                logger.error("lockPath{}获取锁失败继续执行相关流程{}",lockPath,e);
                consumer.accept(param);
            }
            throw new RuntimeException("锁已被获取执行任务失败。。。。。。");
        } finally {
            try {
                mutex.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取分布式锁并执行（无入参有返回值）
     *
     * @param lockPath
     * @param consumer
     */
    public Object sharedReentrantLock(String lockPath, Supplier<Object> consumer) {
        InterProcessMutex mutex = new InterProcessMutex(client, lockPath);
        try {
            try {
                if (mutex.acquire(2, TimeUnit.SECONDS)) {
                    return consumer.get();
                }
            } catch (Exception e) {
                logger.error("获取锁失败继续执行相关流程。。。。。。");
                return consumer.get();
            }
            throw new RuntimeException("锁已被获取执行任务失败。。。。。。");
        } finally {
            try {
                mutex.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @PreDestroy
    public void destroy() {
        CloseableUtils.closeQuietly(client);
    }


}
