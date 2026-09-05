import apiClient from "./client";

export interface AuthUser {
  username: string;
  roles: string[];
}

export interface LoginRequest {
  username: string;
  password: string;
}

export async function login(data: LoginRequest): Promise<AuthUser> {
  await apiClient.post("/auth/login", data);

  const response = await apiClient.get<AuthUser>("/auth/me");

  return response.data;
}

export async function getCurrentUser(): Promise<AuthUser> {
  const response = await apiClient.get<AuthUser>("/auth/me");

  return response.data;
}

export async function logout(): Promise<void> {
  await apiClient.post("/auth/logout");
}