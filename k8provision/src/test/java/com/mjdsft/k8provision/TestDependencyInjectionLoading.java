package com.mjdsft.k8provision;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testcontainers.containers.MySQLContainer;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = KubernetesControllerApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TestDependencyInjectionLoading {

    @LocalServerPort
    private int port;

    @Rule
    public MySQLContainer mysql = new MySQLContainer();

    /**
     * This just causes the dependencies to resolve and connect to the db since this service is hard to
     * test --- i.e. it needs a k8s cluster to run against
     */
    @Test
    public void containerLoadTest() {


    }

}
