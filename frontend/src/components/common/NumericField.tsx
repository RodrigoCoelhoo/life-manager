import {
	useState,
	forwardRef,
	useImperativeHandle,
	type KeyboardEvent
} from "react";
import type { Rule } from "../../rules/rules";

interface NumericFieldProps {
	value: string;
	onChange: (val: string) => void;
	placeholder?: string;
	rules?: Rule[];
	formatter?: (val: string) => string;
	disabled?: boolean;
}

export const NumericField = forwardRef(function NumericField(
	{ value, onChange, rules = [], placeholder = "0.00", formatter = (v) => (v), disabled }: NumericFieldProps,
	ref
) {
	const [error, setError] = useState<string | null>(null);

	const validate = () => {
		for (const rule of rules) {
			const res = rule(value);
			if (res !== true) {
				setError(res);
				return false;
			}
		}
		setError(null);
		return true;
	};

	useImperativeHandle(ref, () => ({
		validate,
	}));

	const handleKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
		if (/^\d$/.test(e.key)) {
			e.preventDefault();

			const digits = value.replace(/\D/g, "") + e.key;
			const num = Number(digits) / 100;
			const formatted = num.toFixed(2);

			onChange(formatted);
			runRules(formatted);
			return;
		}

		if (e.key === "Backspace") {
			e.preventDefault();

			const rawDigits = value.replace(/\D/g, "").slice(0, -1);
			const num = Math.max(Number(rawDigits || "0") / 100, 0);
			const formatted = num.toFixed(2);

			onChange(formatted);
			runRules(formatted);
			return;
		}


		if (!["Tab", "ArrowLeft", "ArrowRight"].includes(e.key)) {
			e.preventDefault();
		}
	};


	const runRules = (val: string) => {
		for (const rule of rules) {
			const res = rule(val.trim());
			if (res !== true) {
				setError(res);
				return;
			}
		}
		setError(null);
	};

	return (
		<div className="flex flex-col w-full">
			<input
				type="text"
				value={formatter(value)}
				placeholder={placeholder}
				readOnly
				onKeyDown={handleKeyDown}
				className={`form-input w-full ${error ? "border-red-500" : ""} text-right ${disabled ? "cursor-not-allowed opacity-60" : "cursor-text"}`}
				disabled={disabled}
			/>

			{error && (
				<p className="text-red-500 text-sm mt-1">{error}</p>
			)}
		</div>
	);
});
