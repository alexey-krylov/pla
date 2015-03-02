package org.nthdimenzion.application;

import com.netflix.config.*;
import com.netflix.config.sources.URLConfigurationSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Support class for reading and updating the properties files dynamically.
 * Currently it supports only dynamic_prop.yml file.
 * <p/>
 * Created by pradyumna on 06-02-2015.
 */
public class ArchaiusPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer implements InitializingBean {

    private final String[] configFiles = new String[]{"dynamic_prop.yml"};

    /**
     * Resolve the given placeholder using the properties using Archaius DynamicPropertyFactory.
     *
     * @param placeholder the placeholder to resolve
     * @param props       this will always be empty, DynamicPropertyFactory is used instead to read the
     *                    current value.
     * @return the resolved value, of null if none
     */
    @Override
    protected String resolvePlaceholder(String placeholder, Properties props) {
        DynamicPropertyFactory dpf = DynamicPropertyFactory.getInstance();
        return dpf.getStringProperty(placeholder, null).get();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        AbstractPollingScheduler scheduler = new FixedDelayPollingScheduler();
        ConcurrentCompositeConfiguration finalConfig = new ConcurrentCompositeConfiguration();
        PolledConfigurationSource source = new URLConfigurationSourceForYAML(configFiles);
        DynamicConfiguration dynamicConfig = new DynamicConfiguration(source, scheduler);
        finalConfig.addConfiguration(dynamicConfig);
        ConfigurationManager.install(finalConfig);
    }

    /**
     * Static class for reading a YAML based resource file.
     */
    static class URLConfigurationSourceForYAML extends URLConfigurationSource {

        private final String[] configUrls;

        public URLConfigurationSourceForYAML(String... urls) {
            this.configUrls = urls;
        }

        @Override
        public PollResult poll(boolean initial, Object checkPoint) throws IOException {
            if (configUrls == null || configUrls.length == 0) {
                return PollResult.createFull(null);
            }
            Map<String, Object> map = new HashMap<>();
            for (String url : configUrls) {
                ClassPathResource resource = new ClassPathResource(url);
                YamlPropertySourceLoader yamlPropertySourceLoader = new YamlPropertySourceLoader();
                MapPropertySource propertySource = (MapPropertySource) yamlPropertySourceLoader.load(url, resource, null);
                for (String propName : propertySource.getPropertyNames()) {
                    map.put(propName, propertySource.getProperty(propName));
                }
            }
            return PollResult.createFull(map);
        }
    }
}
