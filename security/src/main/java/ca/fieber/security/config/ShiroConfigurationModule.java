package ca.fieber.security.config;

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.ProvisionException;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.apache.shiro.config.Ini;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.apache.shiro.realm.text.IniRealm;

import javax.servlet.ServletContext;

/**
 * Configures Shiro components.
 *
 * @author cfieber
 */
class ShiroConfigurationModule extends ShiroWebModule {

    private final String securityFilterPathSpec;
    
    /**
     * Constructs a new ShiroConfigurationModule for the provided ServletContext.
     *
     * @param servletContext the ServletContext for this ShiroConfigurationModule
     * @param securityFilterPathSpec the path spec that will be intercepted by the security filter chain
     */
    public ShiroConfigurationModule(ServletContext servletContext, String securityFilterPathSpec) {
        super(servletContext);
        this.securityFilterPathSpec = securityFilterPathSpec;
    }

    @Override
    protected void configureShiroWeb() {
        try {
            bindRealm().toConstructor(IniRealm.class.getConstructor(Ini.class));
        } catch (NoSuchMethodException nsme) {
            throw new ProvisionException("Binding realm failed", nsme);
        }
        bindConstant().annotatedWith(Names.named("shiro.port")).to(8443);

        addFilterChains();
    }

    /**
     * Adds the filter chains for the application.
     */
    @SuppressWarnings("unchecked")
    void addFilterChains() {
        addFilterChain(securityFilterPathSpec, SSL, AUTHC_BASIC);
    }

    /**
     * Provides the Ini to configure Shiro.
     *
     * @return the Ini to configure Shiro.
     */
    @Provides
    @Singleton
    Ini shiroIni() {
        return Ini.fromResourcePath("classpath:shiro.ini");
    }
}
