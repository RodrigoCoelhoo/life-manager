import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { authService } from "../../services/auth/authService";

export default function Login() {
	const [username, setUsername] = useState<string>("");
	const [password, setPassword] = useState<string>("");
	const [confirmPassword, setConfirmPassword] = useState<string>("");
	const [email, setEmail] = useState<string>("");
	const [firstName, setFirstName] = useState<string>("");
	const [lastName, setLastName] = useState<string>("");
	const [error, setError] = useState<string | null>(null);

	const navigate = useNavigate();

	const submitForm = async (event: React.FormEvent<HTMLFormElement>) => {
		event.preventDefault();
		setError(null);

		{/** Check if password match */ }
		try {
			await authService.signup({ username, firstName, lastName, email, password });
			navigate("/login");
		} catch (err: any) {
			console.error(err);

			const apiMessage = err.response?.data?.message;
			let message: string;

			if (typeof apiMessage === "string") {
				message = apiMessage;
			} else if (typeof apiMessage === "object" && apiMessage !== null) {
				message = Object.values(apiMessage).join(" ");
			} else {
				message = "Something went wrong.";
			}

			setError(message);
		}
	};

	return (
		<section className="flex items-center justify-center min-h-screen bg-background text-textcolor">
			<div className="bg-foreground p-8 rounded-2xl shadow-lg w-full max-w-md">
				<h1 className="text-textcolor text-3xl font-semibold mb-6 text-center tracking-tight cursor-default">
					Life<span className="text-primary">Manager</span>
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

					<div className="flex flex-row justify-between w-full gap-4">
						<div className="flex flex-col text-left w-1/2">
							<label htmlFor="firstName" className="text-sm mb-1">
								First Name
							</label>
							<input
								type="text"
								id="firstName"
								name="firstName"
								placeholder="Enter your first name"
								className="p-2 border border-secondary/50 rounded-lg bg-background focus:outline-none focus:border-primary/80 w-full text-sm"
								required
								value={firstName}
								onChange={(event) => setFirstName(event.target.value)}
							/>
						</div>

						<div className="flex flex-col text-left w-1/2">
							<label htmlFor="lastName" className="text-sm mb-1">
								Last Name
							</label>
							<input
								type="text"
								id="lastName"
								name="lastName"
								placeholder="Enter your last name"
								className="p-2 border border-secondary/50 rounded-lg bg-background focus:outline-none focus:border-primary/80 w-full text-sm"
								required
								value={lastName}
								onChange={(event) => setLastName(event.target.value)}
							/>
						</div>
					</div>

					<div className="flex flex-col text-left">
						<label htmlFor="email" className="text-sm mb-1">
							Email
						</label>
						<input
							type="email"
							id="email"
							name="email"
							placeholder="Enter your email"
							className="p-2 border border-secondary/50 rounded-lg bg-background focus:outline-none focus:border-primary/80"
							required
							value={email}
							onChange={(event) => setEmail(event.target.value)}
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

					<div className="flex flex-col text-left">
						<label htmlFor="confirmPassword" className="text-sm mb-1">
							Confirm Password
						</label>
						<input
							type="password"
							id="confirmPassword"
							name="confirmPassword"
							placeholder="Confirm your password"
							className="p-2 border border-secondary/50 rounded-lg bg-background focus:outline-none focus:border-primary/80"
							required
							value={confirmPassword}
							onChange={(event) => setConfirmPassword(event.target.value)}
						/>
					</div>

					<button
						type="submit"
						className="cursor-pointer mt-4 bg-primary text-white py-2 rounded-lg hover:bg-primary/90 transition-all"
					>
						Sign Up
					</button>
				</form>

				<p className="text-sm text-center mt-6">
					Already have an account?{" "}
					<Link
						to="/login"
						className="text-primary hover:underline"
					>
						Login
					</Link>
				</p>
			</div>
		</section>
	);
}
