import {createBrowserRouter, RouterProvider} from 'react-router';
import LoginPage from "./auth/LoginPage.tsx";
import RegisterPage from "./auth/RegisterPage.tsx";
import AccountSelectionPanel from "./user/AccountSelectionPanel.tsx";
import accountSelectionPanelLoader from "./user/components/accountSelectionPanel.loader.ts";


const router = createBrowserRouter([
    {
        path: "/login",
        element: <LoginPage/>
    },
    {
        path: "/register",
        element: <RegisterPage/>
    },
    {
        path: "/accounts",
        element: <AccountSelectionPanel/>,
        loader: accountSelectionPanelLoader
    },
    {
        path: "/admin",
        element: <div>placeholder - admin</div>
    }
])
function App() {
    return(
        <RouterProvider router={router}/>
    );
}

export default App;