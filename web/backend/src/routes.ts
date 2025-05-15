import { Router } from "express";
import { loginUser, signUpUser } from "./controllers/user-controller";

export const router = Router();

// Rota de cadastro
router.put("/signUp", signUpUser);
router.put("/login", loginUser);