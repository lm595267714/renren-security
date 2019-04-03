package io.renren;

import io.renren.common.lock.CuratorLockUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.net.MalformedURLException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 分布式锁测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class CuratorLockTest {

    @Autowired
    private CuratorLockUtil curatorLock;

    /**
     * 无返回值
     * @throws MalformedURLException
     */
    @Test
    public void testConsumer() throws MalformedURLException {
        int age = 30;
        Consumer<Integer> consumer = (x) -> {
            cacluAge(x);
        };
        curatorLock.sharedReentrantLock("/home", consumer, 20);
    }

    /**
     * 有入参有出参
     */
    @Test
    public void testFunction(){
        Function<String,Integer> function = (str) ->{
            return stringToInteger(str);
        };
        Integer aaa = (Integer)curatorLock.sharedReentrantLock("/home", function, "123");
        System.out.println(aaa);
    }

    @Test
    public void testSupplier(){
        Supplier <Long> supplier = () ->{
                return getCurrent();
        };
        Long o =(Long) curatorLock.sharedReentrantLock("/home", supplier);
        System.out.println(o);
    }

    public Long getCurrent(){
        return System.currentTimeMillis();
    }


    public void cacluAge(Integer age) {
        age += 100;
        System.out.println(age);
    }

    public Integer stringToInteger(String value){
        return Integer.valueOf(value);
    }
}
