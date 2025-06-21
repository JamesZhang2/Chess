package application;

/**
 * Response to login, register, or play as guest
 */
public class LoginResponse {
    public final boolean success;
    public final String message;

    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
