export type MessageResponse = { message: string };

export type SignUpRequest = {
    username: string;
    email: string;
    password: string;
    confirmPassword: string;
}