import type { ChatMessage, HeadMessage } from "../modules/Common";
import type { Result } from "../modules/Request";
import { api, authorization } from "./Api";

const messageApiPettern = "/Api/Chat";

function chatApiUrl(url:string){
    return messageApiPettern + url;
}

export function FreshHeadMessage(headMessage:any,successCallback:(res:Result)=>void){
    api.post(chatApiUrl("/FreshHeadMessage"),authorization(),headMessage,successCallback);
}

export function CreateHeadMessage(headMessage:any,successCallback:(res:Result)=>void){
    api.put(chatApiUrl("/CreateHeadMessage"),authorization(),headMessage,successCallback);
}

export function CreateMessage(chatMessage:ChatMessage,successCallback:(res:Result)=>void){
    api.put(chatApiUrl("/CreateMessage"),authorization(),chatMessage,successCallback);
}

export function GetHeadMessages(userId:string,successCallback:(res:Result)=>void){
    api.get(chatApiUrl(`/GetHeadMessages/${userId}`),authorization(),successCallback);
}

export function GetMessages(currentPage:number,pageSize:number,userId:string,contactId:string,successCallback:(res:Result)=>void){
    api.get(chatApiUrl(`/GetMessages/${userId}/${contactId}?`+
        `page=${currentPage}&pageSize=${pageSize}`
    ),authorization(),successCallback);
}

export function CreateOfflineMessage(chatMessage:any){
    api.put(chatApiUrl("/CreateOfflineMessage"),authorization(),chatMessage,null);
}