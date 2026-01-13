import { useEffect, useRef, useState } from "react";
import { useAuth } from "../contexts/AuthContext";
import { NavLink } from "react-router-dom";
import { emailRules, passwordRules, personalNameRules, usernameRules } from "../rules/rules";
import { userService } from "../services/user/user.service";
import { InputField } from "../components/common/InputField";
import type { UserDTO } from "../services/user/user.dto";
import toast from "react-hot-toast";

export default function Profile() {
	const { logout } = useAuth();

	const [formData, setFormData] = useState<UserDTO>({
		firstName: "",
		lastName: "",
		username: "",
		email: "",
		passwordConfirmation: ""
	});

	const [loading, setLoading] = useState(false);
	const [message, setMessage] = useState("");
	const [isEditing, setIsEditing] = useState(false);
	const [originalData, setOriginalData] = useState<UserDTO | null>(null);

	const firstNameRef = useRef<{ validate: () => boolean }>(null);
	const lastNameRef = useRef<{ validate: () => boolean }>(null);
	const usernameRef = useRef<{ validate: () => boolean }>(null);
	const emailRef = useRef<{ validate: () => boolean }>(null);
	const passwordRef = useRef<{ validate: () => boolean }>(null);

	useEffect(() => {
		const fetchProfile = async () => {
			try {
				const profile = await userService.getProfile();
				setFormData({
					firstName: profile.firstName,
					lastName: profile.lastName,
					username: profile.username,
					email: profile.email,
					passwordConfirmation: ""
				});
			} catch (err: any) {
				setMessage(err.message);
			}
		};
		fetchProfile();
	}, []);

	const handleUpdate = async () => {
		const isValid =
			firstNameRef.current?.validate() &&
			lastNameRef.current?.validate() &&
			usernameRef.current?.validate() &&
			emailRef.current?.validate() &&
			(!isEditing || passwordRef.current?.validate());

		if (!isValid) return;

		setLoading(true);
		setMessage("");

		try {
			const updated = await userService.updateProfile(formData);
			setFormData({
				firstName: updated.firstName,
				lastName: updated.lastName,
				username: updated.username,
				email: updated.email,
				passwordConfirmation: ""
			});
			setMessage("Profile updated successfully!");
			setTimeout(() => {
				setMessage("");
			}, 5000);
			setIsEditing(false);

			if (originalData?.username !== formData.username) {
				toast("Authentication credentials changed. You will be logged out", {
					icon: "⚠️",
					style: { border: "2px solid #FBBF24", backgroundColor: "#282841", color: "#f9fafb" }
				});


				setTimeout(() => logout(), 4000);
			}
		} catch (err: any) {
			setMessage(err.message);
		}

		setLoading(false);
	};

	return (
		<div className="min-h-screen bg-background flex flex-col items-center justify-center py-12 px-8 text-textcolor">
			<h1 className="flex w-full justify-center gap-1 md:justify-start 2xl:w-300 px-8 text-2xl md:text-4xl font-bold mb-8">Welcome,<span className="text-secondary">{formData.firstName} {formData.lastName}</span></h1>
			<div className="bg-foreground shadow-lg rounded-3xl w-full 2xl:w-300 p-8">
				<div className="flex flex-col gap-6">

					{message && (
						<div className="flex  text-sm text-secondary font-medium">
							{message}
						</div>
					)}
					<div className="flex flex-col">
						<label className="text-sm mb-1">Username</label>
						<InputField
							ref={usernameRef}
							value={formData.username}
							onChange={(val) => setFormData({ ...formData, username: val })}
							placeholder="Username"
							rules={usernameRules()}
							disabled={!isEditing}
						/>
					</div>

					<div className="flex flex-row gap-8">
						<div className="flex flex-col w-full">
							<label className="text-sm mb-1">First Name</label>
							<InputField
								ref={firstNameRef}
								value={formData.firstName}
								onChange={(val) => setFormData({ ...formData, firstName: val })}
								placeholder="First Name"
								rules={personalNameRules()}
								disabled={!isEditing}
							/>
						</div>
						<div className="flex flex-col w-full">
							<label className="text-sm mb-1">Last Name</label>
							<InputField
								ref={lastNameRef}
								value={formData.lastName}
								onChange={(val) => setFormData({ ...formData, lastName: val })}
								placeholder="Last Name"
								rules={personalNameRules()}
								disabled={!isEditing}
							/>
						</div>
					</div>

					<div className="flex flex-col">
						<label className="text-sm mb-1">Email</label>
						<InputField
							ref={emailRef}
							value={formData.email}
							onChange={(val) => setFormData({ ...formData, email: val })}
							placeholder="Email"
							rules={emailRules()}
							type="email"
							disabled={!isEditing}
						/>
					</div>

					{isEditing && (
						<>
							<div className="border border-primary/40 my-4"></div>

							<div className="flex flex-col">
								<label className="text-sm mb-1">Password Confirmation</label>
								<InputField
									ref={passwordRef}
									value={formData.passwordConfirmation}
									onChange={(val) => setFormData({ ...formData, passwordConfirmation: val })}
									placeholder="Password"
									type="password"
									rules={passwordRules()}
								/>
							</div>
						</>
					)}

					{!isEditing ? (
						<div className="mt-8 pt-6 border-t border-gray-700">
							<div className="flex flex-col md:flex-row justify-between items-center gap-4">
								<div className="flex flex-col md:flex-row gap-3 w-full md:w-auto">
									<button
										type="button"
										onClick={() => {
											setOriginalData(formData);
											setIsEditing(true);
										}}
										className="cursor-pointer px-6 py-3 rounded-lg bg-primary hover:bg-primary/90 text-white font-medium transition-all duration-200 shadow-md hover:shadow-lg flex items-center justify-center gap-2"
									>
										<svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
											<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
										</svg>
										Edit Profile
									</button>

									<NavLink
										to="/change-password"
										className="cursor-pointer px-6 py-3 rounded-lg bg-secondary/90 hover:bg-secondary text-white font-medium transition-all duration-200 shadow-md hover:shadow-lg flex items-center justify-center gap-2"
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
									</NavLink>
								</div>

								<div className="w-full md:w-auto">
									<button
										onClick={logout}
										className="cursor-pointer px-6 py-3 rounded-lg bg-linear-to-r from-red-500 to-red-600 hover:from-red-600 hover:to-red-700 text-white font-medium transition-all duration-200 shadow-md hover:shadow-lg w-full flex items-center justify-center gap-2 group"
									>
										<svg className="w-5 h-5 group-hover:rotate-12 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
											<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
										</svg>
										Logout
									</button>
								</div>
							</div>

							<div className="mt-4 text-center text-sm text-gray-400">
								<p>Click "Edit Profile" to update your personal information</p>
							</div>
						</div>
					) : (
						<div className="flex flex-row items-center justify-end w-full gap-4 mt-8">
							<button
								type="button"
								onClick={() => {
									if (originalData) setFormData(originalData);
									setIsEditing(false);
									setOriginalData(null);
								}}
								className="cursor-pointer px-6 py-3 rounded-lg border border-gray-600 hover:border-red-500 text-gray-300 hover:text-red-400 font-medium transition-all duration-200 flex items-center gap-2"
							>
								<svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
									<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
								</svg>
								Cancel
							</button>

							<button
								type="button"
								onClick={handleUpdate}
								disabled={loading}
								className="cursor-pointer px-6 py-3 rounded-lg bg-linear-to-r from-green-500 to-emerald-600 hover:from-green-600 hover:to-emerald-700 text-white font-medium transition-all duration-200 shadow-md hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
							>
								{loading ? (
									<>
										<svg className="animate-spin h-5 w-5" viewBox="0 0 24 24">
											<circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
											<path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
										</svg>
										Updating...
									</>
								) : (
									<>
										<svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
											<path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
										</svg>
										Update Profile
									</>
								)}
							</button>
						</div>
					)}
				</div>
			</div>
		</div>
	);
}