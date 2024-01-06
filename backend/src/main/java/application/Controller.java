package application;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class Controller {
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/")
    public ResponseEntity<String> index() {
        System.out.println("Hello world!");
        return new ResponseEntity<>("Hello world!", HttpStatus.OK);
    }
}
