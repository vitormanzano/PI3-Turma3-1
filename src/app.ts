import express from "express";
import { router } from "./routes";

export function createApp() {
    const app = express();

    app.use(express.json());

    app.use("/", router)

    return app;
}