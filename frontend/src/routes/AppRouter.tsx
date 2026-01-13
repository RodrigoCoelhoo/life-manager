import {
	Route,
	createBrowserRouter,
	createRoutesFromElements,
	RouterProvider,
	Navigate,
} from 'react-router-dom'
import HomePage from '../pages/HomePage';
import NotFound from '../pages/NotFoundPage';
import Login from '../pages/auth/LoginPage';
import SignUp from '../pages/auth/SignUpPage';
import HomeLayout from '../layouts/HomeLayout';
import MainLayout from '../layouts/MainLayout';
import { ProtectedRoute } from './ProtectedRoute';
import Wallets from '../pages/finances/Wallets';
import Transactions from '../pages/finances/Transactions';
import Transferences from '../pages/finances/Transferences';
import Ingredients from '../pages/nutrition/Ingredients';
import Recipes from '../pages/nutrition/Recipes';
import Meals from '../pages/nutrition/Meals';
import Exercises from '../pages/training/Exercises';
import TrainingPlans from '../pages/training/TrainingPlans';
import TrainingSessions from '../pages/training/TrainingSessions';
import Profile from '../pages/Profile';
import Bills from '../pages/finances/Bills';
import FinancesDashboard from '../pages/finances/Dashboard';
import NutritionDashboard from '../pages/nutrition/Dashboard';
import TrainingDashboard from '../pages/training/Dashboard';
import ChangePassword from '../pages/auth/ChangePassword';

const router = createBrowserRouter(
	createRoutesFromElements(
		<>
			<Route element={<HomeLayout />}>
				<Route path="/" element={<Navigate to="/home" replace />} />
				<Route path="/home" element={<HomePage />} />
				<Route path="*" element={<NotFound />} />
			</Route>

			<Route path="/login" element={<Login />} />
			<Route path="/signup" element={<SignUp />} />
			<Route path="/change-password" element={<ProtectedRoute><ChangePassword /></ProtectedRoute>} />

			<Route element={<ProtectedRoute><MainLayout /></ProtectedRoute>}>
				<Route path="/finances-dashboard" element={<FinancesDashboard />} />
				<Route path="/wallets" element={<Wallets />} />
				<Route path="/transactions" element={<Transactions />} />
				<Route path="/transferences" element={<Transferences />} />
				<Route path='/bills' element={<Bills />} />

				<Route path="/nutrition-dashboard" element={<NutritionDashboard />} />
				<Route path="/ingredients" element={<Ingredients />} />
				<Route path="/recipes" element={<Recipes />} />
				<Route path="/meals" element={<Meals />} />

				<Route path="/training-dashboard" element={<TrainingDashboard />} />
				<Route path="/exercises" element={<Exercises />} />
				<Route path="/training-plans" element={<TrainingPlans />} />
				<Route path="/training-sessions" element={<TrainingSessions />} />

				<Route path="/profile" element={<Profile />} />
			</Route>
		</>
	)
);

export default function App() {
	return (
		<RouterProvider router={router} />
	);
};