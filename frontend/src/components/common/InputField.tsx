import { useState, forwardRef, useImperativeHandle, type ChangeEvent } from "react";
import type { Rule } from "../../rules/rules";

interface InputFieldProps {
	value: string;
	onChange: (val: string) => void;
	placeholder?: string;
	rules?: Rule[];
	type?: string;
	multiline?: boolean;
}

export const InputField = forwardRef(({ value, onChange, rules = [], type = "text", placeholder, multiline }: InputFieldProps, ref) => {
	const [error, setError] = useState<string | null>(null);

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
			{multiline ? (
				<textarea
					value={value}
					onChange={handleChange}
					placeholder={placeholder}
					className={`form-input ${error ? "border-red-500" : ""} w-full h-25`}
				/>
			) : (
				<input
					type={type}
					value={value}
					placeholder={placeholder}
					onChange={handleChange}
					className={`form-input ${error ? "border-red-500" : ""} w-full`}
				/>
			)}
			{error && <p className="text-red-500 text-sm mt-1">{error}</p>}
		</div>
	);
});
