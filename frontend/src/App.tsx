import {createBrowserRouter, RouterProvider} from 'react-router';
import LoginPage from "./auth/LoginPage.tsx";
import RegisterPage from "./auth/RegisterPage.tsx";
import UserPanel from "./user/UserPanel.tsx";


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
        path: "/panel",
        element: <UserPanel/>
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