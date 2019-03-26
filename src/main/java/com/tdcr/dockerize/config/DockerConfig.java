package com.tdcr.dockerize.config;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DockerConfig {

    @Bean
    DefaultDockerClientConfig defaultDockerClientConfig(){
        DefaultDockerClientConfig config
                = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://localhost:5555").build();
        return config;
    }


    @Bean
    public DockerClient dockerClient (DefaultDockerClientConfig config){
        return DockerClientBuilder.getInstance(config).build();
    }


}
