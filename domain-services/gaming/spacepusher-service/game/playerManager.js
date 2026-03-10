// In-memory store for all connected players.
// The key is the player's unique socket ID, and the value is an object containing username and score.
const players = new Map();

/**
 * Adds a new player to the game.
 * Initializes their score to 0.
 *
 * @param {string} id - The unique identifier of the player (usually socket ID).
 * @param {string} username - The display name of the player.
 */
function addPlayer(id, username) {
  players.set(id, { username, score: 0 });
}

/**
 * Removes a player from the game by their ID.
 * This is typically called when a player disconnects.
 *
 * @param {string} id - The unique identifier of the player to remove.
 */
function removePlayer(id) {
  players.delete(id);
}

/**
 * Retrieves the current map of all players.
 * Useful for accessing or iterating over all players.
 *
 * @returns {Map<string, {username: string, score: number}>} - The map of player objects keyed by ID.
 */
function getPlayers() {
  return players;
}

// Export the player management functions for use in game logic modules
export { addPlayer, removePlayer, getPlayers };
