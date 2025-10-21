package com.zone.zone01blog;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HelloController {
    
    @GetMapping("/")
    public String Welcome() {
        return "weeeeeeeeeee";
    }


    @GetMapping("hello")
    public String HelloWorld(){
        return "Helllooow";
    }
    
}
