import {useState} from "react";
import {register} from "../api/auth.ts";
import {useNavigate} from "react-router";
import {isAxiosError} from "axios";
import type {ExceptionResponse, RegisterResponse} from "../Types.ts";
import Alert from "./reusable/Alert.tsx";

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

    registerArea: `w-full
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
    focus:outline-none
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

function RegisterPage(){
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [passwordRepeat, setPasswordRepeat] = useState("");
    const navigate = useNavigate();

    const [alertMessages, setAlertMessages] = useState<string[]>([]);
    const [alertType, setAlertType] = useState<"error" | "success">("error");
    const [displayAlert, setDisplayAlert] = useState<boolean>(false);

    const alert = <Alert messages={alertMessages} type={alertType}></Alert>

    function validateForm() : string[]{
        const errors : string[] = [];
        if(!username.trim()) errors.push("Username is required");
        if(!password.trim()) errors.push("Password is required");
        else if(password !== passwordRepeat) errors.push("Passwords must match");
        return errors;
    }

    async function handleRegister() {
        setAlertMessages([]);
        setDisplayAlert(false);
        const formErrors = validateForm();
        if(formErrors.length > 0){
            setAlertMessages(formErrors);
            setDisplayAlert(true);
            return;
        }

        try {
            const res = await register({ username, password });
            handleSuccess(res.data as RegisterResponse);
        } catch (err) {
            if(isAxiosError(err)){
                switch(err.response?.status){
                    case 409:{
                        handle409(err.response.data as ExceptionResponse);
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

    function handleSuccess(response : RegisterResponse){
        setAlertMessages([response.message, "Redirecting to login..."]);
        setAlertType("success");
        setDisplayAlert(true);
        setTimeout(() => navigate("/login"), 5000);
    }

    function handle409(response : ExceptionResponse){
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
                <div className={styles.registerArea}>
                    <h4 className={styles.h4}>
                        REGISTER
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
                    />

                    <label className={styles.label}>
                        Repeat password
                    </label>
                    <input className={styles.input}
                           type={"password"}
                           onChange={(e) => setPasswordRepeat(e.target.value)}
                    />

                    <button className={styles.button}
                        onClick={() => {handleRegister()}}>
                        Register
                    </button>

                    <a className={styles.a} onClick={() => navigate("/login")}>
                        Sign in
                    </a>

                </div>
                    {displayAlert ? alert : <div></div>}
            </div>)
}





export default RegisterPage;