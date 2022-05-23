export class AppNotification {
    level:string;
    message:string;
    title:string;

    constructor(level:string,message:string,title:string = ''){
        this.level = level;
        this.message = message;
        this.title = title;
    }
}