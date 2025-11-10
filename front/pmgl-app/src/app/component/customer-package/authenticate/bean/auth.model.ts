export interface AuthDTO{

    email : string;

    password : string;
}

export interface AuthDTOResponse {
  
    content: AuthDTO[]; 

    pageable: any;

    totalElements: number;

    totalPages: number;

    last: boolean;
    
    size: number;
}