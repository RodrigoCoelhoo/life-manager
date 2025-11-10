import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { authService } from "../../services/auth/authService";
import { useAuth } from "../../contexts/AuthContext";

export default function Login() {
	const [username, setUsername] = useState<string>("");
	const [password, setPassword] = useState<string>("");
	const [error, setError] = useState<string | null>(null);
	
	const { login } = useAuth();
	const navigate = useNavigate(); // for redirect after login

	const submitForm = async (event: React.FormEvent<HTMLFormElement>) => {
		event.preventDefault();
		setError(null);

		try {
			const { token } = await authService.signin({ username, password });~
			login(token);
			navigate("/dashboard");
		} catch (err: any) {
			console.error(err);
			setError("Invalid credentials");
		}
	};

	return (
		<section className="flex items-center justify-center min-h-screen bg-background text-textcolor">
			<div className="bg-foreground p-8 rounded-2xl shadow-lg w-full max-w-md">
				<h1 className="text-3xl font-semibold mb-6 text-center">
					Welcome Back
				</h1>

				{error && <p className="text-red-500 text-sm mb-2">{error}</p>}
				<form className="flex flex-col space-y-4" onSubmit={submitForm}>
					<div className="flex flex-col text-left">
						<label htmlFor="username" className="text-sm mb-1">
							Username
						</label>
						<input
							type="text"
							id="username"
							name="username"
							placeholder="Enter your username"
							className="p-2 border border-secondary/50 rounded-lg bg-background focus:outline-none focus:border-primary/80"
							required
							value={username}
							onChange={(event) => setUsername(event.target.value)}
						/>
					</div>

					<div className="flex flex-col text-left">
						<label htmlFor="password" className="text-sm mb-1">
							Password
						</label>
						<input
							type="password"
							id="password"
							name="password"
							placeholder="Enter your password"
							className="p-2 border border-secondary/50 rounded-lg bg-background focus:outline-none focus:border-primary/80"
							required
							value={password}
							onChange={(event) => setPassword(event.target.value)}
						/>
					</div>

					<button
						type="submit"
						className="mt-4 bg-primary text-white py-2 rounded-lg hover:bg-primary/90 transition-all"
					>
						Log In
					</button>
				</form>

				<p className="text-sm text-center mt-6">
					Don't have an account?{" "}
					<Link
						to="/signup"
						className="text-primary hover:underline"
					>
						Sign Up
					</Link>
				</p>
			</div>
		</section>
	);
}
