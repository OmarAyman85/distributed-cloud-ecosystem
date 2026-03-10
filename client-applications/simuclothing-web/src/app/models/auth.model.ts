export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  username: string;
  email: string;
  roles: string[];
}

export interface User {
  username: string;
  email: string;
  roles: string[];
}
