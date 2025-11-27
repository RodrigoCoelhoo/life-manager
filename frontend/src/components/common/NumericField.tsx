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
}

export const NumericField = forwardRef(function NumericField(
	{ value, onChange, rules = [], placeholder = "0.00" }: NumericFieldProps,
	ref
) {
	const [error, setError] = useState<string | null>(null);

	// ----- Validation -----
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

			const digits = value.replace(/\D/g, "").slice(0, -1) || "0";
			const num = Number(digits) / 100;
			const formatted = num.toFixed(2);

			onChange(formatted);
			runRules(formatted);
			return;
		}

		if (!["Tab", "ArrowLeft", "ArrowRight"].includes(e.key)) {
			e.preventDefault();
		}
	};

	// ----- Inline rule validation -----
	const runRules = (val: string) => {
		for (const rule of rules) {
			const res = rule(val);
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
				value={value}
				placeholder={placeholder}
				readOnly
				onKeyDown={handleKeyDown}
				className={`form-input w-full ${error ? "border-red-500" : ""} text-right`}
			/>

			{error && (
				<p className="text-red-500 text-sm mt-1">{error}</p>
			)}
		</div>
	);
});
