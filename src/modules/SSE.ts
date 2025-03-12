import stateStroge from "./StateStorage"

const user = stateStroge.get("user");

const defaultOpenCallback = () => {
    console.log("SSE开启...")
};

class SSE {
    private messageFuncs: Array<(event:MessageEvent<any>)=>void> = [];
    private sse: EventSource;
    constructor() {
        const sse = new EventSource(`http://localhost:5725/Api/Common/SSE/${user.id}`);
        sse.onopen = defaultOpenCallback;
        sse.onerror = () => { }
        sse.onmessage = (event:MessageEvent<any>)=>{
           for(let func of this.messageFuncs)
                func(event);
        };
        this.sse = sse;
    }

    addMsgFunc(func:(event:MessageEvent<any>)=>void){
        this.messageFuncs.push(func);
    }

    clearMsgFuncs(){
        const len = this.messageFuncs.length;
        this.messageFuncs.splice(0,len);
    }

    assignErrorCallback(callback:()=>void|null){
        this.sse.onerror = callback;
    }

    assignOpenCallback(callback:()=>void|null){
        this.sse.onopen = callback;
    }

    close(){
        this.sse.close();
    }
}

const sse = new SSE();
export default sse;