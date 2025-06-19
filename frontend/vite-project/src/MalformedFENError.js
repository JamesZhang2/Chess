class MalformedFENError extends Error {
    constructor(message) {
        super(message);
        this.name = "MalformedFENError";
    }
}

export default MalformedFENError;
