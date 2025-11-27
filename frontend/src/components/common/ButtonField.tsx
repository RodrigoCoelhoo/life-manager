import { forwardRef, useImperativeHandle, useState, type JSX } from "react";
import type { Rule } from "../../rules/rules";

interface ButtonFieldProps {
	value: string;
	onClick: () => void;
	placeholder?: string;
	rules?: Rule[];
	icon?: JSX.Element;
	disabled?: boolean;
}

export const ButtonField = forwardRef(({ value, onClick, placeholder, rules = [], icon, disabled }: ButtonFieldProps, ref) => {
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

	return (
		<div className="flex flex-col w-full">
			<button
				type="button"
				onClick={onClick}
				className={`form-input flex ${!!icon ? "justify-between" : "justify-center"} items-center w-full ${disabled ? "cursor-not-allowed" : "cursor-pointer"} ${error ? "border-red-500" : ""}`}
				disabled={disabled}
			>
				<span className="truncate"> {value || placeholder} </span>
				{!!icon ? icon : <></>}
			</button>
			{error && <p className="text-red-500 text-sm mt-1">{error}</p>}
		</div>
	);
});
