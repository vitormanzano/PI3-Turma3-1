import { Request, Response } from "express";
import { IUserModel } from "../models/user-model";
import { SignUpUserService } from "../services/user/signup-service";
import { IHttpResponseModel } from "../models/httpResponse-model";

export const signUpUser = async (req: Request, res: Response) => {
    const user = req.body as IUserModel;
    let httpResponse: IHttpResponseModel;

    try {
        const signUpUserService = new SignUpUserService();

        httpResponse = await signUpUserService.execute(user);

        return res.status(httpResponse.statusCode).json(httpResponse.body)
    }

    catch (error) {
        console.log(error);

        return res.status(400).json(error);

    }

}