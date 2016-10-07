package io.pivotal;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NLPController {
	
	@RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!\n";
    }

}
