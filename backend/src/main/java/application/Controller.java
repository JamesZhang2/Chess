package application;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class Controller {
    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/")
    public ResponseEntity<String> index() {
        System.out.println("Hello world!");
        return new ResponseEntity<>("Hello world!", HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/button")
    public ResponseEntity<String> button() {
        System.out.println("Button clicked!");
        return new ResponseEntity<>("Received!", HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/text")
    public ResponseEntity<String> getText(@RequestBody Map<String, String> body) {
        System.out.println(body.get("text"));
        return new ResponseEntity<>("Received!", HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> body) {
        System.out.println("Username: " + body.get("login_username"));
        System.out.println("Password: " + body.get("login_password"));
        return new ResponseEntity<>("Received!", HttpStatus.OK);
    }
}
