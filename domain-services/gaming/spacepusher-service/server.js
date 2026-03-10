// Import necessary modules
import express from "express";
import { createServer } from "http";
import cors from "cors";
import { Server } from "socket.io";
import { setupSocketHandlers } from "./socket/index.js";

// Initialize the Express application
const app = express();

// Enable CORS so the frontend (e.g., Angular app on localhost:4200) can communicate with this backend
app.use(cors());

// Create an HTTP server using the Express app
const server = createServer(app);

// Initialize a new Socket.IO server, attached to the HTTP server
const io = new Server(server, {
  cors: {
    origin: "http://localhost:4200", // Allow connections from the Angular development server
    methods: ["GET", "POST"], // Specify allowed HTTP methods
  },
});

// Register all socket event handlers (connections, game events, etc.)
setupSocketHandlers(io);

// Start the HTTP and WebSocket server on the defined port
const PORT = 3000;
server.listen(PORT, () => {});
