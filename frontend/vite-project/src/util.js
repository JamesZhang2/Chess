import InvalidArgumentError from "./InvalidArgumentError";

/**
 * Converts the name of a square (like a1, e4, h8) to row and column values.
 * Requires: sqName is a valid square name.
 * @param {string} sqName name of the square to convert
 * @returns [r, c], the row and column values (between 0 and 7, inclusive)
 */
export function getRC(sqName) {
    if (sqName.length !== 2) {
        throw new InvalidArgumentError("sqName is not the name of a square: " + sqName);
    }
    const r = sqName.charCodeAt(1) - "1".charCodeAt(0);
    const c = sqName.charCodeAt(0) - "a".charCodeAt(0);
    if (r < 0 || r > 7) {
        throw new InvalidArgumentError("Illegal row: " + sqName.charAt(1));
    }
    if (c < 0 || c > 7) {
        throw new InvalidArgumentError("Illegal column " + sqName.charAt(0));
    }
    return [r, c];
}

/**
 * Converts row and column values into square name (like a1, e4, h8).
 * Requires: 0 <= r < = 7, 0 <= c <= 7.
 * @param {int} r row value
 * @param {int} c column value
 * @returns the name of the square
 */
export function getSquareName(r, c) {
    if (r < 0 || r > 7) {
        throw new InvalidArgumentError("Illegal row value: " + r);
    }
    if (c < 0 || c > 7) {
        throw new InvalidArgumentError("Illegal column value: " + c);
    }
    return String.fromCharCode("a".charCodeAt(0) + c) + (r + 1);
}