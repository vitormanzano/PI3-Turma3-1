import { Router } from "express";
import * as userController from "../src/controllers/user-controller";

export const router = Router(); 

router.post("/user/signUp", userController.signUpUser);
router.get("/user/login", userController.loginUser)