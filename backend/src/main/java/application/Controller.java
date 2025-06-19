package application;

import controller.GameController;
import model.board.BitmapBoard;
import model.board.Board;
import model.board.MalformedFENException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * The API that connects the frontend and the backend.
 */
@RestController
//@CrossOrigin(origins = "http://localhost")
@CrossOrigin(origins = "*")  // TODO: Only allow localhost but allow any port
public class Controller {
    private int guestCounter = 0;
    private Board board;

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
        System.out.println("Password: " + body.get("register_password"));
        return new ResponseEntity<>("Register successful!", HttpStatus.OK);
    }

    @GetMapping("/playAsGuest")
    public ResponseEntity<String> playAsGuest() {
        String username = "_guest" + (guestCounter++);
        return new ResponseEntity<>(username, HttpStatus.OK);
    }

    @GetMapping("/initGame")
    public ResponseEntity<String> initGame() {
        try {
            board = new BitmapBoard("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 1");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("initGame called");
        return new ResponseEntity<>(board.toFEN(), HttpStatus.OK);
    }

    @GetMapping("/getCandidates")
    public ResponseEntity<CandidateMoves> getCandidates(@RequestParam String square) {
        return new ResponseEntity<>(new CandidateMoves(board, square), HttpStatus.OK);
    }

    @PostMapping("/tryMove")
    public ResponseEntity<UIMoveResponse> tryMove(@RequestBody UIMove uiMove) {
        System.out.println(uiMove);
        // TODO: Write logic to handle UI moves
        // for now, we'll say all moves are illegal
        return new ResponseEntity<>(new UIMoveResponse(board.toFEN(), false, board.getWinner()), HttpStatus.OK);
    }
}
