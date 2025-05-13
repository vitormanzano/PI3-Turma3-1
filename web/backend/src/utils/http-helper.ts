import { IHttpResponseModel } from "../models/httpResponse-model";

export const ok = async (data: any): Promise<IHttpResponseModel> => {
    return {
        statusCode: 200,
        body: data
    };
};

export const created = async (): Promise<IHttpResponseModel> => {
    return {
        statusCode: 201,
        body: {
            message: "Successful"
        }
    };
}

export const noContent = async(data: any): Promise<IHttpResponseModel> => {
    return {
        statusCode: 204,
        body: data
    };
};

export const badRequest = async (data: any): Promise<IHttpResponseModel> => {
    return {
        statusCode: 400,
        body: data
    };
};

export const notFound = async (data: any): Promise<IHttpResponseModel> => {
    return {
        statusCode: 404,
        body: data
    };
};

export const serverError = async (error: Error): Promise<IHttpResponseModel> => {
    return  {
        statusCode: 500,
        body: error
    }
}

export const conflict =  async (error: Error): Promise<IHttpResponseModel> => {
    return {
        statusCode: 409,
        body: error
    }
}