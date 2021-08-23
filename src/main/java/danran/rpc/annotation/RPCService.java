package danran.rpc.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @Classname RPCService
 * @Description TODO
 * @Date 2021/8/23 15:11
 * @Created by LengDanran
 * 添加该注解即可成为 RPC 服务
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RPCService {
    String value() default "";
}
