process.loadEnvFile();

export const port = process.env.PORT;
export const user_jwt_expires_in = '7d';
export const secret = process.env.JWT_SECRET;
export const resetSecret = process.env.JWT_RESET_SECRET;
export const setupSecret = process.env.JWT_SETUP_SECRET;
export const user_jwt_reset_expires_in = '30m';
export const setup_jwt_expires_in = '30m';