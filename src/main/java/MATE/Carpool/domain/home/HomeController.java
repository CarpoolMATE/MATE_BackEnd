package MATE.Carpool.domain.home;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;


import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/")
    public String index() {
        return "카풀 메이트 API 서버";
    }
    
   @PostMapping("/")
    public ResponseEntity<String> post(@RequestBody String test){
        return ResponseEntity.ok("postMapping"+test);
    }


