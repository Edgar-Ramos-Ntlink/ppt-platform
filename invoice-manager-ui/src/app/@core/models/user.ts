export class User{

    public id: number;

    public activo:boolean;

    public email: string;

    public username: string;

    public password: string;

    public tipoUsuario: string;

    public multiempresas: boolean;

    public creador: number;

    public creacion: string;

    public actualizacion: string;

    constructor(username?:string){
        this.username = username;
    }
}