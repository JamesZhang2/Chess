package application;

import controller.GUIGameController;
import model.board.IllegalBoardException;
import model.board.MalformedFENException;
import model.eval.MaterialEvaluator;
import model.player.HumanGUIPlayer;
import model.player.MinimaxAIPlayer;
import model.player.Player;
import model.player.RandomAIPlayer;
import org.springframework.http.HttpStatus;
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
    private Player whitePlayer;
    private Player blackPlayer;
    private GUIGameController gameController;

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
    public ResponseEntity<String> initGame(@RequestParam String whitePlayerType, @RequestParam String blackPlayerType) {
        System.out.printf("initGame called with white player: %s, black player: %s\n", whitePlayerType, blackPlayerType);
        switch (whitePlayerType) {
            case "HumanGUIPlayer":
                whitePlayer = new HumanGUIPlayer(true);
                break;
            case "RandomAIPlayer":
                whitePlayer = new RandomAIPlayer(true);
                break;
            case "MinimaxAIPlayer":
                whitePlayer = new MinimaxAIPlayer(true, new MaterialEvaluator(), 1);
                break;
            default:
                throw new IllegalArgumentException("Unknown white player: " + whitePlayerType);
        }
        switch (blackPlayerType) {
            case "HumanGUIPlayer":
                blackPlayer = new HumanGUIPlayer(false);
                break;
            case "RandomAIPlayer":
                blackPlayer = new RandomAIPlayer(false);
                break;
            case "MinimaxAIPlayer":
                blackPlayer = new MinimaxAIPlayer(false, new MaterialEvaluator(), 1);
                break;
            default:
                throw new IllegalArgumentException("Unknown white player: " + whitePlayerType);
        }
        // for testing promotions
        String testFEN = "q4k2/1P6/8/5K2/8/8/2p5/8 w - - 0 1";
        try {
            gameController = new GUIGameController(whitePlayer, blackPlayer, testFEN);
        } catch (MalformedFENException | IllegalBoardException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(gameController.getFEN(), HttpStatus.OK);
    }

    @GetMapping("/getCandidates")
    public ResponseEntity<CandidateMoves> getCandidates(@RequestParam String square) {
        return new ResponseEntity<>(gameController.getCandidateMoves(square), HttpStatus.OK);
    }

    @PostMapping("/tryMove")
    public ResponseEntity<UIMoveResponse> tryMove(@RequestBody UIMove uiMove) {
        System.out.println(uiMove);
        UIMoveResponse response = gameController.tryMove(uiMove);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
