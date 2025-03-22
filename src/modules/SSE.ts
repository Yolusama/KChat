

const defaultOpenCallback = () => {
    console.log("SSE开启...")
};

class SSE {
    private sse: any;
    constructor() {
        
    }

    assign(userId:string){
        const sse = new EventSource(`http://localhost:5725/Api/Common/SSE/${userId}`);
        sse.onopen = defaultOpenCallback;
        sse.onerror = () => { }
        this.sse = sse;
    }

    assignMessageCallback(callback:(event:MessageEvent<any>)=>void){
        this.sse.onmessage = callback;
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