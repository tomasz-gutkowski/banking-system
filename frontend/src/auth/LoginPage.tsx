import {useState} from "react";
import {useNavigate} from "react-router";
import {isAxiosError} from "axios";
import type {ExceptionResponse, JwtClaims, LoginResponse} from "../Types.ts";
import Alert from "./reusable/Alert.tsx";
import {jwtDecode} from "jwt-decode";
import {login, setToken} from "../api/auth.ts";

const styles = {
    page: `w-full 
    h-screen
    flex 
    flex-col
    items-center
    justify-center
    font-default
    bg-bg1
    text-t1`,

    loginArea: `w-full
    h-4/5
    flex
    flex-col
    items-center
    justify-center
    `,

    h4: `
    text-t1
    text-8xl
    font-bold
    mb-4`,

    label:`
    mb-1
    text-2xl
    text-t2`,

    input:`
    bg-bg2
    hover:bg-bg2-hover
    mb-3
    px-2
    text-3xl
    border-0
    rounded-xl
    `,

    button:`
    bg-bg1
    border-brdr1
    hover:bg-bg1-hover
    hover:border-brdr1-hover
    hover:cursor-pointer
    text-t1 
    px-4 
    py-1 
    text-2xl 
    border-3 
    rounded-2xl 
    `,

    a:`hover:cursor-pointer
    text-t2
    pt-2`
}

function LoginPage(){
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const [alertMessages, setAlertMessages] = useState<string[]>([]);
    const [alertType, setAlertType] = useState<"error" | "success">("error");
    const [displayAlert, setDisplayAlert] = useState<boolean>(false);

    const alert = <Alert messages={alertMessages} type={alertType}></Alert>

    const navigate = useNavigate();

    async function handleLogin() {
        setDisplayAlert(false)
        setAlertMessages([]);
        try {
            const res = await login({username, password});
            const data = res.data as LoginResponse;
            const jwt = data.jwt;

            if(jwt == null) throw new Error("Auth token missing");
            else setToken(jwt);

            const claims = jwtDecode<JwtClaims>(jwt);
            if(claims.roles.includes("ROLE_USER")){
                navigate("/accounts")
            } else if(claims.roles.includes("ROLE_ADMIN")){
                navigate("/admin")
            }
        } catch (err) {
            if(isAxiosError(err)){
                switch(err.response?.status){
                    case 401:{
                        handle401(err.response.data as ExceptionResponse);
                        break;
                    }
                    case 403:{
                        handle403(err.response.data as ExceptionResponse);
                        break;
                    }
                    case 422:{
                        handle422(err.response.data as {username? : string, password? : string});
                        break;
                    }
                    case 429:{
                        handle429(err.response.data as ExceptionResponse);
                        break;
                    }
                }
            } else{
                setAlertMessages(["Unexpected error"]);
                setAlertType("error");
                setDisplayAlert(true);
            }
        }
    }


    function handle401(response : ExceptionResponse){
        setAlertMessages([response.message]);
        setAlertType("error");
        setDisplayAlert(true);
    }

    function handle403(response : ExceptionResponse){
        setAlertMessages([response.message]);
        setAlertType("error");
        setDisplayAlert(true);
    }

    function handle422(response: {username? : string, password? : string}) {
        const messages : string[] = [];
        if(response.username) messages.push(response.username);
        if(response.password) messages.push(response.password);
        setAlertMessages(messages);
        setAlertType("error");
        setDisplayAlert(true);
    }

    function handle429(response : ExceptionResponse){
        setAlertMessages([response.message]);
        setAlertType("error");
        setDisplayAlert(true);
    }

    return (<div className={styles.page}>
        <div className={styles.loginArea}>
        <h4 className={styles.h4}>
            SIGN-IN
        </h4>

        <label className={styles.label}>
            Username
        </label>
        <input className={styles.input}
               type={"text"}
               onChange={(e) => setUsername(e.target.value)}
        />

        <label className={styles.label}>
            Password
        </label>
        <input className={styles.input}
               type={"password"}
               onChange={(e) => setPassword(e.target.value)}
        /> <button className={styles.button}
                onClick={() => {handleLogin()}}>
            Log-in
        </button>

        <a className={styles.a} onClick={() => navigate("/register")}>
            Register
        </a>
    </div>
        {displayAlert ? alert : <div></div>}
    </div>)
}




export default LoginPage;