package danran.rpc.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 添加该注解即可成为 RPC 服务<br/>
 * 并且使用了Spring的 @Component 注解，可以使其作为bean注入到Spring容器中<br/>
 * 用户不需要在再使用注解注入bean
 *
 * @Classname RPCService
 * @Description TODO
 * @Date 2021/8/23 15:11
 * @Created by LengDanran
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RPCService {
    String value() default "";
}
