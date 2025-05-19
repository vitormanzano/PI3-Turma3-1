import { Request, Response, NextFunction } from "express";
import { IUserModel } from "../models/user-model";
import { IUserLoginModel } from "../models/userLogin-model";
import { SignUpUserService } from "../services/user/signUpUser-service";
import { LoginUserService } from "../services/user/loginUser-service";

export const signUpUser = async (
  req: Request,
  res: Response,
  next: NextFunction
): Promise<void> => {
  try {
    const user = req.body as IUserModel;
    const signUpUserService = new SignUpUserService();
    const httpResponse = await signUpUserService.execute(user);

    res.status(httpResponse.statusCode).json(httpResponse.body);
  } 
  catch (error) {
    next(error);  // repassa erro para o middleware de tratamento de erro
  }
};

export const loginUser = async (
  req: Request,
  res: Response,
  next: NextFunction
): Promise<void> => {
  try {
    const user = req.body as IUserLoginModel;
    const loginUserService = new LoginUserService();
    const httpResponse = await loginUserService.execute(user);

    res.status(httpResponse.statusCode).json(httpResponse.body);
  } catch (error) {
    next(error);
  }
};