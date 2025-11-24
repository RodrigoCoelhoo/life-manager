import { Link } from 'react-router-dom'
import { useAuth } from "../contexts/AuthContext"
import { authService } from '../services/auth/authService';

export default function Profile() {
	const { logout, login } = useAuth();

	const handleReLogin = async () => {
		const { token } = await authService.signin({ username: 'RodrigoCoelho', password: 'Rodrigo-123' });
		logout();
		login(token);
	}

	return (
		<div className="h-full w-full p-6 text-textcolor text-4xl rounded-lg">
			<div className="bg-foreground flex flex-col justify-center items-center rounded-lg col-span-1 h-full gap-10">
				<h1 className='menu-title text-5xl'>Profile</h1>
				<Link
					to={"/home"}
					className='bg-background py-3 px-8 rounded-2xl border-2 border-secondary'
					onClick={logout}
				>
					Logout
				</Link>

				<button
					className='form-submit p-2'
					onClick={handleReLogin}	
				>
					ReLogin
				</button>
			</div>

		</div>
	);
}
