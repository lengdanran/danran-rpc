package danran.rpc.annotation;

import java.lang.annotation.*;

/**
 * @Classname InnjectService
 * @Description TODO
 * @Date 2021/8/23 15:15
 * @Created by ASUS
 * 用于添加引用RPC服务
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InjectService {
}
