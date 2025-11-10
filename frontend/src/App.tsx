import {
	Route,
	createBrowserRouter,
	createRoutesFromElements,
	RouterProvider,
	Navigate,
} from 'react-router-dom'
import HomePage from './pages/HomePage';
import NotFound from './pages/NotFoundPage';
import Login from './pages/auth/LoginPage';
import SignUp from './pages/auth/SignUpPage';
import MainLayout from './layouts/MainLayout';
import { ProtectedRoute } from './components/ProtectedRoute';
import Dashboard from './pages/finances/Dashboard';

const router = createBrowserRouter(
	createRoutesFromElements(
		<>
			<Route element={<MainLayout />}>
				<Route path="/" element={<Navigate to="/home" replace />} />
				<Route path="/home" element={<HomePage />} />
				<Route path="*" element={<NotFound />} />
			</Route>

			{/* Quando houver login, dar redirect se o user estiver logado  */}
			<Route path="/login" element={<Login />} />
			<Route path="/signup" element={<SignUp />} />

			<Route element={<ProtectedRoute><MainLayout /></ProtectedRoute>}>
				<Route path="/dashboard" element={<Dashboard />} />
				{/*<Route path="/finances" element={<Finances />} />*/}
			</Route>
		</>
	)
);

export default function App() {
	return (
		<RouterProvider router={router} />
	);
};