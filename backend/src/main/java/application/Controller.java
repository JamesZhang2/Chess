package application;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost")
public class Controller {
    private int guestCounter = 0;

    @GetMapping("/")
    public ResponseEntity<String> index() {
        System.out.println("Hello world!");
        return new ResponseEntity<>("Hello world!", HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> body) {
        System.out.println("Username: " + body.get("login_username"));
        System.out.println("Password: " + body.get("login_password"));
        return new ResponseEntity<>("Login successful!", HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> body) {
        System.out.println("Username: " + body.get("register_username"));
        System.out.println("Password: " + body.get("register__password"));
        return new ResponseEntity<>("Register successful!", HttpStatus.OK);
    }

    @PostMapping("/playAsGuest")
    public ResponseEntity<String> playAsGuest() {
        String username = "_guest" + (guestCounter++);
        return new ResponseEntity<>(username, HttpStatus.OK);
    }
}
