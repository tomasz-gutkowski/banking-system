import {getToken} from "../api/auth.ts";


async function userPanelLoader() {
    const token = getToken();
    if(!token)
    return null;
}

export default userPanelLoader;