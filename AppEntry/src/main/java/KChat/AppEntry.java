package KChat;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.Resource;

@SpringBootApplication
@EnableAsync
public class AppEntry implements CommandLineRunner {
    @Resource
    private NettyServer nettyServer;

    public static void main(String[] args){
        SpringApplication.run(AppEntry.class,args);
    }

    @Override
    public void run(String... args) throws Exception {
        nettyServer.initAndRun();
    }
}
