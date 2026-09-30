import apiClient from './client';
import type {LoginRequest, RegisterRequest} from "../Types.ts";


let token : string | null = null;
export const setToken = (newToken: string) => token = newToken;
export const getToken = () => {return token};

export const login = async (loginRequest: LoginRequest) =>
    apiClient.post("auth/login", loginRequest);

export const register = async (registerRequest: RegisterRequest) =>
    apiClient.post("auth/register", registerRequest);