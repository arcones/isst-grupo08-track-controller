package es.upm.isst.grupo08.trackback;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
        title = "Trackermaster API",
        version = "1.0",
        description = "API to track and load parcels' tracking information"
))
public class TrackBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrackBackApplication.class, args);
    }

}
