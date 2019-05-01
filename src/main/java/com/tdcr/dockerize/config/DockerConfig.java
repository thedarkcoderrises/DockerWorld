package com.tdcr.dockerize.config;


import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DockerConfig {

    @Value("${HOST_NAME:localhost}")
    String host;


    @Bean
    DefaultDockerClientConfig defaultDockerClientConfig(){
        DefaultDockerClientConfig config
                = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://"+host+":5555").build();
        return config;
    }


    @Bean
    public DockerClient dockerClient (DefaultDockerClientConfig config){
        return DockerClientBuilder.getInstance(config).build();
    }


}
