package itstime.reflog.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER) // 파라미터에서 사용
@Retention(RetentionPolicy.RUNTIME) // 런타임까지 유지
@Documented
public @interface UserId {
}
