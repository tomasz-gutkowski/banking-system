export type JwtClaims = {
    sub: string;
    roles: string[];
    iat: number;
    exp: number;
}

export type LoginRequest = {
    username: string,
    password: string
}

export type RegisterRequest = {
    username: string,
    password: string
}

export type ExceptionResponse = {
    "status": number,
    "message": string,
    "path": string,
    "timestamp": string
}

export type RegisterResponse = {
    "message": string,
}

export type LoginResponse = {
    "message": string,
    "jwt": string,
}