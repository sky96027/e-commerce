package kr.hhplus.be.server;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.*;
import org.springframework.boot.origin.Origin;
import org.springframework.boot.origin.OriginLookup;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;

class KafkaOriginDumpTest extends IntegrationTestBase {

    @Autowired
    ConfigurableEnvironment env;

    @Autowired(required = false)
    KafkaAdmin kafkaAdmin;

    @Autowired
    KafkaTemplate<?,?> template;

    @Test
    void dumpKafkaBootstrapOrigin() {
        String key = "spring.kafka.bootstrap-servers";
        System.out.println(">>> activeProfiles = " + String.join(",", env.getActiveProfiles()));

        for (PropertySource<?> ps : env.getPropertySources()) {
            if (ps.containsProperty(key)) {
                Object val = ps.getProperty(key);
                Origin origin = OriginLookup.getOrigin(ps, key);
                System.out.println(">>> " + ps.getName() + " -> " + key + " = " + val + " | origin=" + origin);
            }
        }
        System.out.println(">>> EFFECTIVE = " + env.getProperty(key));
    }

    @Test
    void printKafkaAdminConfig() {
        if (kafkaAdmin == null) {
            System.out.println(">>> No KafkaAdmin bean");
            return;
        }
        var props = kafkaAdmin.getConfigurationProperties();
        System.out.println(">>> KafkaAdmin props = " + props);
        System.out.println(">>> BOOTSTRAP = " + props.get(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG));
    }

}
