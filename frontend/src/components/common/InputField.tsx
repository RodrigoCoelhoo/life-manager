import { useState, forwardRef, useImperativeHandle, type ChangeEvent } from "react";
import type { Rule } from "../../rules/rules";
import { IoEyeOffOutline, IoEyeOutline } from "react-icons/io5";

interface InputFieldProps {
	value: string;
	onChange: (val: string) => void;
	placeholder?: string;
	rules?: Rule[];
	type?: string;
	multiline?: boolean;
	disabled?: boolean;
}

export const InputField = forwardRef(({ value, onChange, rules = [], type = "text", placeholder, multiline, disabled }: InputFieldProps, ref) => {
	const [error, setError] = useState<string | null>(null);
	const [showPassword, setShowPassword] = useState(false);

	const isPassword = type === "password";

	const validate = () => {
		for (const rule of rules) {
			const result = rule(value);
			if (result !== true) {
				setError(result);
				return false;
			}
		}
		setError(null);
		return true;
	};

	useImperativeHandle(ref, () => ({
		validate
	}));

	const handleChange = (e: ChangeEvent<HTMLInputElement> | ChangeEvent<HTMLTextAreaElement>) => {
		const val = e.target.value;
		onChange(val);

		if (!rules) return;

		for (let rule of rules) {
			const result = rule(val);
			if (result !== true) {
				setError(result);
				return;
			}
		}
		setError(null);
	};

	return (
		<div className="flex flex-col">
			<div className="relative">

				{multiline ? (
					<textarea
						value={value}
						onChange={handleChange}
						placeholder={placeholder}
						className={`form-input ${error ? "border-red-500" : ""} w-full h-25`}
						disabled={disabled}
					/>
				) : (
					<input
						type={
							isPassword && showPassword ? "text" : type
						}
						value={value}
						placeholder={placeholder}
						onChange={handleChange}
						className={`form-input ${error ? "border-red-500" : ""} w-full ${disabled ? "text-textcolor/80" : ""}`}
						disabled={disabled}
					/>
				)}

				{isPassword && !multiline && (
					<button
						type="button"
						onClick={() =>
							setShowPassword((prev) => !prev)
						}
						className="absolute right-3 top-1/2 -translate-y-1/2 text-textcolor/60 hover:text-textcolor"
						tabIndex={-1}
						aria-label="Toggle password visibility"
					>
						{showPassword ? (
							<IoEyeOffOutline size={20} />
						) : (
							<IoEyeOutline size={20} />
						)}
					</button>
				)}
			</div>

			{error && <p className="text-red-500 text-sm mt-1">{error}</p>}
		</div>
	);
});
