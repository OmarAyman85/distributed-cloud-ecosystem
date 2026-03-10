import { addPlayer, removePlayer } from "../game/playerManager.js";
import { handleTap, getWinner, resetGame } from "../game/gameManager.js";

// Flag to track whether a game is currently in progress
let gameStarted = false;

/**
 * Sets up all the socket.io event listeners for a connected client.
 * Handles player joins, readiness, taps, disconnections, and game flow.
 *
 * @param {import("socket.io").Server} io - The socket.io server instance
 */
function setupSocketHandlers(io) {
  // Listen for new client connections
  io.on("connection", (socket) => {
    /**
     * Event: 'join-game'
     * Triggered when a player joins the game.
     * Adds the player and notifies all clients.
     */
    socket.on("join-game", (username) => {
      addPlayer(socket.id, username);
      io.emit("player-joined", { id: socket.id, username });
    });

    /**
     * Event: 'ready'
     * Triggered when a player signals they are ready.
     * Starts the game if it hasn't already started.
     */
    socket.on("ready", () => {
      if (!gameStarted) {
        gameStarted = true;

        // Delay the actual game start to give players a countdown
        const startTime = Date.now() + 3000;
        io.emit("game-start", { startTime }); // Notify all clients when the game will start

        // After game duration (15 seconds + 5 seconds buffer), determine winner
        setTimeout(() => {
          const winner = getWinner();
          io.emit("game-end", winner); // Broadcast the winner to all clients
          resetGame(); // Reset scores for the next game
          gameStarted = false; // Mark the game as ended
        }, 15000 + 5000);
      }
    });

    /**
     * Event: 'tap'
     * Triggered when a player taps during the game.
     * Increases their score.
     */
    socket.on("tap", () => handleTap(socket.id));

    /**
     * Event: 'disconnect'
     * Triggered when a player leaves the game.
     * Removes them from the game and notifies other players.
     */
    socket.on("disconnect", () => {
      removePlayer(socket.id);
      io.emit("player-left", socket.id);
    });
  });
}

export { setupSocketHandlers };
