import { Injectable } from '@angular/core';
import io from 'socket.io-client';
import { environment } from '../../environments/environment';

/**
 * SocketService handles real-time communication with the backend server
 * using Socket.IO. It manages the connection and exposes methods to
 * emit events and listen for server messages related to the game.
 */
@Injectable({ providedIn: 'root' })
export class SocketService {
  // The socket instance used to communicate with the server
  private socket: ReturnType<typeof io>;

  /**
   * On service initialization, connect to the backend Socket.IO server
   * using the URL specified in environment configuration.
   */
  constructor() {
    this.socket = io(environment.socketUrl);
  }

  /**
   * Emit a 'join-game' event to notify the server that a player
   * with the given username has joined the game.
   * @param username - The player's chosen username
   */
  joinGame(username: string) {
    this.socket.emit('join-game', username);
  }

  /**
   * Emit a 'ready' event to signal the server that the player
   * is ready to start the game.
   */
  signalReady() {
    this.socket.emit('ready');
  }

  /**
   * Emit a 'tap' event whenever the player performs a tap action.
   * This increments the player's score on the server.
   */
  sendTap() {
    this.socket.emit('tap');
  }

  /**
   * Register a callback to listen for specific events emitted
   * from the server.
   * @param event - The name of the event to listen for
   * @param callback - The function to call with the event data when received
   */
  on(event: string, callback: (data: any) => void) {
    this.socket.on(event, callback);
  }
}
