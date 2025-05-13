import { Request, Response } from "express";
import { IUserModel } from "../models/user-model";
import { SignUpUserService } from "../services/user/signUpUser-service";
import { IHttpResponseModel } from "../models/httpResponse-model";
import { IUserLoginModel } from "../models/userLogin-model";
import { LoginUserService } from "../services/user/loginUser-service";

export const signUpUser = async (req: Request, res: Response) => {
    const user = req.body as IUserModel;
    let httpResponse: IHttpResponseModel;

    try {
        const signUpUserService = new SignUpUserService();

        httpResponse = await signUpUserService.execute(user);
    }

    catch (error) {
        console.log(error);

        res.status(400).json(error);
    }

    res.status(httpResponse!.statusCode).json(httpResponse!.body)
}

export const loginUser = async (req: Request, res: Response) => {
    const user = req.body as IUserLoginModel;
    let httpResponse: IHttpResponseModel;

    try {
        const loginUserService = new LoginUserService();

        httpResponse = await loginUserService.execute(user);

        return res.status(httpResponse.statusCode).json(httpResponse.body);
    }

    catch (error) {
        console.log(error);

        return res.status(400).json(error)
    }
}