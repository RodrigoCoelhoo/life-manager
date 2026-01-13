import { useRef, useState } from "react";
import { NavLink } from "react-router-dom";
import { InputField } from "../../components/common/InputField";
import { passwordRules } from "../../rules/rules";
import { IoMdArrowRoundBack } from "react-icons/io";
import { userService } from "../../services/user/user.service";
import { useAuth } from "../../contexts/AuthContext";
import toast from "react-hot-toast";

export default function ChangePassword() {
	const { logout } = useAuth();

	const [submitting, setSubmitting] = useState<boolean>(false);

	const [newPassword, setPassword] = useState<string>("");
	const [confirmNewPassword, setConfirmPassword] = useState<string>("");
	const [passwordConfirmation, setPasswordConfirmation] = useState<string>("");
	const [error, setError] = useState<string | null>(null);

	const newPasswordRef = useRef<any>(null);
	const confirmNewPasswordRef = useRef<any>(null);
	const passwordConfirmationRef = useRef<any>(null);

	const submitForm = async (event: React.FormEvent<HTMLFormElement>) => {
		event.preventDefault();
		setError(null);

		const isNewPasswordValid = newPasswordRef.current?.validate();
		const isConfirmNewPasswordValid = confirmNewPasswordRef.current?.validate();
		const isPasswordConfirmationValid = passwordConfirmationRef.current?.validate();

		if (!isNewPasswordValid || !isConfirmNewPasswordValid || !isPasswordConfirmationValid) {
			return;
		}

		if (newPassword !== confirmNewPassword) {
			setError("Passwords don't match.");
			return;
		}

		setSubmitting(true);
		try {
			await userService.updatePassword({ newPassword, passwordConfirmation })

			toast("Authentication credentials changed. You will be logged out", {
				icon: "⚠️",
				style: { border: "2px solid #FBBF24", backgroundColor: "#282841", color: "#f9fafb" }
			});


			setTimeout(() => logout(), 4000);

		} catch (err: any) {
			console.error(err);
			setError("Current password is wrong.");
		} finally {
			setSubmitting(false);
		}
	};

	return (
		<section className="flex items-center justify-center min-h-screen bg-background text-textcolor">
			<div className="bg-foreground p-8 rounded-2xl shadow-lg w-full max-w-md">

				<div className="flex flex-row justify-between items-center mb-6">
					<NavLink
						to={"/profile"}
					>
						<IoMdArrowRoundBack size={24} />
					</NavLink>
					<h1 className="text-textcolor text-3xl font-semibold text-center tracking-tight cursor-default">
						Life<span className="text-primary">Manager</span>
					</h1>
					<div></div>
				</div>

				{error !== "Passwords don't match." && <p className="text-red-500 text-sm mb-2">{error}</p>}
				<form className="flex flex-col space-y-4" onSubmit={submitForm}>
					<div className="flex flex-col text-left">
						{error === "Passwords don't match." && <p className="text-red-500 text-sm mb-2">{error}</p>}
						<label htmlFor="password" className="text-sm mb-1">
							New Password
						</label>
						<InputField
							type="password"
							ref={newPasswordRef}
							value={newPassword}
							onChange={setPassword}
							placeholder="Enter a new password"
							rules={passwordRules()}
						/>
					</div>

					<div className="flex flex-col text-left">
						<label htmlFor="confirmPassword" className="text-sm mb-1">
							Confirm New Password
						</label>
						<InputField
							type="password"
							ref={confirmNewPasswordRef}
							value={confirmNewPassword}
							onChange={setConfirmPassword}
							placeholder="Re-enter the new password"
							rules={[
								(v: string) => v.trim().length > 0 || "This field cannot be blank"
							]}
						/>
					</div>

					<div className="border border-primary/20 mt-2 mb-4"></div>

					<div className="flex flex-col text-left">
						<label htmlFor="confirmPassword" className="text-sm mb-1">
							Current Password
						</label>
						<InputField
							type="password"
							ref={passwordConfirmationRef}
							value={passwordConfirmation}
							onChange={setPasswordConfirmation}
							placeholder="Enter your current password"
							rules={[
								(v: string) => v.trim().length > 0 || "This field cannot be blank"
							]}
						/>
					</div>

					<button
						type="submit"
						className="cursor-pointer mt-4 px-6 py-3 rounded-lg bg-primary/90 hover:bg-primary text-white font-medium transition-all duration-200 shadow-md hover:shadow-lg flex items-center justify-center gap-2"
						disabled={submitting}
					>
						<svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
							<path
								strokeLinecap="round"
								strokeLinejoin="round"
								strokeWidth={2}
								d="M15 7a2 2 0 012 2m4 0a6 6 0 01-7.743 5.743L11 17H9v2H7v2H4a1 1 0 01-1-1v-2.586a1 1 0 01.293-.707l5.964-5.964A6 6 0 1121 9z"
							/>
						</svg>
						Change Password
					</button>
				</form>
			</div>
		</section>
	);
}
