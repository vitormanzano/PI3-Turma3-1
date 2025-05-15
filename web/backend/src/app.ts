import express, { Request, Response } from "express";
import { router } from "./routes";
import path from "path";

export function createApp() {
    const app = express();

    app.use(express.json());
    app.use(express.static(path.join(__dirname, "../../frontend")));

    // Rota para servir a página de cadastro exatamente em /signUp
    app.get('/signUp', (req: Request, res: Response) => {
        res.sendFile(path.resolve(__dirname, "../../frontend/signUp/signup.html"));
    });

    app.get('/login', (req: Request, res: Response) => {
        res.sendFile(path.resolve(__dirname, "../../frontend/login/login.html"));
    });

    app.use("/", router);

    return app;
}
