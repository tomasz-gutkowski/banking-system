import {statuses} from "../../api/account.ts";

async function accountSelectionPanelLoader(){
    try{
        const res = await statuses();
        console.log(res.data);
    }catch(err){

    }
}

export default accountSelectionPanelLoader;