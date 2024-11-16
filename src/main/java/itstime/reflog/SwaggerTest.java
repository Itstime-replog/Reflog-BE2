package itstime.reflog;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/example")
public class SwaggerTest {
    @GetMapping("/hello")
    public String hello() {
        return "Hello, Swagger!";
    }
}