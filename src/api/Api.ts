import { Request, RequestAsync, type Result } from "../modules/Request";
import { ElMessage } from "element-plus";

class Api {
    private template(url: string, headers: Record<string, any>, type: string, data: any,
        successCallback: ((result: Result) => void) | null = null, failCallback: (() => void) | null = null) {
        Request(url, type, data, headers, response => {
            const res = response.data;
            if (!res.ok) {
                ElMessage({
                    message: res.message,
                    type: "error"
                });
                if (failCallback != null)
                    failCallback();
                return;
            }
            if (successCallback != null)
                successCallback({ message: res.message, data: res.data });
        });
    }

    get(url: string, headers: Record<string, any>, successCallback: ((result: Result) => void) | null = null,
        failCallback: (() => void) | null = null) {
        this.template(url, headers, "GET", null, successCallback, failCallback);
    }

    post(url: string, headers: Record<string, any>, data: any, successCallback: ((result: Result) => void) | null = null,
        failCallback: (() => void) | null = null) {
        this.template(url, headers, "POST", data, successCallback, failCallback);
    }

    delete(url: string, headers: Record<string, any>, successCallback: ((result: Result) => void) | null = null,
        failCallback: (() => void) | null = null) {
        this.template(url, headers, "DELETE", null, successCallback, failCallback);
    }

    put(url: string, headers: Record<string, any>, data: any, successCallback: ((result: Result) => void) | null = null,
        failCallback: (() => void) | null = null) {
        this.template(url, headers, "PUT", data, successCallback, failCallback);
    }

    patch(url: string, headers: Record<string, any>, data: any, successCallback: ((result: Result) => void) | null = null,
        failCallback: (() => void) | null = null) {
        this.template(url, headers, "PATCH", data, successCallback, failCallback);
    }

}

class AsyncApi {
    private async template(url: string, headers: Record<string, any>, type: string, data: any,
        successCallback: ((result: Result) => void) | null = null, failCallback: (() => void) | null = null) {
        const res = await RequestAsync(url, type, data, headers);
        if (!res.ok) {
            ElMessage({
                message: res.message,
                type: "error"
            });
            if (failCallback != null)
                failCallback();
            return;
        }
        if (successCallback != null)
            successCallback({ message: res.message, data: res.data });
    }

    async get(url: string, headers: Record<string, any>, successCallback: ((result: Result) => void) | null = null,
        failCallback: (() => void) | null = null) {
        await this.template(url, headers, "GET", null, successCallback, failCallback);
    }

    async post(url: string, headers: Record<string, any>, data: any, successCallback: ((result: Result) => void) | null = null,
        failCallback: (() => void) | null = null) {
        await this.template(url, headers, "POST", data, successCallback, failCallback);
    }

    async delete(url: string, headers: Record<string, any>, successCallback: ((result: Result) => void) | null = null,
        failCallback: (() => void) | null = null) {
        await this.template(url, headers, "DELETE", null, successCallback, failCallback);
    }

    async put(url: string, headers: Record<string, any>, data: any, successCallback: ((result: Result) => void) | null = null,
        failCallback: (() => void) | null = null) {
        await this.template(url, headers, "PUT", data, successCallback, failCallback);
    }

    async patch(url: string, headers: Record<string, any>, data: any, successCallback: ((result: Result) => void) | null = null,
        failCallback: (() => void) | null = null) {
        await this.template(url, headers, "PATCH", data, successCallback, failCallback);
    }
}

export const api = new Api();
export const asyncApi = new AsyncApi();