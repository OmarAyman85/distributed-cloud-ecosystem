// Import the function to access the current map of connected players
import { getPlayers } from "./playerManager.js";

/**
 * Increments the score of the player with the given ID.
 * This function is triggered when a player taps in the game.
 *
 * @param {string} id - The unique identifier of the player.
 */
function handleTap(id) {
  const player = getPlayers().get(id); // Retrieve the player object from the map
  if (player) {
    player.score++; // Increment the player's score if they exist
  }
}

/**
 * Determines the player with the highest score.
 * If no players are present, returns "No one" as the winner.
 *
 * @returns {{ winner: string, score: number }} - The username of the winner and their score.
 */
function getWinner() {
  let maxScore = 0;
  let winner = "No one";

  // Iterate through all players to find the one with the highest score
  getPlayers().forEach((player) => {
    if (player.score > maxScore) {
      maxScore = player.score;
      winner = player.username;
    }
  });

  return { winner, score: maxScore };
}

/**
 * Resets the score of all players to 0.
 * Typically used to start a new round or game session.
 */
function resetGame() {
  getPlayers().forEach((player) => (player.score = 0));
}

// Export the game logic functions to be used in other parts of the application
export { handleTap, getWinner, resetGame };
