package application;

/**
 * Response to login, register, or play as guest
 */
public record LoginResponse(boolean success, String message) {
}
