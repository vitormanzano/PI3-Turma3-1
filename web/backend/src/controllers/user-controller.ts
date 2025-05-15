import { Request, Response, NextFunction} from "express";
import { IUserModel } from "../models/user-model";
import { SignUpUserService } from "../services/user/signup-service";
import { IHttpResponseModel } from "../models/httpResponse-model";

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
  } catch (error) {
    next(error);  // Use next para repassar erro para o middleware de erro do express
  }
};
