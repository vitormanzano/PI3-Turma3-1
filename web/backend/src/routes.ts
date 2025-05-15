import { Router } from "express";
import { signUpUser } from "./controllers/user-controller";

export const router = Router();

// Rota de cadastro
router.put("/signUp", signUpUser);
