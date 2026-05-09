package jdev.lojavirtual_fs.lojavirtual_fs;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextLoad implements ApplicationContextAware {
    /* Código Original, mas dando problema no static com  api-key

    @Autowired
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }*/

    private static ApplicationContext applicationContext; // ✅ static SEM @Autowired

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context; // ✅ atribui ao campo estático (sem this)
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
