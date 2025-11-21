import { useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { authService } from "../../services/auth/authService";
import { InputField } from "../../components/common/InputField";
import { emailRules, personalNameRules, passwordRules, usernameRules } from "../../rules/rules";

export default function Login() {
	const [username, setUsername] = useState<string>("");
	const [password, setPassword] = useState<string>("");
	const [confirmPassword, setConfirmPassword] = useState<string>("");
	const [email, setEmail] = useState<string>("");
	const [firstName, setFirstName] = useState<string>("");
	const [lastName, setLastName] = useState<string>("");
	const [error, setError] = useState<string | null>(null);

	const usernameRef = useRef<any>(null);
	const emailRef = useRef<any>(null);
	const passwordRef = useRef<any>(null);
	const firstNameRef = useRef<any>(null);
	const lastNameRef = useRef<any>(null);
	const confirmPasswordRef = useRef<any>(null);

	const navigate = useNavigate();

	const submitForm = async (event: React.FormEvent<HTMLFormElement>) => {
		event.preventDefault();
		setError(null);

		const isUsernameValid = usernameRef.current?.validate();
		const isEmailValid = emailRef.current?.validate();
		const isPasswordValid = passwordRef.current?.validate();
		const isConfirmPasswordValid = confirmPasswordRef.current?.validate();
		const isFirstNameValid = firstNameRef.current?.validate();
		const isLastNameValid = lastNameRef.current?.validate();

		if (!isUsernameValid || !isEmailValid || !isPasswordValid || !isConfirmPasswordValid || !isFirstNameValid || !isLastNameValid) {
			return;
		}

		if (password !== confirmPassword) {
			setError("Passwords don't match.");
			return;
		}

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
				message = Object.values(apiMessage).join("\n");
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

				{error !== "Passwords don't match." && <p className="text-red-500 text-sm mb-2">{error}</p>}
				<form className="flex flex-col space-y-4" onSubmit={submitForm}>
					<div className="flex flex-col text-left">
						<label htmlFor="username" className="text-sm mb-1">
							Username
						</label>
						<InputField
							ref={usernameRef}
							value={username}
							onChange={setUsername}
							placeholder="Enter your username"
							rules={usernameRules()}
							type="text"
						/>
					</div>

					<div className="flex flex-row gap-4 w-full">
						<div className="flex flex-col flex-1">
							<label htmlFor="firstName" className="text-sm mb-1">First Name</label>
							<InputField
								ref={firstNameRef}
								value={firstName}
								onChange={setFirstName}
								placeholder="Enter your first name"
								rules={personalNameRules()}
								type="text"
							/>
						</div>

						<div className="flex flex-col flex-1">
							<label htmlFor="lastName" className="text-sm mb-1">Last Name</label>
							<InputField
								ref={lastNameRef}
								value={lastName}
								onChange={setLastName}
								placeholder="Enter your last name"
								rules={personalNameRules()}
								type="text"
							/>
						</div>
					</div>


					<div className="flex flex-col text-left">
						<label htmlFor="email" className="text-sm mb-1">
							Email
						</label>
						<InputField
							type="email"
							ref={emailRef}
							value={email}
							onChange={setEmail}
							placeholder="Enter your email"
							rules={emailRules()}
						/>
					</div>

					<div className="flex flex-col text-left">
						<label htmlFor="password" className="text-sm mb-1">
							Password
						</label>
						<InputField
							type="password"
							ref={passwordRef}
							value={password}
							onChange={setPassword}
							placeholder="Password"
							rules={passwordRules()}
						/>
					</div>

					<div className="flex flex-col text-left">
						<label htmlFor="confirmPassword" className="text-sm mb-1">
							Confirm Password
						</label>
						<InputField
							type="password"
							ref={confirmPasswordRef}
							value={confirmPassword}
							onChange={setConfirmPassword}
							placeholder="Confirm your password"
						/>
						{error === "Passwords don't match." && <p className="text-red-500 text-sm mb-2">{error}</p>}
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
