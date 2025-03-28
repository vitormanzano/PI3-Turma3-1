import express from "express";
import { router } from "./routes";

export function createApp() {
    const app = express();  //Cria uma aplicação express

    app.use(express.json()); //Diz que vai usar json

    app.use("/", router)

    return app;
}