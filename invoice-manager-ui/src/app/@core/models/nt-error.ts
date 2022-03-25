export class NtError implements Error{

    readonly name = "NtError";
    readonly message: string;
    readonly error: any | null;


    constructor(message:string, error?:any){
        this.message = message;
        this.error = error;
    }
    
}