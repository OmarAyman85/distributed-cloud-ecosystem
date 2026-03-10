import { Component, OnInit } from '@angular/core';
import { SocketService } from '../services/socket.service'; // Service for handling socket.io communication
import { CommonModule } from '@angular/common'; // Angular common module for directives like *ngIf, *ngFor
import { FormsModule } from '@angular/forms'; // Enables two-way data binding via [(ngModel)]

// Define a standalone Angular component for the game UI and logic
@Component({
  selector: 'app-game', // The HTML tag used to embed this component
  standalone: true, // Marks this as a standalone component (no NgModule needed)
  imports: [CommonModule, FormsModule], // Import Angular modules needed for this component
  templateUrl: './game.component.html', // External HTML template
  styleUrls: ['./game.component.css'], // External CSS styles
})
export class GameComponent implements OnInit {
  username = ''; // Stores the player's username input
  joined = false; // Tracks if the player has joined the game
  ready = false; // Tracks if the player has marked themselves ready
  gameStarted = false; // Flag indicating if the game is currently running
  taps = 0; // Number of taps the player has made in the current game

  winner: string | null = null; // Stores the winner's username after game ends
  winnerScore: number | null = null; // Stores the winner's tap count
  gameOver = false; // Flag indicating that the game has ended

  countdown: number | null = null; // Countdown timer before game start in seconds
  gameTimer: number | null = null; // Timer for the 15-second game duration countdown

  private keyIsDown = false; // Internal flag to track whether the tap key is currently pressed

  constructor(private socketService: SocketService) {}

  ngOnInit(): void {
    // Listen for 'game-start' event from backend with the official start timestamp
    this.socketService.on('game-start', ({ startTime }) => {
      // Calculate delay until the game starts (in ms)
      const delay = startTime - Date.now();

      // Initialize countdown in seconds (rounding up)
      this.countdown = Math.ceil(delay / 1000);

      // Start an interval that updates the countdown every second before game starts
      const countdownInterval = setInterval(() => {
        if (this.countdown !== null) {
          this.countdown--;
          if (this.countdown <= 0) {
            clearInterval(countdownInterval);
            this.countdown = null;
            this.gameStarted = true;

            // Initialize the game timer to 15 seconds
            this.gameTimer = 15;

            // Listen to key events for tap counting
            window.addEventListener('keydown', this.handleKeyDown);
            window.addEventListener('keyup', this.handleKeyUp);

            // Start interval to count down game time every second
            const gameTimerInterval = setInterval(() => {
              if (this.gameTimer !== null) {
                this.gameTimer--;
                if (this.gameTimer <= 0) {
                  clearInterval(gameTimerInterval);
                  // Game time ended, can optionally do something here if needed
                  this.gameTimer = null;
                }
              }
            }, 1000);
          }
        }
      }, 1000);
    });

    // Listen for 'game-end' event containing the winner info
    this.socketService.on('game-end', (winner) => {
      // Remove event listeners to stop counting taps
      window.removeEventListener('keydown', this.handleKeyDown);
      window.removeEventListener('keyup', this.handleKeyUp);

      // Save winner information to display in UI
      this.winner = winner.winner;
      this.winnerScore = winner.score;
      this.gameOver = true; // Mark that the game is over

      // Reset game state for next round
      this.reset();
    });
  }

  // Called when player clicks "Join" button to join the game
  join() {
    this.socketService.joinGame(this.username); // Notify backend of new player
    this.joined = true; // Update local state to reflect joined status
  }

  // Called when player clicks "Ready" button to signal readiness
  readyUp() {
    this.socketService.signalReady(); // Notify backend player is ready
    this.ready = true; // Update local ready state
  }

  // Handler for 'keydown' event to register a tap
  handleKeyDown = (event: KeyboardEvent) => {
    // Only count a tap if key is not currently held down (prevents holding key)
    if (!this.keyIsDown) {
      this.keyIsDown = true; // Mark key as down to block repeated taps from holding key
      this.taps++; // Increment local tap count
      this.socketService.sendTap(); // Notify backend of a tap event
    }
  };

  // Handler for 'keyup' event to reset the key state when key is released
  handleKeyUp = (event: KeyboardEvent) => {
    this.keyIsDown = false; // Mark key as up, allowing next keydown to count as a tap
  };

  // Reset game-related state to initial values to prepare for a new game round
  reset() {
    this.ready = false;
    this.gameStarted = false;
    this.taps = 0;
    this.countdown = null;
    this.gameTimer = null;
    this.keyIsDown = false;
  }
}
